package com.krushiadhaar.app.community

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.krushiadhaar.app.MainActivity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object CommunityNotificationHelper {
    const val CHANNEL_ID = "krishi_community_channel"
    private const val CHANNEL_NAME = "Krishi Community & Charcha"
    private const val CHANNEL_DESC = "Instant notifications when farmers and buyers post experiences, guidance, or crop demands."

    // Flow for in-app floating banner notifications
    private val _inAppNotificationFlow = MutableSharedFlow<CommunityPost>(extraBufferCapacity = 10)
    val inAppNotificationFlow = _inAppNotificationFlow.asSharedFlow()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 250)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun dispatchNewPostNotification(context: Context, post: CommunityPost) {
        // 1. Emit to in-app banner listener
        _inAppNotificationFlow.tryEmit(post)

        // 2. Dispatch system status bar notification
        try {
            createNotificationChannel(context)

            val roleBadge = when (post.authorRole.uppercase()) {
                "BUYER" -> "🤝 Buyer"
                "EXPERT" -> "🔬 Agri Expert"
                else -> "🌾 Farmer"
            }

            val title = "$roleBadge: ${post.authorName}"
            val subtitle = if (!post.cropTag.isNullOrBlank()) "Crop: ${post.cropTag}" else post.authorLocation

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("destination", "community")
                putExtra("post_id", post.id)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                post.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(post.content)
                .setSubText(subtitle)
                .setStyle(NotificationCompat.BigTextStyle().bigText(post.content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            NotificationManagerCompat.from(context).notify(post.id.hashCode(), builder.build())
        } catch (e: SecurityException) {
            // Android 13+ POST_NOTIFICATIONS permission not granted yet - fallback gracefully to in-app banner
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
