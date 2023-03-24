package com.kubrayildirim.aksampazari.data.model

import com.google.firebase.Timestamp


data class Product(
    val first_price: String,
    val last_price: String,
    val name: String,
    val restaurant_name: String,
    val photo_url: String,
    val timestamp: Timestamp,
) {}