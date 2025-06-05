package com.example.smartaquarium

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartaquarium.Worker.WaterStatusWorker
import com.example.smartaquarium.navigation.NavigationApp
import com.example.smartaquarium.ui.theme.SmartAquariumTheme
import com.example.smartaquarium.utils.GlobalStatusManager
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Izin notifikasi tidak diberikan", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "🚀 MainActivity.onCreate dijalankan")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("MainActivity", "✅ App launched, calling GlobalStatusManager.checkAndNotifyFromPrefs")
        GlobalStatusManager.checkAndNotifyFromPrefs(applicationContext)

        // ✅ Minta izin notifikasi (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // ✅ Minta izin battery optimization
        requestBatteryOptimizationPermission()
        scheduleBackgroundWorker()

        setContent {
            SmartAquariumTheme {
                NavigationApp()
            }
        }
    }
    private fun scheduleBackgroundWorker() {
        Log.d("MainActivity", "✅ Worker scheduled")
        val workRequest = PeriodicWorkRequestBuilder<WaterStatusWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "WaterStatusCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun requestBatteryOptimizationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = packageName
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }
    }


}
