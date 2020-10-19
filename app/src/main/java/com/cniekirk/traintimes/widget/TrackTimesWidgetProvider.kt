package com.cniekirk.traintimes.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.service.ServicesListService


class TrackTimesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        appWidgetIds.forEach {

            val intent = Intent(context, ServicesListService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it)
                data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
            }

            val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
            remoteViews.setRemoteAdapter(R.id.services_list, intent)

            val intentSync = Intent(context, TrackTimesWidgetProvider::class.java)
            intentSync.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            val pendingSync = PendingIntent.getBroadcast(
                context,
                0,
                intentSync,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            remoteViews.setOnClickPendingIntent(R.id.btn_update_widget, pendingSync)

            appWidgetManager.updateAppWidget(it, remoteViews)

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.extras?.let {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(
                context.packageName,
                TrackTimesWidgetProvider::class.java.name
            )
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

//            val intent = Intent(context, ServicesListService::class.java)
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
//
//            val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
//            remoteViews.setRemoteAdapter(R.id.services_list, intent)
//
//            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)

        }

    }

}