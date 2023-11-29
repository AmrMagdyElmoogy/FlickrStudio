package com.example.flickrstudio.workmanager

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
                    notifiyUser()
                }
            }
            Result.success()
        } catch (ex: Exception) {
            Log.d(TAG, "Background task cannot be happen")
            Result.failure()
        }
    }

    private fun notifiyUser() {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(
            context, NOTIFICATION_CHANNEL_ID
        ).setTicker(context.getString(R.string.new_pictures_title))
            .setSmallIcon(R.drawable.flickr)
            .setContentTitle(context.getString(R.string.new_pictures_title))
            .setContentText(context.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(0, notification)
    }
}