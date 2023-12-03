package com.example.flickrstudio.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flickrstudio.workmanager.PollWorkManager

abstract class VisibleFragment : Fragment() {
    val TAG: String = "VisibleFragment"
    private val onShowNotification = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            Toast.makeText(context, "Got a broadcast", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Cancelling the broadcast")
            resultCode = Activity.RESULT_CANCELED
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollWorkManager.ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(
            onShowNotification,
            filter,
            PollWorkManager.PRIVATE_PERM,
            null,
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowNotification)
    }
}