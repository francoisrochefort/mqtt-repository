package com.example.test1234

import android.app.Application
import com.example.test1234.di.AppModule

class Test1234 : Application() {
    companion object {
        lateinit var appModule: AppModule
    }
    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}