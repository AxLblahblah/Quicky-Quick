package com.vibe.rootquicktiles

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TorchController.init(this)
    }
}
