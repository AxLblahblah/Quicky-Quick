# Status Bar Icon Order (Magisk module)

Systemless framework overlay that re-orders the Android 12 status bar icon slots so **Wi-Fi sits directly to the left of the mobile signal/data icon** — the arrangement used on iOS, and close to how Android 16 groups connectivity icons.

## How it works

`config_statusBarIcons` (framework-res) defines the left-to-right slot order of every status bar icon. The module ships a static runtime resource overlay (RRO) targeting package `android` that replaces the whole array with the AOSP 12 list, reordering the tail so it reads `... ethernet, hotspot, wifi, mobile, airplane, battery ...`. Magisk mounts the APK into `/product/overlay` systemlessly; the overlay manager picks it up at boot as a static (always-on) overlay.

## Install

1. Build the repo's `magisk-module` CI artifact (a `.zip`).
2. Magisk app → Modules → Install from storage → pick the zip.
3. Reboot.

To revert: remove the module in Magisk and reboot.

## Caveats

- Built against the **AOSP 12** icon set. OEM SystemUIs that add their own slot names (heavy OneUI/MIUI/ColorOS mods) may show icons in different spots or ignore the overlay — tell me your device model + ROM and I'll tune the array per device.
- A true Android-16-style *merged pill* around the connectivity icons is drawn by SystemUI code, not config — that can't be done with a config overlay; it would need a per-device SystemUI modification.
