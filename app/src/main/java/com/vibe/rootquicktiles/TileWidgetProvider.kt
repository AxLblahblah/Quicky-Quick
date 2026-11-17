package com.vibe.rootquicktiles

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews

class TileWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAll(context)
    }

    companion object {
        private val SLOTS_4 = listOf(
            R.id.iv0 to R.id.tv0,
            R.id.iv1 to R.id.tv1,
            R.id.iv2 to R.id.tv2,
            R.id.iv3 to R.id.tv3,
        )
        private val SLOTS_8 = SLOTS_4 + listOf(
            R.id.iv4 to R.id.tv4,
            R.id.iv5 to R.id.tv5,
            R.id.iv6 to R.id.tv6,
            R.id.iv7 to R.id.tv7,
        )

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, TileWidgetProvider::class.java))
            if (ids.isEmpty()) return
            Thread {
                val tiles = Prefs.enabledTiles(context).mapNotNull { Tiles.byId[it] }
                val states = readStates(tiles)
                val views = buildViews(context, tiles, states)
                Handler(Looper.getMainLooper()).post {
                    for (id in ids) {
                        manager.updateAppWidget(id, views)
                    }
                }
            }.start()
        }

        private fun readStates(tiles: List<TileDef>): Map<TileId, Boolean> {
            val result = mutableMapOf<TileId, Boolean>()
            val shellTiles = tiles.filter { it.query != null }
            if (shellTiles.isNotEmpty() && RootShell.check()) {
                val script = shellTiles.joinToString(" ; ") { it.query!! }
                val lines = RootShell.run(script)
                    ?.split("\n")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    .orEmpty()
                shellTiles.forEachIndexed { index, tile ->
                    val parse = tile.parseOn ?: { _: String -> false }
                    result[tile.id] = index < lines.size && parse(lines[index])
                }
            }
            tiles.filter { it.query == null }.forEach { tile ->
                when (tile.id) {
                    TileId.FLASHLIGHT -> result[tile.id] = TorchController.on
                    else -> result[tile.id] = false
                }
            }
            return result
        }

        private fun buildViews(
            context: Context,
            tiles: List<TileDef>,
            states: Map<TileId, Boolean>,
        ): RemoteViews {
            val layout = if (tiles.size <= 4) R.layout.widget_tiles_4 else R.layout.widget_tiles_8
            val slots = if (tiles.size <= 4) SLOTS_4 else SLOTS_8
            val views = RemoteViews(context.packageName, layout)
            slots.forEachIndexed { index, (iconId, labelId) ->
                val tile = tiles.getOrNull(index)
                if (tile == null) {
                    views.setViewVisibility(iconId, View.INVISIBLE)
                    views.setViewVisibility(labelId, View.INVISIBLE)
                } else {
                    views.setViewVisibility(iconId, View.VISIBLE)
                    views.setViewVisibility(labelId, View.VISIBLE)
                    views.setTextViewText(labelId, tile.label)
                    views.setImageViewResource(
                        iconId,
                        if (states[tile.id] == true) tile.iconOn else tile.iconOff
                    )
                    val pendingIntent = ToggleReceiver.pendingIntent(context, tile.id)
                    views.setOnClickPendingIntent(iconId, pendingIntent)
                    views.setOnClickPendingIntent(labelId, pendingIntent)
                }
            }
            return views
        }
    }
}
