package com.example.workmaneger

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.workmaneger.data.DemoApi
import com.example.workmaneger.worker.CustomWorker
import com.example.workmaneger.worker.CycleWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: CustomWorkerFactory
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory).build()
    }

}

class CustomWorkerFactory @Inject constructor(private val api: DemoApi) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = CycleWorker(appContext, workerParameters)

}