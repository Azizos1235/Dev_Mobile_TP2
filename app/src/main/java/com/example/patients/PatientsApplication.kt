package com.example.patients

import android.app.Application
import com.example.patients.di.AppContainer

class PatientsApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}