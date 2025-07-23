package com.example.smartaquarium.ViewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartaquarium.network.RetrofitInstance
import com.example.smartaquarium.network.ph_reading
import com.example.smartaquarium.network.tds_reading
import com.example.smartaquarium.network.temperature_reading
import com.example.smartaquarium.network.turbidity_reading
import com.example.smartaquarium.utils.AuthManager
import com.example.smartaquarium.utils.GlobalStatusManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs: SharedPreferences =
        application.getSharedPreferences("water_status_prefs", Context.MODE_PRIVATE)


    private val _turbidity = MutableStateFlow<Double?>(null)
    val turbidity: StateFlow<Double?> get() = _turbidity

    private val _temperature = MutableStateFlow<Double?>(null)
    val temperature: StateFlow<Double?> get() = _temperature

    private val _ph = MutableStateFlow<Double?>(null)
    val ph: StateFlow<Double?> get() = _ph

    private val _tds = MutableStateFlow<Double?>(null)
    val tds: StateFlow<Double?> get() = _tds

    private val _forecastResult = MutableStateFlow<String?>(null)
    val forecastResult: StateFlow<String?> get() = _forecastResult

    val phForecast = mutableStateOf<List<Float>>(emptyList())
    val tdsForecast = mutableStateOf<List<Float>>(emptyList())
    val tempForecast = mutableStateOf<List<Float>>(emptyList())
    val timeForecast = mutableStateOf<List<Long>>(emptyList()) // timestamp in millis

    fun fetchForecast(unitId: String) {

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getForecastData(unitId)
                if (response.isSuccessful) {
                    val forecastList = response.body() ?: emptyList()

                    phForecast.value = forecastList.map { it.predicted_ph }
                    tdsForecast.value = forecastList.map { it.predicted_tds }
                    tempForecast.value = forecastList.map { it.predicted_temperature }
                    timeForecast.value = forecastList.map {
                        // Parse waktu ISO ke timestamp millis
                        OffsetDateTime.parse(it.time).toInstant().toEpochMilli()
                    }
                } else {
                    Log.e("Forecast", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Forecast", "Error fetching forecast", e)
            }
        }
    }



    fun fetchTurbidity(unitId: String) {
        AuthManager.getToken { token ->
            token?.let {
                RetrofitInstance.apiService.getLatestTurbidity(unitId, "Bearer $token")
                    .enqueue(object : Callback<turbidity_reading> {
                        override fun onResponse(call: Call<turbidity_reading>, response: Response<turbidity_reading>) {
                            if (response.isSuccessful) {
                                viewModelScope.launch {
                                    _turbidity.emit(response.body()?.latestData?.value)
                                }
                            }
                        }
                        override fun onFailure(call: Call<turbidity_reading>, t: Throwable) {}
                    })
            }
        }
    }

    fun fetchTemperature(unitId: String) {
        AuthManager.getToken { token ->
            token?.let {
                RetrofitInstance.apiService.getLatestTemperature(unitId, "Bearer $token")
                    .enqueue(object : Callback<temperature_reading> {
                        override fun onResponse(call: Call<temperature_reading>, response: Response<temperature_reading>) {
                            if (response.isSuccessful) {
                                val tempValue = response.body()?.latestData?.value
                                viewModelScope.launch {
                                    _temperature.emit(tempValue)
                                }
                            }
                        }
                        override fun onFailure(call: Call<temperature_reading>, t: Throwable) {}
                    })
            }
        }
    }

    fun fetchPh(unitId: String) {
        AuthManager.getToken { token ->
            token?.let {
                RetrofitInstance.apiService.getLatestph(unitId, "Bearer $token")
                    .enqueue(object : Callback<ph_reading> {
                        override fun onResponse(call: Call<ph_reading>, response: Response<ph_reading>) {
                            if (response.isSuccessful) {
                                val phValue = response.body()?.latestData?.value
                                viewModelScope.launch {
                                    _ph.emit(phValue)
                                }
                            }
                        }
                        override fun onFailure(call: Call<ph_reading>, t: Throwable) {}
                    })
            }
        }
    }

    fun fetchTds(unitId: String) {
        AuthManager.getToken { token ->
            token?.let {
                RetrofitInstance.apiService.getLatestTds(unitId, "Bearer $token")
                    .enqueue(object : Callback<tds_reading> {
                        override fun onResponse(call: Call<tds_reading>, response: Response<tds_reading>) {
                            if (response.isSuccessful) {
                                val tdsValue = response.body()?.latestData?.value
                                viewModelScope.launch {
                                    _tds.emit(tdsValue)
                                    val phVal = _ph.value
                                    val tempVal = _temperature.value
                                    if (phVal != null && tempVal != null && tdsValue != null) {
                                        saveSensorValuesToPrefs(phVal, tempVal, tdsValue)
                                        GlobalStatusManager.checkAndNotifyFromPrefs(getApplication())
                                    }
                                }
                            }
                        }
                        override fun onFailure(call: Call<tds_reading>, t: Throwable) {}
                    })
            }
        }
    }

    private fun saveSensorValuesToPrefs(ph: Double, temp: Double, tds: Double) {
        sharedPrefs.edit()
            .putFloat("ph_value", ph.toFloat())
            .putFloat("temp_value", temp.toFloat())
            .putFloat("tds_value", tds.toFloat())
            .apply()
    }

    fun startRealtimeUpdates(unitId: String) {
        fetchTurbidity(unitId)
        fetchTemperature(unitId)
        fetchPh(unitId)
        fetchTds(unitId)
    }

    fun refreshSensorData(unitId: String) {
        fetchTurbidity(unitId)
        fetchTemperature(unitId)
        fetchPh(unitId)
        fetchTds(unitId)
    }
}
