package com.example.pam_pertemuan15

import android.app.Application
import com.example.pam_pertemuan15.di.ContainerApp


class MahasiswaApp : Application() {
    lateinit var containerApp: ContainerApp
    override fun onCreate() {
        super.onCreate()
        containerApp = ContainerApp(this)
    }
}