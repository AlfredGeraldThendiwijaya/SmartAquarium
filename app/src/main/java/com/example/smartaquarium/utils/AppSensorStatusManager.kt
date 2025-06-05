//ph > 7.0 -> (7.5 - ph) * 200
//else -> 100.0
//}
//
//val tdsScore = when {
//    tds > 180 -> 0.0
//    tds <= 150 -> 100.0
//    else -> (180 - tds) * (100.0 / 30.0)
//}
//
//val tempScore = when {
//    temp < 25 || temp > 31 -> 0.0
//    temp < 27 -> (temp - 25) * 50
//    temp > 29 -> (31 - temp) * 50
//    else -> 100.0
//}
//
//val avgScore = (phScore + tdsScore + tempScore) / 3
//val status = when {
//    avgScore >= 80 -> "Safe"
//    avgScore >= 50 -> "Risk"
//    else -> "Dangerous"
//}
//
//Log.d("GlobalStatusManager", "📊 avgScore = $avgScore → Status = $status")
//
//if (status != "Safe") {
//    WaterStatusNotifier.maybeNotify(context, status)
//}
//}
//}
//package com.example.smartaquarium.utils
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.util.Log
//
//object AppSensorStatusManager {
//    fun checkAndNotify(context: Context, ph: Double, temp: Double, tds: Double) {
//        val sharedPrefs: SharedPreferences = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
//
//        val phScore = when {
//            ph < 5.5 || ph > 7.5 -> 0.0
//            ph < 6.0 -> (ph - 5.5) * 200
//