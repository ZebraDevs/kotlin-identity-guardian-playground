package com.zebra.nilac.igplayground

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.util.Random

class StatusService : Service() {

    private var mIsServiceRunning = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mIsServiceRunning = true

        val filters: IntentFilter = IntentFilter().apply {
            addAction(STOP_FG_SERVICE_ACTION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filters, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, filters)
        }

        contentResolver.registerContentObserver(
            Uri.parse(AppConstants.STATUS_URI),
            false,
            statusContentObserver
        )

        startForeground(1161, createServiceNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!mIsServiceRunning) {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        contentResolver.unregisterContentObserver(statusContentObserver)
        unregisterReceiver(receiver)
        mIsServiceRunning = false
    }

    private val statusContentObserver = object : ContentObserver(Handler(Looper.myLooper()!!)) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            getStatusResponse()
        }
    }

    private fun getStatusResponse() {
        val response = contentResolver.call(
            Uri.parse(AppConstants.BASE_URI),
            AppConstants.LOCKSCREEN_STATUS_ACTION,
            AppConstants.LOCKSCREEN_STATUS_STATE_METHOD,
            null
        )

        if (response != null && response.containsKey("RESULT")) {
            Log.i(TAG, response.getString("RESULT")!!)
            try {
                val jsonObject = JSONObject(response.getString("RESULT")!!)

                val state = jsonObject.getString("state")
                val timestamp = jsonObject.getString("lastchangedtimestamp")

                sendNewNotification(
                    "New Event", """
                    Type: $state
                    TimeStamp: $timestamp
                """.trimIndent()
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createServiceNotification(): Notification {
        val channelId = packageName
        val channelName = "IG Playground Status Service"

        // Create Channel
        val notificationChannel = NotificationChannel(
            channelId, channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        // Set Channel
        val manager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)

        // Build Notification
        val notificationBuilder = NotificationCompat.Builder(
            this,
            channelId
        )

        // Add action button in the notification
        val stopServicePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            STOP_FG_SERVICE,
            Intent(STOP_FG_SERVICE_ACTION),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Return Build Notification object
        return notificationBuilder
            .setContentTitle("Service is active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_generic_close, "STOP", stopServicePendingIntent)
            .setOngoing(true)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun sendNewNotification(title: String, content: String) {
        val channelId = packageName
        val channelName = "IG Playground Status Service"

        // Create Channel
        val notificationChannel = NotificationChannel(
            channelId, channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        // Set Channel
        val manager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)

        // Build Notification
        val notificationBuilder = NotificationCompat.Builder(
            this,
            channelId
        ).apply {
            setContentTitle(title)
            setContentText(content)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setCategory(Notification.CATEGORY_SERVICE)
            setPriority(NotificationCompat.PRIORITY_HIGH)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(Random().nextInt(), notificationBuilder.build())
        }
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == STOP_FG_SERVICE_ACTION) {
                Log.i(TAG, "About to stop the service")
                stopSelf()
            }
        }
    }

    companion object {
        const val TAG = "StatusService"

        const val STOP_FG_SERVICE = 1121
        const val STOP_FG_SERVICE_ACTION =
            "com.zebra.nilac.igplayground.STOP_FG_SERVICE_ACTION"
    }
}