package com.vibe.rootquicktiles

import android.content.Context

object Prefs {
    private const val FILE = "tiles"
    private const val KEY = "enabled"
    private val DEFAULT = listOf(TileId.WIFI, TileId.BLUETOOTH, TileId.FLASHLIGHT, TileId.DND)

    fun enabledTiles(context: Context): List<TileId> {
        val saved = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return DEFAULT
        return saved.split(",")
            .mapNotNull { runCatching { TileId.valueOf(it) }.getOrNull() }
    }

    fun save(context: Context, tiles: List<TileId>) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, tiles.joinToString(","))
            .apply()
    }
}
