package com.example.workmaneger.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workmaneger.R
import com.example.workmaneger.data.DemoApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.net.UnknownHostException

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted private val api: DemoApi,
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        delay(10000)
        return try {
            val response = api.getPost()
            if(response.isSuccessful){
                Log.d("CustomWorker", "doWork: Success")
                Log.d("CustomWorker", "doWork: id${response.body()}")
                Result.success()
            }else{
                Log.d("CustomWorker", "doWork: Failuer")

                Result.retry()
            }
        }catch (e :Exception){
            if( e is UnknownHostException){
                Log.d("CustomWorker", "doWork: Retrying")
                Result.retry()

            }else{
                Log.d("CustomWorker", "doWork: Success")
                Result.failure()
            }

        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(context)
    }
}
private fun getForegroundInfo(context: Context): ForegroundInfo{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ForegroundInfo(
            1,
            createNotification(context = context),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
        )
    } else {
        ForegroundInfo(
             1,
            createNotification(context = context)
        )
    }
}

private fun createNotification(context: Context): Notification{
    val channelId = "main_channel_id"
    val channelName = "main channel"

    val builder = NotificationCompat.Builder(context,channelId)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Notification")
        .setContentText("Text")
        .setOngoing(true)
        .setAutoCancel(true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        builder.build()
    }else{
        builder.build()
    }
}