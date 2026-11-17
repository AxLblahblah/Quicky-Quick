package com.vibe.rootquicktiles

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val order = Prefs.enabledTiles(this).toMutableList()
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var rowsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val scroll = ScrollView(this).apply { setBackgroundColor(Color.parseColor("#0E1116")) }
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(24))
        }
        scroll.addView(column)
        setContentView(scroll)

        column.addView(TextView(this).apply {
            text = "Root Quick Tiles"
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
        })

        val rootStatus = TextView(this).apply {
            setPadding(0, dp(8), 0, 0)
            setTextColor(Color.parseColor("#9AA4B2"))
            text = "Checking root…"
        }
        column.addView(rootStatus)
        Thread {
            val ok = RootShell.check()
            runOnUiThread {
                rootStatus.text = if (ok) {
                    "Root: available — toggles will work"
                } else {
                    "Root: NOT available — grant superuser to this app or toggles will do nothing"
                }
            }
        }.start()

        column.addView(TextView(this).apply {
            text = "Tiles (order: top-left → bottom-right, 4 per row)"
            setTextColor(Color.WHITE)
            setPadding(0, dp(20), 0, dp(8))
        })

        rowsLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        column.addView(rowsLayout)
        rebuildRows()

        column.addView(Button(this).apply {
            text = "Save & update widgets"
            setOnClickListener { onSave() }
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
            it.topMargin = dp(16)
        })

        column.addView(TextView(this).apply {
            text = "How it works: each tile runs a root shell command (svc / settings / cmd) to read and toggle the setting, like Nothing OS quick-setting widgets. Flashlight uses the camera torch API and needs no root. Add the widget to your home screen, then come back here to pick and reorder tiles."
            textSize = 13f
            setTextColor(Color.parseColor("#9AA4B2"))
            setPadding(0, dp(16), 0, 0)
        })
    }

    private fun onSave() {
        Prefs.save(this, order)
        TileWidgetProvider.updateAll(this)
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
        }
        finish()
    }

    private fun rebuildRows() {
        rowsLayout.removeAllViews()
        val ordered = order.mapNotNull { Tiles.byId[it] }
        val disabled = Tiles.all.filter { it.id !in order }
        (ordered + disabled).forEach { tile -> addRow(tile) }
    }

    private fun addRow(tile: TileDef) {
        val enabled = tile.id in order
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(6), 0, dp(6))
        }
        row.addView(CheckBox(this).apply {
            text = tile.label
            textSize = 16f
            setTextColor(Color.WHITE)
            isChecked = enabled
            setOnCheckedChangeListener { _, checked ->
                if (checked) order.add(tile.id) else order.remove(tile.id)
                rebuildRows()
            }
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        row.addView(Button(this).apply {
            text = "▲"
            isEnabled = enabled && order.indexOf(tile.id) > 0
            setOnClickListener { move(tile.id, -1); rebuildRows() }
        })
        row.addView(Button(this).apply {
            text = "▼"
            isEnabled = enabled && order.indexOf(tile.id) in 0 until order.size - 1
            setOnClickListener { move(tile.id, +1); rebuildRows() }
        })
        rowsLayout.addView(row)
    }

    private fun move(id: TileId, delta: Int) {
        val from = order.indexOf(id)
        val to = from + delta
        if (from >= 0 && to in order.indices) {
            order[from] = order[to].also { order[to] = id }
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
