package com.example

import android.app.Application

class AnchorApplication : Application() {
    
    // Manual DI container will go here
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
