package com.example.workmaneger

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmaneger.ui.theme.WorkManegerTheme
import com.example.workmaneger.worker.CustomWorker
import com.example.workmaneger.worker.CycleWorker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.LongUnaryOperator

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore

//        db.collection("users").addSnapshotListener { value, error ->
//            value?.documents
//            Log.d("Cycle Worker", "doWork:${value?.documents} ")
//        }
//        val workRequest =
//            OneTimeWorkRequestBuilder<CustomWorker>()
//                .setExpedited(policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
////                .setInitialDelay(Duration.ofSeconds(10))
//                .setBackoffCriteria(
//                    backoffPolicy = BackoffPolicy.LINEAR,
//                    duration = Duration.ofSeconds(15)
//                ).build()
//        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        setContent {
            WorkManegerTheme {
                LaunchedEffect(key1 = Unit, block = {
                    val constraints  = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val workRequest = PeriodicWorkRequestBuilder<CycleWorker>(
                        repeatInterval = 15,
                        repeatIntervalTimeUnit = TimeUnit.MINUTES,
                        flexTimeInterval = 15,
                        flexTimeIntervalUnit = TimeUnit.SECONDS
                    ).setBackoffCriteria(
                        backoffPolicy = BackoffPolicy.LINEAR, duration = Duration.ofSeconds(
                            15
                        )
                    ).setConstraints(constraints = constraints).build()
                    val workManager = WorkManager.getInstance(applicationContext)
                    workManager.enqueueUniquePeriodicWork(
                        "myWork",
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest
                    )
                })
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WorkManegerTheme {
        Greeting("Android")
    }
}