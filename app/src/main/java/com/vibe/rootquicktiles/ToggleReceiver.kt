package com.vibe.rootquicktiles

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ToggleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TOGGLE) return
        val tileName = intent.getStringExtra(EXTRA_TILE) ?: return
        val tile = Tiles.byId[runCatching { TileId.valueOf(tileName) }.getOrNull()] ?: return
        val pending = goAsync()
        Thread {
            try {
                when (tile.id) {
                    TileId.FLASHLIGHT -> TorchController.toggle()
                    else -> {
                        val currentlyOn = queryState(tile)
                        val command = if (currentlyOn) tile.toggleOff else tile.toggleOn
                        command?.let { RootShell.run(it) }
                    }
                }
                TileWidgetProvider.updateAll(context)
            } finally {
                pending.finish()
            }
        }.start()
    }

    private fun queryState(tile: TileDef): Boolean {
        val query = tile.query ?: return false
        val parse = tile.parseOn ?: return false
        if (!RootShell.check()) return false
        val raw = RootShell.run(query)?.trim() ?: return false
        return parse(raw)
    }

    companion object {
        const val ACTION_TOGGLE = "com.vibe.rootquicktiles.action.TOGGLE"
        const val EXTRA_TILE = "tile"

        fun pendingIntent(context: Context, id: TileId): PendingIntent {
            val intent = Intent(context, ToggleReceiver::class.java)
                .setAction(ACTION_TOGGLE)
                .putExtra(EXTRA_TILE, id.name)
            return PendingIntent.getBroadcast(
                context,
                id.ordinal,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
