#!/sbin/sh
SKIPUNZIP=0

DEVICE="$(getprop ro.product.device)"
FINGERPRINT="$(getprop ro.build.fingerprint)"

ui_print "- Status Bar Icon Order module v1.1"
ui_print "- Device: ${DEVICE:-unknown}"
ui_print "- ROM:    ${FINGERPRINT:-unknown}"

# Device whitelist: this overlay is tuned and intended for the Redmi A1+ /
# POCO C50 (codename snow / ice, Android 12 Go, MIUI 13). On that device we
# install without any override file. On other low-RAM (Android Go) builds we
# still refuse unless the user opts in with a marker file.
if [ "${DEVICE}" = "ice" ] || [ "${DEVICE}" = "snow" ]; then
  ui_print "- Redmi A1+ / POCO C50 detected: device-tuned build, installing"
elif [ "$(getprop ro.config.low_ram)" = "true" ]; then
  if [ ! -f "/sdcard/allow_statusbar_order" ]; then
    abort "! Android Go / low-RAM device detected and not a supported model. To override on purpose, place an empty file named 'allow_statusbar_order' in internal storage (/sdcard) and flash this zip again."
  fi
  ui_print "- Go / low-RAM device with explicit override file detected, continuing..."
fi

if [ ! -f "$MODPATH/overlay.apk" ]; then
  abort "! overlay.apk missing from module zip"
fi

# A/B note: the overlay is mounted systemless and is slot-independent, so it
# applies the same regardless of which slot is active.
CURRENT_SLOT="$(getprop ro.boot.slot_suffix)"
if [ -n "${CURRENT_SLOT}" ]; then
  ui_print "- Active slot: ${CURRENT_SLOT}"
fi

mkdir -p "$MODPATH/system/product/overlay"
mv -f "$MODPATH/overlay.apk" "$MODPATH/system/product/overlay/StatusbarOrderOverlay.apk"

set_perm_recursive "$MODPATH" 0 0 0755 0644

ui_print "- Installed systemless overlay for package: android"
ui_print "- Wi-Fi will now sit to the left of the mobile signal icon"
ui_print "- Reboot to apply"
ui_print "- To revert: remove module in Magisk and reboot"
ui_print "- If Android misbehaves after reboot, do NOT factory reset."
ui_print "- Instead: boot TWRP > adb shell > rm -rf /data/adb/modules/statusbar_icon_order /data/resource-cache/* > reboot"
