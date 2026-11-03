package com.rojo.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — Hilt entry point so @HiltAndroidApp is honoured
 * across the whole app (MainActivity + all screens).
 */
@HiltAndroidApp
class CaptainRojoApp : Application()
