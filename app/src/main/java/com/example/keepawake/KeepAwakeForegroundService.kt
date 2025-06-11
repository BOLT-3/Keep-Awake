package com.example.keepawake

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class KeepAwakeForegroundService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "KeepAwakeServiceChannel"
        private const val CHANNEL_NAME = "Keep Awake Service"

        // Helper method to start the service
        fun startService(context: Context) {
            val intent = Intent(context, KeepAwakeForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        // Helper method to stop the service
        fun stopService(context: Context) {
            val intent = Intent(context, KeepAwakeForegroundService::class.java)
            context.stopService(intent)
        }
    }

    private lateinit var floatingWindow : KeepAwake1x1Window

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        floatingWindow = KeepAwake1x1Window(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create and start the foreground notification
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        floatingWindow.showFloatingWindow()

        // Return START_STICKY to restart the service if it gets killed
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This service doesn't support binding
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any resources here
        if (::floatingWindow.isInitialized) {
            floatingWindow.hideFloatingWindow()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for Keep Awake Service notifications"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        // Create an intent that opens your main activity when notification is tapped
        val notificationIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Keep Awake Service")
            .setContentText("Service is running, floating")
            .setSmallIcon(R.drawable.ic_tea_cup) // Use a system icon or replace with your own
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Makes the notification persistent
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}