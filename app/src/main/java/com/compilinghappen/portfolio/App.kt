package com.compilinghappen.portfolio

import android.app.Application
import kotlinx.coroutines.runBlocking

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        runBlocking {
            ApiUtils.loadUserToken(applicationContext)
        }
    }
}