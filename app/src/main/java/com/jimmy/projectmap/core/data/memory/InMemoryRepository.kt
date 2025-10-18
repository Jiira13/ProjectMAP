package com.jimmy.projectmap.core.data.memory

import com.jimmy.projectmap.core.model.Product
import com.jimmy.projectmap.core.data.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object InMemoryRepository : InventoryRepository {
    private val _products = MutableStateFlow(
        listOf(
            Product(
                id = 1,
                name = "Indomie Goreng",
                sku = "IDM-G",
                price = 3500,
                stock = 24,
                reorderPoint = 6
            ),
            Product(
                id = 2,
                name = "Aqua 600ml",
                sku = "AQA-600",
                price = 2500,
                stock = 18,
                reorderPoint = 8
            ),
            Product(
                id = 3,
                name = "Gulaku 1kg",
                sku = "GLK-1K",
                price = 14000,
                stock = 5,
                reorderPoint = 4
            )
        )
    )
    override val products: StateFlow<List<Product>> = _products
    private var nextId = 4L

    override suspend fun saveProduct(p: Product): Long {
        val id = if (p.id == 0L) nextId++ else p.id
        _products.value = _products.value
            .filterNot { it.id == id }
            .plus(p.copy(id=id))
            .sortedBy { it.name }
        return id
    }

    override suspend fun deleteProduct(id: Long) {
        _products.value = _products.value.filterNot { it.id == id }
    }

    override suspend fun getProduct(id: Long): Product? =
        _products.value.firstOrNull { it.id == id }
}