package com.example.myapplication.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.data.DBHelper

class ExpiryCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "expiry_check_work"
        private const val DAYS_THRESHOLD = 3
    }

    override suspend fun doWork(): Result {
        return try {
            val dbHelper = DBHelper(context)
            val expiringFoods = dbHelper.getExpiringFoods(DAYS_THRESHOLD)

            if (expiringFoods.isNotEmpty()) {
                val itemNames = expiringFoods.take(3).joinToString(", ") { it.name }
                val message = if (expiringFoods.size > 3) {
                    "$itemNames and ${expiringFoods.size - 3} more items are expiring soon!"
                } else {
                    "$itemNames ${if (expiringFoods.size == 1) "is" else "are"} expiring soon!"
                }

                NotificationHelper.showExpiryNotification(
                    context = context,
                    notificationId = 1001,
                    title = "⚠️ Food Expiring Soon",
                    message = message
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
