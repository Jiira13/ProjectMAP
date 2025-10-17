package com.jimmy.projectmap

import kotlinx.coroutines.flow.StateFlow

interface InventoryRepository {
    val products: StateFlow<List<Product>>
    suspend fun saveProduct(p: Product): Long
    suspend fun deleteProduct(id: Long)
    suspend fun getProduct(id: Long): Product?
}
