# Status Bar Icon Order (Magisk module)

Systemless framework overlay that re-orders the Android 12 status bar icon slots so **Wi-Fi sits directly to the left of the mobile signal/data icon** — the arrangement used on iOS, and close to how Android 16 groups connectivity icons.

## How it works

`config_statusBarIcons` (framework-res) defines the left-to-right slot order of every status bar icon. The module ships a static runtime resource overlay (RRO) targeting package `android` that replaces the whole array with the AOSP 12 list, reordering the tail so it reads `... ethernet, hotspot, wifi, mobile, airplane, battery ...`. Magisk mounts the APK into `/product/overlay` systemlessly; the overlay manager picks it up at boot as a static (always-on) overlay.

## Tested device: Redmi A1+ / POCO C50 (snow / ice)

The installer is tuned for the Xiaomi **Redmi A1+** (codename `snow`, also known as `ice`, sold as POCO C50) running Android 12 Go (MIUI 13, build V13.0.24.0.SGMMIXM). On that device the module installs directly, with no override file. Other Android Go builds still require the marker-file opt-in (see below).

Lessons from real-device testing on this model:

- Removal is only fully complete if BOTH the module folder AND the overlay cache are gone. When removing by hand in TWRP (data must be decrypted there):

  ```
  rm -rf /data/adb/modules/statusbar_icon_order
  rm -rf /data/resource-cache/*
  ```

  Deleting only the module folder from TWRP skips `uninstall.sh` and left the compiled idmap cache behind on the test device.

- If Android misbehaves after installing or removing, do NOT trigger or let through a factory reset, and do NOT delete `/data/system/packages.xml`. Module removal plus the two commands above, then reboot, is enough. On this device a factory-reset attempt that was interrupted while magiskd held the `/data` unmount lock corrupted the package permission database and killed SystemUI with SecurityExceptions on every boot - far worse than the original overlay issue ever was.

## Install

1. Build the repo's `magisk-module` CI artifact (a `.zip`).
2. Magisk app → Modules → Install from storage → pick the zip.
3. Reboot.

### Non-Redmi A1+ / other Android Go devices

On low-RAM (`ro.config.low_ram=true`) builds other than snow/ice, the installer refuses to run unless you explicitly opt in by creating an empty file named `allow_statusbar_order` in internal storage (`/sdcard`) and flashing again.

## Caveats

- Built against the **AOSP 12** icon set. OEM SystemUIs that add their own slot names (heavy OneUI/MIUI/ColorOS mods) may show icons in different spots or ignore the overlay — tell me your device model + ROM and I'll tune the array per device.
- A true Android-16-style *merged pill* around the connectivity icons is drawn by SystemUI code, not config — that can't be done with a config overlay; it would need a per-device SystemUI modification.

## Uninstall behavior

Removing the module in the Magisk app deletes the module folder (which contains the overlay APK) and then runs `uninstall.sh`, which additionally clears the compiled overlay cache in `/data/resource-cache` so the next boot is clean. Note that deleting the module folder manually (e.g., from TWRP file manager) skips `uninstall.sh` and any overlay-cache clearing, so a manual removal should be followed by `rm -rf /data/resource-cache/*` and a Dalvik cache wipe.
