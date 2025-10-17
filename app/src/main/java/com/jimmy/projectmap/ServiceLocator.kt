package com.jimmy.projectmap

object ServiceLocator {
    // default ke Firebase
    var repo: InventoryRepository = FirebaseRepository()
}