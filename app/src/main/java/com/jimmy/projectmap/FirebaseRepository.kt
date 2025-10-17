package com.jimmy.projectmap

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await


class FirebaseRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : InventoryRepository {

    private val collProducts = db.collection("products")
    private val collTransactions = db.collection("transactions")

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    override val products: StateFlow<List<Product>> = _products

    private var prodListener: ListenerRegistration? = null
    private var txnListener: ListenerRegistration? = null

    // ---- Transaksi ----
    private val _transactions = MutableStateFlow<List<StockTransaction>>(emptyList())
    val transactions: StateFlow<List<StockTransaction>> = _transactions

    init {
        // Live products
        prodListener = collProducts.addSnapshotListener { snap, err ->
            if (err != null || snap == null) return@addSnapshotListener
            val list = snap.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(
                    id = doc.getString("id")?.toLongOrNull()
                        ?: doc.getLong("id") ?: 0L
                )
            }.sortedBy { it.name }
            _products.value = list
        }

        // Live transactions (seluruhnya; bisa difilter di UI)
        txnListener = collTransactions
            .orderBy("ts", com.google.firebase.firestore.Query.Direction.DESCENDING)
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
        return if (doc.exists()) doc.toObject(Product::class.java)?.copy(id = id) else null
    }

    suspend fun addTransaction(tx: StockTransaction) {
        db.runTransaction { tr ->
            // 1) Ambil produk - FIXED: Use indexed accessor instead of get()
            val pRef = collProducts.document(tx.productId.toString())
            val pSnap = tr[pRef]  // Changed from tr.get(pRef)
            if (!pSnap.exists()) throw IllegalStateException("Produk tidak ditemukan")

            val product = pSnap.toObject(Product::class.java)
                ?: throw IllegalStateException("Produk invalid")
            val currentStock = product.stock
            val delta = when (tx.type) {
                TransactionType.IN -> kotlin.math.abs(tx.qty)
                TransactionType.OUT -> -kotlin.math.abs(tx.qty)
                TransactionType.ADJUST -> tx.qty
            }
            val newStock = (currentStock + delta).coerceAtLeast(0)

            // 2) Update stok
            tr.set(pRef, product.copy(stock = newStock))

            // 3) Tulis transaksi
            val tRef = collTransactions.document()
            tr.set(tRef, tx.copy(qty = delta))
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
