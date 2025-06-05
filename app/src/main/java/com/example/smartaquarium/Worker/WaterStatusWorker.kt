package com.example.smartaquarium.Worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.smartaquarium.utils.GlobalStatusManager

class WaterStatusWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d("WaterStatusWorker", "🔥 Worker dijalankan dari background")
        val prefs = applicationContext.getSharedPreferences("SensorPrefs", Context.MODE_PRIVATE)
        val ph = prefs.getFloat("latest_ph", -1f)
        val temp = prefs.getFloat("latest_temp", -1f)
        val tds = prefs.getFloat("latest_tds", -1f)

        Log.d("💧WaterStatusWorker", "📊 Data dari prefs: pH=$ph, Temp=$temp, TDS=$tds")
        GlobalStatusManager.checkAndNotifyFromPrefs(applicationContext)
        return Result.success()
    }
}
