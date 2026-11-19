#!/sbin/sh
SKIPUNZIP=0

ui_print "- Status Bar Icon Order module"

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
