#!/sbin/sh
SKIPUNZIP=0

ui_print "- Status Bar Icon Order module"

# Safety: Android Go and other low-RAM builds are known to be fragile with
# framework config overlays. Refuse to install unless the user opts in.
if [ "$(getprop ro.config.low_ram)" = "true" ]; then
  if [ ! -f "/sdcard/allow_statusbar_order" ]; then
    abort "! Android Go / low-RAM device detected. This module is NOT recommended here (risk of bootloop). To override on purpose, place an empty file named 'allow_statusbar_order' in internal storage (/sdcard) and flash this zip again."
  fi
  ui_print "- Go / low-RAM device with explicit override file detected, continuing..."
fi

if [ ! -f "$MODPATH/overlay.apk" ]; then
  abort "! overlay.apk missing from module zip"
fi

mkdir -p "$MODPATH/system/product/overlay"
mv -f "$MODPATH/overlay.apk" "$MODPATH/system/product/overlay/StatusbarOrderOverlay.apk"

set_perm_recursive "$MODPATH" 0 0 0755 0644

ui_print "- Installed systemless overlay for package: android"
ui_print "- Wi-Fi will now sit to the left of the mobile signal icon"
ui_print "- Reboot to apply"
ui_print "- To revert: remove module in Magisk and reboot"
