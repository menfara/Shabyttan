package farkhat.myrzabekov.shabyttan.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import farkhat.myrzabekov.shabyttan.R
import farkhat.myrzabekov.shabyttan.fragments.HomeFragment

class MyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each widget that belongs to this provider
        for (appWidgetId in appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            val pendingIntent: PendingIntent =
                Intent(context, HomeFragment::class.java)
                    .let { intent ->
                        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    }

            // Get the layout for the App Widget and attach an on-click listener to the button
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_layout
            ).apply {

            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
