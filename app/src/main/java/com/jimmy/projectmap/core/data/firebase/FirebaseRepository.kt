package com.jimmy.projectmap.core.data.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.jimmy.projectmap.core.model.Product
import com.jimmy.projectmap.core.data.InventoryRepository
import com.jimmy.projectmap.core.model.StockTransaction
import com.jimmy.projectmap.core.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

class FirebaseRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : InventoryRepository {

    private val collProducts = db.collection("products")
    private val collTransactions = db.collection("transactions")

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    override val products: StateFlow<List<Product>> = _products

    private var prodListener: ListenerRegistration? = null
    private var txnListener: ListenerRegistration? = null

    private fun DocumentSnapshot.safeLong(field: String): Long {
        val v = this.get(field)
        return when (v) {
            is Number -> v.toLong()
            is String -> v.toLongOrNull() ?: 0L
            else -> 0L
        }
    }

    // ---- Transaksi ----
    private val _transactions = MutableStateFlow<List<StockTransaction>>(emptyList())
    val transactions: StateFlow<List<StockTransaction>> = _transactions

    init {
        // listener transaksi (urut terbaru)
        collTransactions.orderBy("ts", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                _transactions.value = snap.documents.mapNotNull { it.toObject(StockTransaction::class.java)?.copy(id = it.id) }
            }

        // Live products
        prodListener = collProducts.addSnapshotListener { snap, err ->
            if (err != null || snap == null) return@addSnapshotListener
            val list = snap.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(
                    id = doc.safeLong("id")
                )
            }.sortedBy { it.name }
            _products.value = list
        }

        // Live transactions (seluruhnya; bisa difilter di UI)
        txnListener = collTransactions
            .orderBy("ts", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { doc ->
                    doc.toObject(StockTransaction::class.java)?.copy(id = doc.id)
                }
                _transactions.value = list
            }
    }

    override suspend fun saveProduct(p: Product): Long {
        val id = if (p.id == 0L) {
            val doc = collProducts.document()
            val newId = doc.id.hashCode().toLong()
            doc.set(p.copy(id = newId)).await()
            newId
        } else {
            collProducts.document(p.id.toString()).set(p).await()
            p.id
        }
        return id
    }

    override suspend fun deleteProduct(id: Long) {
        collProducts.document(id.toString()).delete().await()
    }

    override suspend fun getProduct(id: Long): Product? {
        val doc = collProducts.document(id.toString()).get().await()
        return if (doc.exists()) {
            doc.toObject(Product::class.java)?.copy(id = doc.safeLong("id"))
        } else null
    }

    suspend fun addTransaction(tx: StockTransaction) {
        db.runTransaction { tr ->
            val pRef = collProducts.document(tx.productId.toString())
            val pSnap = tr.get(pRef)
            val product = pSnap.toObject(Product::class.java) ?: throw IllegalStateException("Produk tidak ditemukan")

            val delta = when (tx.type) {
                TransactionType.IN -> kotlin.math.abs(tx.qty)
                TransactionType.OUT -> -kotlin.math.abs(tx.qty)
                TransactionType.ADJUST -> tx.qty
            }
            val newStock = (product.stock + delta).coerceAtLeast(0)

            tr.set(pRef, product.copy(stock = newStock))
            tr.set(collTransactions.document(), tx.copy(qty = delta))
            null
        }.await()
    }

    fun transactionsFor(productId: Long): Flow<List<StockTransaction>> =
        transactions.map { list -> list.filter { it.productId == productId } }

    fun cleanup() {
        prodListener?.remove()
        txnListener?.remove()
    }

}