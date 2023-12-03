package com.example.flickrstudio.workmanager

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flickrstudio.MainActivity
import com.example.flickrstudio.R
import com.example.flickrstudio.api.RetrofitApi
import com.example.flickrstudio.repo.GalleryRepository
import com.example.flickrstudio.repo.PreferencesRepository
import com.example.flickrstudio.util.NOTIFICATION_CHANNEL_ID
import kotlinx.coroutines.flow.first

const val TAG = "PollWorker"

class PollWorkManager(
    private val context: Context,
    private val workParas: WorkerParameters
) : CoroutineWorker(context, workParas) {

    val prefRepo = PreferencesRepository.get()

    val galleryRepository = GalleryRepository(RetrofitApi.api)

    override suspend fun doWork(): Result {
        val query = prefRepo.storedQuery.first()
        val lastResultId = prefRepo.lastResultId.first()
        if (query.isEmpty()) {
            Log.d(TAG, "No Saved Query, finishing early")
        }

        return try {
            val items = galleryRepository.searchPhotos(query)
            if (items.isNotEmpty()) {
                val newResultId = items.first().id
                if (newResultId == lastResultId) {
                    Log.d(TAG, "Still have the same result: $newResultId")
                } else {
                    Log.d(TAG, "Got some new data: $newResultId")
                    prefRepo.setLastResultId(newResultId)
                    val notification = createNotification()
//                    notifyUser()
//                    context.sendBroadcast(Intent(ACTION_SHOW_NOTIFICATION), PRIVATE_PERM)
                    showBackgroundNotification(0, notification)
                }
            }
            Result.success()
        } catch (ex: Exception) {
            Log.d(TAG, "Background task cannot be happen")
            Result.failure()
        }
    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification) {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).also {
            it.putExtra(REQUEST_CODE, requestCode)
            it.putExtra(NOTIFICATION, notification)
        }
        context.sendOrderedBroadcast(intent, PRIVATE_PERM)
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION =
            "com.example.flickrstudio.SHOW_NOTIFICATION"
        const val PRIVATE_PERM = "com.example.flickrstudion.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }

    private fun createNotification(): Notification {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(
            context, NOTIFICATION_CHANNEL_ID
        ).setTicker(context.getString(R.string.new_pictures_title))
            .setSmallIcon(R.drawable.flickr)
            .setContentTitle(context.getString(R.string.new_pictures_title))
            .setContentText(context.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }
}