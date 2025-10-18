package com.jimmy.projectmap.core.model

data class Product(
    val id: Long = 0L,
    val name: String = "",
    val sku: String? = null,
    val price: Int = 0,
    val stock: Int = 0,
    val reorderPoint: Int = 0,
    val imageUrl: String? = null
)