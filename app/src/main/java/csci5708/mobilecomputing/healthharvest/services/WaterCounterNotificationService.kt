package csci5708.mobilecomputing.healthharvest.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import csci5708.mobilecomputing.healthharvest.AddFoodItemActivity
import csci5708.mobilecomputing.healthharvest.R
import csci5708.mobilecomputing.healthharvest.WaterDatabaseHelper
import csci5708.mobilecomputing.healthharvest.receivers.WaterNotificationActionReceiver

class WaterCounterNotificationService: Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = this
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create the custom notification layout
        val contentView = RemoteViews(packageName, R.layout.notification_layout)

        contentView.setOnClickPendingIntent(R.id.btnIncrease, getPendingIntentForAction(ACTION_INCREASE))
        contentView.setOnClickPendingIntent(R.id.btnDecrease, getPendingIntentForAction(ACTION_DECREASE))

        contentView.setOnClickPendingIntent(R.id.addFoodButton, getPendingIntentForActivity(context))

        val waterDatabaseHelper = WaterDatabaseHelper(this)
        val count = waterDatabaseHelper.getTotalWaterIntakeForToday()
        contentView.setTextViewText(R.id.txtCount, count.toString())
        // Build the notification using the custom layout
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(contentView)
            .setOngoing(true) // Make the notification persistent
            .setAutoCancel(false) // Disable auto-cancel on swipe
            .build()

        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun getPendingIntentForAction(action: String): PendingIntent {
        val intent = Intent(this, WaterNotificationActionReceiver::class.java).apply {
            this.action = action
        }

        // Use PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_MUTABLE
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNotificationChannel() {
        // Create a notification channel if targeting Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Persistent Notification Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntentForActivity(context: Context): PendingIntent {
        val intent = Intent(context, AddFoodItemActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val NOTIFICATION_ID: Int = 1
        const val CHANNEL_ID = "persistent_notification_channel"
        const val ACTION_INCREASE = "action_increase"
        const val ACTION_DECREASE = "action_decrease"
    }
}