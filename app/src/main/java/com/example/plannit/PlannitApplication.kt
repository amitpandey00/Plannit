package com.example.plannit

import android.app.Application
import com.example.plannit.data.local.database.PlannitDatabase
import com.example.plannit.data.notification.NotificationHelper

class PlannitApplication : Application() {
    
    val database: PlannitDatabase by lazy { 
        PlannitDatabase.getDatabase(this) 
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
