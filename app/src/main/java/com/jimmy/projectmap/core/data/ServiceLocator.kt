package com.jimmy.projectmap.core.data

import com.jimmy.projectmap.core.data.firebase.FirebaseRepository

object ServiceLocator {
    // default ke Firebase
    var repo: InventoryRepository = FirebaseRepository()
}