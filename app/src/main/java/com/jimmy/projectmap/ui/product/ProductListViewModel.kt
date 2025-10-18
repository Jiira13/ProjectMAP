package com.jimmy.projectmap.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.jimmy.projectmap.core.data.ServiceLocator

class ProductListViewModel : ViewModel() {
    private val repo = ServiceLocator.repo
    val productsLive = repo.products.asLiveData()
}
