package com.jimmy.projectmap

enum class TransactionType { IN, OUT, ADJUST }

data class StockTransaction(
    val id: String = "",           // docId Firestore
    val productId: Long = 0L,
    val productName: String = "",
    val qty: Int = 0,              // IN: +, OUT: -, ADJUST: bebas (+/-)
    val type: TransactionType = TransactionType.IN,
    val note: String? = null,
    val ts: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
