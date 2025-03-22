package com.example.smartaquarium.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    fun getToken(callback: (String?) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    Log.d("AuthManager", "Bearer Token: $token")

                    // Simpan token di SharedPreferences
                    saveTokenToPrefs(task.result?.token, FirebaseAuth.getInstance().app.applicationContext)

                    callback(token)
                } else {
                    Log.e("AuthManager", "Gagal mendapatkan token", task.exception)
                    callback(null)
                }
            }
    }

    private fun saveTokenToPrefs(token: String?, context: Context) {
        val sharedPref = context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
        sharedPref.edit().putString("TOKEN", token).apply()
    }

    fun getSavedToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
        return sharedPref.getString("TOKEN", null)
    }
}
