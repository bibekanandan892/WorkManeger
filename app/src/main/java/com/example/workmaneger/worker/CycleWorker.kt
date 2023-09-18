package com.example.workmaneger.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.math.log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
class CycleWorker constructor(context : Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val calendar = Calendar.getInstance()

// Set the time to 03:35 PM (15:35)
        calendar.set(Calendar.HOUR_OF_DAY, 15) // 15 corresponds to 03:00 PM
        calendar.set(Calendar.MINUTE, 35)

// Create a SimpleDateFormat to format the time
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

// Format the time and store it as a String
        val formattedTime = sdf.format(calendar.time)
        val db = Firebase.firestore

        val user = hashMapOf(
            "id" to UUID.randomUUID().toString(),
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815,
            "time" to formattedTime

        )
        try {
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("CycleWorker", "DocumentSnapshot added with ID: ${documentReference.id}")

                }
                .addOnFailureListener { e ->
                    Result.failure()
                    Log.w("CycleWorker", "Error adding document", e)
                }
            Log.d("CycleWorker", "doWork: Success")

            db.collection("users").addSnapshotListener { value, error ->

                Log.d("Cycle Worker", "doWork:${value?.documents} ")
                Toast.makeText(applicationContext, "${value?.documents}", Toast.LENGTH_SHORT).show()
            }

            return Result.success()
        }catch (e: Exception){
            return Result.failure()

        }

    }
}