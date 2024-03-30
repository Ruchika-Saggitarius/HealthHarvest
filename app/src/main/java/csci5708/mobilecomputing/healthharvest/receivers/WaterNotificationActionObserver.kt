package csci5708.mobilecomputing.healthharvest.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import csci5708.mobilecomputing.healthharvest.AddFoodItemActivity
import csci5708.mobilecomputing.healthharvest.R
import csci5708.mobilecomputing.healthharvest.WaterDatabaseHelper
import csci5708.mobilecomputing.healthharvest.services.WaterCounterNotificationService

class WaterNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == WaterCounterNotificationService.ACTION_INCREASE) {
            // Handle the + button click
            updateCount(context, true)
        } else if (action == WaterCounterNotificationService.ACTION_DECREASE) {
            // Handle the - button click
            updateCount(context, false)
        }
    }

    private fun updateCount(context: Context, isIncrement: Boolean) {

        val waterDatabaseHelper = WaterDatabaseHelper(context)

        if (isIncrement) {
            waterDatabaseHelper.addWaterIntakeForToday()
        }
        else {
            waterDatabaseHelper.removeLatestWaterIntakeForToday()
        }

        // Increment or decrement the count based on the button click
        val updatedCount = waterDatabaseHelper.getTotalWaterIntakeForToday()

        // Update the notification with the new count
        updateNotification(context, updatedCount)
    }

    private fun getPendingIntentForAction(context: Context, action: String): PendingIntent {
        val intent = Intent(context, WaterNotificationActionReceiver::class.java).apply {
            this.action = action
        }

        // Use PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_MUTABLE
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getPendingIntentForActivity(context: Context): PendingIntent {
        val intent = Intent(context, AddFoodItemActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun updateNotification(context: Context, count: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val contentView = RemoteViews(context.packageName, R.layout.notification_layout)


        contentView.setOnClickPendingIntent(R.id.btnIncrease, getPendingIntentForAction(context,
            WaterCounterNotificationService.ACTION_INCREASE
        ))
        contentView.setOnClickPendingIntent(R.id.btnDecrease, getPendingIntentForAction(context,
            WaterCounterNotificationService.ACTION_DECREASE
        ))

        contentView.setOnClickPendingIntent(R.id.addFoodButton, getPendingIntentForActivity(context))


        // Update the count in the notification layout
        contentView.setTextViewText(R.id.txtCount, count.toString())

        // Rebuild the notification with the updated layout
        val notification = NotificationCompat.Builder(context, WaterCounterNotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(contentView)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(WaterCounterNotificationService.NOTIFICATION_ID, notification)
    }
}
