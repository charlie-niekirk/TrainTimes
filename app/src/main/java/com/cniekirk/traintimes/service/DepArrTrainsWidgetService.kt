package com.cniekirk.traintimes.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.IBinder
import com.cniekirk.traintimes.widget.TrackTimesWidgetProvider

class DepArrTrainsWidgetService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val allWidgetIds = intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

        allWidgetIds?.let { ids ->
            ids.forEach {
                TrackTimesWidgetProvider.updateAppWidget(this, appWidgetManager, it)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

}