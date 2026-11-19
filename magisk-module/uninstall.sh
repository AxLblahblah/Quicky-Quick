#!/sbin/sh
# Executed by the Magisk app when this module is removed.
# Magisk itself deletes $MODPATH, which already removes the overlay APK,
# but we also clear the compiled overlay (idmap) cache so the framework
# rebuilds a clean state on next boot.

ui_print "- Removing Status Bar Icon Order leftovers"

# Remove the overlay APK and slot if anything was mounted manually
rm -f /data/adb/modules/statusbar_icon_order/system/product/overlay/StatusbarOrderOverlay.apk

# Clear idmap cache so no stale overlay mapping survives
rm -rf /data/resource-cache/*

ui_print "- Done. Reboot to finish."
