# Root Quick Tiles

An original, open Android app that puts Nothing-OS-style quick-settings tiles on your home screen as a widget. Each tile reads and toggles a system setting through a **root shell** (`su`), so you can flip Wi-Fi, Bluetooth, DND and more without opening the app or the notification shade.

This is an independent implementation — no code, assets, or branding from any commercial app.

## Features

- Home-screen widget (4x2, resizable) with up to 8 tiles
- 4-tile single-row layout is used automatically when 4 or fewer tiles are enabled
- Tiles: **Wi-Fi, Bluetooth, Do Not Disturb, Flashlight, Airplane mode, Auto-rotate, Mobile data, Location**
- On/off icon states refreshed from live system state on every update
- Config screen (also opens when you place the widget): enable/disable tiles and reorder them
- Flashlight uses the camera torch API (no root needed); everything else uses root

## Root commands used per tile

| Tile | Read state | Toggle |
|---|---|---|
| Wi-Fi | `settings get global wifi_on` | `svc wifi enable/disable` |
| Bluetooth | `settings get global bluetooth_on` | `svc bluetooth enable/disable` |
| DND | `settings get global zen_mode` | `cmd notification set_dnd priority/off` |
| Airplane | `settings get global airplane_mode_on` | `settings put global airplane_mode_on 0/1` + `am broadcast -a android.intent.action.AIRPLANE_MODE` |
| Auto-rotate | `settings get system accelerometer_rotation` | `settings put system accelerometer_rotation 1/0` |
| Mobile data | `settings get global mobile_data` | `svc data enable/disable` |
| Location | `settings get secure location_mode` | `settings put secure location_mode 3/0` |
| Flashlight | `CameraManager` torch callback | `CameraManager.setTorchMode` |

### Device/ROM caveats

- `cmd notification set_dnd` needs a reasonably recent Android (9+); on some ROMs the subcommand differs.
- Some OEMs ignore `settings put` for location or block the `AIRPLANE_MODE` broadcast even from root.
- `settings get global mobile_data` is not reliable on all devices (state may show wrong even though `svc data` toggles fine).
- If a toggle doesn't stick on your ROM, tell me the device/Android version and I'll add a per-ROM command variant.

## Build

1. Open the project in **Android Studio** (it will handle Gradle 8.7 / AGP 8.5.2 sync; if `./gradlew` complains about a missing wrapper jar, run `gradle wrapper` once or just use Studio's sync).
2. `Build > Generate Signed APK` or `./gradlew assembleDebug`.
3. Install, grant **superuser** to the app in your root manager (Magisk/KernelSU), then add the "Quick Tiles (Root)" widget to your launcher.

- minSdk 26 (Android 8.0), targetSdk 34, Kotlin, no dependencies beyond `core-ktx`.

## Notes

- The superuser prompt appears the first time a tile query runs; accept it once for background use.
- Widget state refreshes on tap, on the 30-minute system update cycle, and after each toggle. It does not live-stream state changes made outside the widget (e.g., from quick settings) until the next refresh.
