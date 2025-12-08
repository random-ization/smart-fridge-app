package com.example.myapplication.ai

import com.example.myapplication.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val generativeModel by lazy {
    GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
}

/**
 * AI Service for processing receipts and generating recipes
 */
object AIService {

    /**
     * Process OCR text from a receipt and extract food item names
     */
    suspend fun cleanReceiptData(rawText: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = content {
                    text(
                        "Analyze this Korean receipt OCR text:\n$rawText\n" +
                                "Extract ONLY food items, drinks, and condiments.\n" +
                                "Translate to English.\n" +
                                "Output comma-separated list ONLY. No numbers, prices, or extra words."
                    )
                }
                val response = generativeModel.generateContent(prompt)
                response.text?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Generate a full recipe for a given dish name
     */
    suspend fun generateFullRecipe(dishName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = content {
                    text(
                        "Create a practical cooking recipe for '$dishName'.\n" +
                                "Strictly follow this format in English:\n\n" +
                                "**Ingredients & Seasonings:**\n" +
                                "[List detailed ingredients and seasonings with quantities]\n\n" +
                                "**Missing/Key Ingredients:**\n" +
                                "[Mention main items needed]\n\n" +
                                "**Cooking Instructions:**\n" +
                                "[Detailed step-by-step guide, focusing on heat control, timing, and technique]\n\n" +
                                "IMPORTANT: Direct cooking steps only. Do NOT include history, cultural background, or introduction."
                    )
                }
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Could not generate recipe."
            } catch (e: Exception) {
                "Network Error: ${e.message}"
            }
        }
    }

    /**
     * Recommend a recipe based on available ingredients
     */
    suspend fun recommendSmartRecipe(inventory: List<String>): String {
        return withContext(Dispatchers.IO) {
            try {
                val items = inventory.joinToString(", ")
                val prompt = content {
                    text(
                        "I have these ingredients: $items. \n" +
                                "Task: Recommend ONE best dish I can make.\n" +
                                "Strict Output Format (English):\n\n" +
                                "**Dish Name:** [Name]\n\n" +
                                "**Ingredients from My Fridge:**\n" +
                                "[List items I already have]\n\n" +
                                "**Missing Ingredients (Need to Buy):**\n" +
                                "[List essential ingredients or seasonings I am likely missing]\n\n" +
                                "**Cooking Instructions:**\n" +
                                "[Detailed, practical step-by-step guide]\n\n" +
                                "IMPORTANT: Direct cooking steps only. Do NOT include history, intro, or cultural background."
                    )
                }
                val response = generativeModel.generateContent(prompt)
                response.text ?: "No recommendation available."
            } catch (e: Exception) {
                "Network Error: ${e.message}"
            }
        }
    }
}
