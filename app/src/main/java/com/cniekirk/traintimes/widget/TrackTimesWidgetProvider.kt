package com.cniekirk.traintimes.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.cniekirk.traintimes.MainActivity
import com.cniekirk.traintimes.R

class TrackTimesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { widgetId ->
            // Create an Intent to launch ExampleActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_layout
            )

            appWidgetManager.updateAppWidget(widgetId, views)

        }
    }

}