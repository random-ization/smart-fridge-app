package com.example.myapplication.data

import java.time.LocalDate
import java.util.UUID

/**
 * Data model for a food item in the fridge
 */
data class FoodItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val expiryDate: String = LocalDate.now().plusDays(7).toString(),
    val area: String = "Uncategorized",
    val notes: String = "",
    val quantity: Int = 1
)

/**
 * Data model for a storage area/category
 */
data class StorageArea(
    val id: String = UUID.randomUUID().toString(),
    val name: String = ""
)

/**
 * Data model for a saved recipe
 */
data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val content: String = ""
)
