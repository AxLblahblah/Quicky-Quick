package com.vibe.rootquicktiles

import androidx.annotation.DrawableRes

enum class TileId {
    WIFI, BLUETOOTH, DND, FLASHLIGHT, AIRPLANE, ROTATE, DATA, LOCATION
}

class TileDef(
    val id: TileId,
    val label: String,
    @DrawableRes val iconOn: Int,
    @DrawableRes val iconOff: Int,
    val query: String? = null,
    val parseOn: ((String) -> Boolean)? = null,
    val toggleOn: String? = null,
    val toggleOff: String? = null,
)

object Tiles {
    private fun isOne(raw: String) = raw.trim() == "1"
    private fun isNonZero(raw: String) = raw.trim() != "0" && raw.trim() != "null" && raw.isNotEmpty()

    val all: List<TileDef> = listOf(
        TileDef(
            TileId.WIFI, "Wi-Fi",
            R.drawable.ic_wifi_on, R.drawable.ic_wifi_off,
            "settings get global wifi_on", ::isOne,
            "svc wifi enable", "svc wifi disable"
        ),
        TileDef(
            TileId.BLUETOOTH, "Bluetooth",
            R.drawable.ic_bluetooth_on, R.drawable.ic_bluetooth_off,
            "settings get global bluetooth_on", ::isOne,
            "svc bluetooth enable", "svc bluetooth disable"
        ),
        TileDef(
            TileId.DND, "Do Not Disturb",
            R.drawable.ic_dnd_on, R.drawable.ic_dnd_off,
            "settings get global zen_mode", ::isNonZero,
            "cmd notification set_dnd priority", "cmd notification set_dnd off"
        ),
        TileDef(
            TileId.FLASHLIGHT, "Flashlight",
            R.drawable.ic_flash_on, R.drawable.ic_flash_off
        ),
        TileDef(
            TileId.AIRPLANE, "Airplane",
            R.drawable.ic_airplane_on, R.drawable.ic_airplane_off,
            "settings get global airplane_mode_on", ::isOne,
            "settings put global airplane_mode_on 1 && am broadcast -a android.intent.action.AIRPLANE_MODE",
            "settings put global airplane_mode_on 0 && am broadcast -a android.intent.action.AIRPLANE_MODE"
        ),
        TileDef(
            TileId.ROTATE, "Auto-rotate",
            R.drawable.ic_rotate_on, R.drawable.ic_rotate_off,
            "settings get system accelerometer_rotation", ::isOne,
            "settings put system accelerometer_rotation 1", "settings put system accelerometer_rotation 0"
        ),
        TileDef(
            TileId.DATA, "Mobile data",
            R.drawable.ic_data_on, R.drawable.ic_data_off,
            "settings get global mobile_data", ::isOne,
            "svc data enable", "svc data disable"
        ),
        TileDef(
            TileId.LOCATION, "Location",
            R.drawable.ic_location_on, R.drawable.ic_location_off,
            "settings get secure location_mode", ::isNonZero,
            "settings put secure location_mode 3", "settings put secure location_mode 0"
        ),
    )

    val byId: Map<TileId, TileDef> = all.associateBy { it.id }
}
