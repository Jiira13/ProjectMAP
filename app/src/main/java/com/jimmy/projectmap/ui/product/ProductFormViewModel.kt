package com.jimmy.projectmap.ui.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.projectmap.core.data.ServiceLocator
import com.jimmy.projectmap.core.model.Product
import kotlinx.coroutines.launch

class ProductFormViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ServiceLocator.repo

    suspend fun load(id: Long) = repo.getProduct(id)

    fun save(
        id: Long? = null,
        name: String,
        sku: String?,
        price: Int,
        stock: Int,
        reorderPoint: Int
    ) = viewModelScope.launch {
        repo.saveProduct(
            Product(
                id = id ?: 0L,
                name = name.trim(),
                sku = sku?.trim(),
                price = price,
                stock = stock,
                reorderPoint = reorderPoint
            )
        )
    }
}
