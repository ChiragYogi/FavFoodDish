package com.example.favfooddish.model.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.favfooddish.MainActivity
import com.example.favfooddish.R
import com.example.favfooddish.utils.Constant


class NotifyWorker(context: Context, workerParameters: WorkerParameters):
    Worker(context,workerParameters) {
    override fun doWork(): Result {

        showNotificaiton()
        Log.d("FavFoodDish", "do work in background is called")


        return Result.success()
    }

    private fun showNotificaiton() {
        val notificationId = 0

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constant.NOTIFICATION_ID, notificationId)


        val notifiactionManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = applicationContext.getString(R.string.notification_title)
        val subTitle = applicationContext.getString(R.string.notification_subtitle)
        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_vector_logo)
        val bigPicStyle = NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)


        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val notification =
            NotificationCompat.Builder(applicationContext, Constant.Notification_CHANNEL)
                .setContentTitle(title)
                .setContentText(subTitle)
                .setSmallIcon(R.drawable.ic_small_logo)
                .setLargeIcon(bitmap)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setStyle(bigPicStyle)
                .setAutoCancel(true)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(Constant.NOTIFICATION_ID)

            // Setup the Ringtone for Notification.
            val ringtoneManager =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channel = NotificationChannel(
                Constant.Notification_CHANNEL,
                Constant.NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.lightColor = Color.CYAN
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notifiactionManager.createNotificationChannel(channel)
        }
        notifiactionManager.notify(notificationId, notification.build())


    }

    private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }




}