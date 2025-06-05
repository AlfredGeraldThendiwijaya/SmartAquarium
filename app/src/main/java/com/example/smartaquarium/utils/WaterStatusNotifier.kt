package com.example.smartaquarium.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object GlobalStatusManager {
    private const val TAG = "GlobalStatusManager"
    private const val PREFS_SENSOR = "water_status_prefs"
    private const val PREFS_NOTIF = "notification_prefs"

    private const val KEY_RISK = "last_risk_time"
    private const val KEY_DANGER = "last_danger_time"

    fun checkAndNotifyFromPrefs(context: Context) {
        val sensorPrefs: SharedPreferences = context.getSharedPreferences(PREFS_SENSOR, Context.MODE_PRIVATE)
        val notifPrefs: SharedPreferences = context.getSharedPreferences(PREFS_NOTIF, Context.MODE_PRIVATE)

        val ph = sensorPrefs.getFloat("ph_value", -1f).toDouble()
        val temp = sensorPrefs.getFloat("temp_value", -1f).toDouble()
        val tds = sensorPrefs.getFloat("tds_value", -1f).toDouble()

        Log.d("GlobalStatusManager", "✅ Fetched sensor values from prefs: pH=$ph, Temp=$temp, TDS=$tds")

        if (ph == -1.0 || temp == -1.0 || tds == -1.0) {
            Log.d(TAG, "⚠️ Data sensor belum lengkap")
            return
        }

        val phScore = when {
            ph < 5.5 || ph > 7.5 -> 0.0
            ph < 6.0 -> (ph - 5.5) * 200
            ph > 7.0 -> (7.5 - ph) * 200
            else -> 100.0
        }

        val tdsScore = when {
            tds > 180 -> 0.0
            tds <= 150 -> 100.0
            else -> (180 - tds) * (100.0 / 30.0)
        }

        val tempScore = when {
            temp < 25 || temp > 31 -> 0.0
            temp < 27 -> (temp - 25) * 50
            temp > 29 -> (31 - temp) * 50
            else -> 100.0
        }

        val avgScore = (phScore + tdsScore + tempScore) / 3
        val status = when {
            avgScore >= 80 -> "Safe"
            avgScore >= 50 -> "Risk"
            else -> "Dangerous"
        }

        Log.d("GlobalStatusManager", "📊 avgScore = $avgScore → Status = $status")

        val now = System.currentTimeMillis()
        when (status.lowercase()) {
            "dangerous" -> {
                val last = notifPrefs.getLong(KEY_DANGER, 0)
                if (now - last > 60 * 60 * 1000) {
                    NotificationHelper.showNotification(
                        context,
                        "⚠️ Dangerous Water Quality",
                        "Take immediate action. Water condition is dangerous."
                    )
                    notifPrefs.edit().putLong(KEY_DANGER, now).apply()
                } else {
                    Log.d(TAG, "❌ Skip notif Dangerous: belum lewat 1 jam")
                }
            }

            "risk" -> {
                val last = notifPrefs.getLong(KEY_RISK, 0)
                if (now - last > 3 * 60 * 60 * 1000) {
                    NotificationHelper.showNotification(
                        context,
                        "⚠️ Risky Water Quality",
                        "Check your aquarium soon. Water is not optimal."
                    )
                    notifPrefs.edit().putLong(KEY_RISK, now).apply()
                } else {
                    Log.d(TAG, "❌ Skip notif Risk: belum lewat 3 jam")
                }
            }

            else -> {
                Log.d(TAG, "✅ Status aman, tidak perlu notifikasi")
            }
        }
    }
}
