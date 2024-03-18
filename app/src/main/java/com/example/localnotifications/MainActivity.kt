package com.example.localnotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var button: Button

    private var channelID = "com.example.LocalNotifications"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        button = findViewById(R.id.button)
        button.setOnClickListener {
            if (areNotificationsEnabled(this, channelID)) {
                val snackBar = Snackbar.make(it, "You need to enable app notifications", Snackbar.LENGTH_LONG)
                snackBar.setAction("Open Settings", View.OnClickListener {
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                })
                snackBar.show()
            }
            else {
                sendNotification("Example Notification", "This is a local notification")
            }
        }
        createNotificationChannel(channelID, "Local Notification")
    }

    private fun sendNotification(title: String, content: String) {
        val notificationID = 101
        val icon: Icon = Icon.createWithResource(this, android.R.drawable.ic_dialog_info)
        val resultIntent = Intent(this, ResultActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
            PendingIntent.FLAG_IMMUTABLE)
        val action: Notification.Action = Notification.Action.Builder(icon, "Open", pendingIntent).build()
        val notification = Notification.Builder(this@MainActivity, channelID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .setColor(Color.GREEN)
            .setContentIntent(pendingIntent)
            .setActions(action)
            .setNumber(notificationID)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(id: String, name: String) {
        val importanceLevel = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importanceLevel).apply {
            enableLights(true)
            lightColor = Color.RED
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun areNotificationsEnabled(context: Context, channelID: String?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelID)) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = manager.getNotificationChannel(channelID)
                return channel.importance == NotificationManager.IMPORTANCE_NONE
            }
            false
        }
        else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
}