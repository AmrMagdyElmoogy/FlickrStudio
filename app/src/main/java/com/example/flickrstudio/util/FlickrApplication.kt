package com.example.flickrstudio.util

import android.app.Application
import com.example.flickrstudio.repo.PreferencesRepository

class FlickrApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
    }
}