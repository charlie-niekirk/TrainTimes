package com.cniekirk.traintimes.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cniekirk.traintimes.MainActivity
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import com.cniekirk.traintimes.model.PushPortMessageItem
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "NotificationService"

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PreferenceProvider(this).setFirebaseId(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.isNotEmpty().let {
            if (it) {
                processData(remoteMessage.data)
            }
        }

    }

    private fun processData(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.default_notification_channel_id)

        Log.d(TAG, data["body"].toString())

        val pushPortMessage = data["body"]
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val pushPortList = Types.newParameterizedType(List::class.java, PushPortMessageItem::class.java)
        val pushPortAdapter: JsonAdapter<List<PushPortMessageItem>> = moshi.adapter(pushPortList)

        pushPortMessage?.let {
            val msg = pushPortAdapter.fromJson(pushPortMessage)

            val trackedServices = PreferenceProvider(this).retrieveTrackedService()
            trackedServices?.let { services ->
                val relevantService =
                    services.find { service ->
                        service.rid?.equals(msg?.get(0)?.tS?.get(0)?.tsAttrs?.rid, true) ?: false
                    }

                // Compare to cached details
                relevantService?.let { relevant ->

                    val allCachedLocations = if (relevant.currentLocation == null) {
                        relevant.subsequentLocations
                    } else {
                        relevant.subsequentLocations?.let { subsequent ->
                            listOf(relevant.currentLocation).plus(
                                subsequent
                            )
                        }
                    }

                    // For each location in the message, do a check for any changes
                    msg?.get(0)?.tS?.get(0)?.location?.forEach { msgLocation ->

                        val matchingLocation = allCachedLocations?.find { loc ->
                            loc?.tiploc.equals(msgLocation.stationAttrs?.tpl, true)
                        }

                        Log.e(TAG, allCachedLocations?.toString()!!)

                        if (msgLocation.plat?.get(0)?.platform.equals(matchingLocation?.platform, true)) {
                            if (msgLocation.plat?.get(0)?.platAttrs?.platsup.isNullOrEmpty().and(
                                    matchingLocation?.platformIsHidden == true
                                )) {
                                // Platform confirmed, post notification
                                sendNotification("Platform confirmed (${matchingLocation?.locationName}): " +
                                            "${matchingLocation?.platform}")
                            }
                        } else if (!msgLocation.plat?.get(0)?.platform.equals(matchingLocation?.platform, true)) {
                            sendNotification("Platform changed (${matchingLocation?.locationName}): " +
                                            "${matchingLocation?.platform}")
                        }

                        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                        matchingLocation?.std?.let { stdStr ->
                            msgLocation.dep?.get(0)?.depAttrs?.et?.let {
                                val std = sdf.parse(stdStr)
                                val etd = sdf.parse(it)
                                if (etd.after(std)) {
                                    sendNotification("$stdStr at ${matchingLocation.locationName} delayed, now: $it")
                                }
                            }
                        }

                    }
                }

            }

            println(msg)
        }

//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_info)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_speed))
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
//            .setContentTitle(getString(R.string.app_name))
//            .setContentText(data["body"])
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                getString(R.string.app_name),
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_info)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_speed))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


}