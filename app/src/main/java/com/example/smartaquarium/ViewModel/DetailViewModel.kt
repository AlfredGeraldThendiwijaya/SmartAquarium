package com.example.smartaquarium.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartaquarium.network.RetrofitInstance
import com.example.smartaquarium.network.analyzer.AnalyzerRetrofitInstance
import com.example.smartaquarium.network.analyzer.AnalyzerRetrofitInstance.api
import com.example.smartaquarium.network.ph_reading
import com.example.smartaquarium.network.predict.PredictRetrofitInstance
import com.example.smartaquarium.network.tds_reading
import com.example.smartaquarium.network.temperature_reading
import com.example.smartaquarium.network.turbidity_reading
import com.example.smartaquarium.utils.AuthManager
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _sensorDataPoints = MutableStateFlow<List<Entry>>(emptyList())
    val sensorDataPoints: StateFlow<List<Entry>> get() = _sensorDataPoints

    private val _turbidity = MutableStateFlow<Double?>(null)
    val turbidity: StateFlow<Double?> get() = _turbidity

    private val _temperature = MutableStateFlow<Double?>(null)
    val temperature: StateFlow<Double?> get() = _temperature

    private val _ph = MutableStateFlow<Double?>(null)
    val ph: StateFlow<Double?> get() = _ph

    private val _tds = MutableStateFlow<Double?>(null)
    val tds: StateFlow<Double?> get() = _tds

    val _forecastResult = MutableStateFlow<String?>(null)
    val forecastResult: StateFlow<String?> get() = _forecastResult


    fun fetchForecast(unitId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getForecastText(unitId)
                if (response.isSuccessful) {
                    _forecastResult.value = response.body()?.string()
                }
            } catch (e: Exception) {
                Log.e("Forecast", "Error fetching forecast", e)
            }
        }
    }
    fun fetchTurbidity(unitId: String) {
        Log.d("FETCH_TURBIDITY", "Fetching turbidity for unitId: $unitId")

        AuthManager.getToken { token ->
            if (token != null) {
                viewModelScope.launch {
                    RetrofitInstance.apiService.getLatestTurbidity(unitId, "Bearer $token")
                        .enqueue(object : Callback<turbidity_reading> {
                            override fun onResponse(
                                call: Call<turbidity_reading>,
                                response: Response<turbidity_reading>
                            ) {
                                if (response.isSuccessful) {
                                    val ntuValue = response.body()?.latestData?.value
                                    Log.d("API_SUCCESS", "Turbidity: $ntuValue")

                                    viewModelScope.launch {
                                        _turbidity.emit(ntuValue)
                                    }
                                } else {
                                    Log.e("API_ERROR", "Failed: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<turbidity_reading>, t: Throwable) {
                                Log.e("API_FAILURE", "Error fetching turbidity", t)
                            }
                        })
                }
            } else {
                Log.e("AUTH_ERROR", "Token tidak ditemukan")
            }
        }
    }

    fun fetchTemperature(unitId: String) {
        AuthManager.getToken { token ->
            if (token != null) {
                viewModelScope.launch {
                    RetrofitInstance.apiService.getLatestTemperature(unitId, "Bearer $token")
                        .enqueue(object : Callback<temperature_reading> {
                            override fun onResponse(
                                call: Call<temperature_reading>,
                                response: Response<temperature_reading>
                            ) {
                                if (response.isSuccessful) {
                                    val temperatureValue = response.body()?.latestData?.value
                                    Log.d("API_SUCCESS", "Temperature: $temperatureValue")

                                    viewModelScope.launch {
                                        _temperature.emit(temperatureValue)
                                    }
                                } else {
                                    Log.e("API_ERROR", "Failed: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<temperature_reading>, t: Throwable) {
                                Log.e("API_FAILURE", "Error fetching temperature", t)
                            }
                        })
                }
            } else {
                Log.e("AUTH_ERROR", "Token tidak ditemukan")
            }
        }
    }

    fun fetchPh(unitId: String) {
        AuthManager.getToken { token ->
            if (token != null) {
                viewModelScope.launch {
                    RetrofitInstance.apiService.getLatestph(unitId, "Bearer $token")
                        .enqueue(object : Callback<ph_reading> {
                            override fun onResponse(
                                call: Call<ph_reading>,
                                response: Response<ph_reading>
                            ) {
                                if (response.isSuccessful) {
                                    val phValue = response.body()?.latestData?.value
                                    Log.d("API_SUCCESS", "Ph: $phValue")

                                    viewModelScope.launch {
                                        _ph.emit(phValue)
                                    }
                                } else {
                                    Log.e("API_ERROR", "Failed: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<ph_reading>, t: Throwable) {
                                Log.e("API_FAILURE", "Error fetching temperature", t)
                            }
                        })
                }
            } else {
                Log.e("AUTH_ERROR", "Token tidak ditemukan")
            }
        }
    }

    fun fetchTds(unitId: String) {
        AuthManager.getToken { token ->
            if (token != null) {
                viewModelScope.launch {
                    RetrofitInstance.apiService.getLatestTds(unitId, "Bearer $token")
                        .enqueue(object : Callback<tds_reading> {
                            override fun onResponse(
                                call: Call<tds_reading>,
                                response: Response<tds_reading>
                            ) {
                                if (response.isSuccessful) {
                                    val tdsValue = response.body()?.latestData?.value
                                    Log.d("API_SUCCESS", "Tds: $tdsValue")

                                    viewModelScope.launch {
                                        _tds.emit(tdsValue)
                                    }
                                } else {
                                    Log.e("API_ERROR", "Failed: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<tds_reading>, t: Throwable) {
                                Log.e("API_FAILURE", "Error fetching temperature", t)
                            }
                        })
                }
            } else {
                Log.e("AUTH_ERROR", "Token tidak ditemukan")
            }
        }
    }

    fun startRealtimeUpdates(unitId: String) {
        // Dipanggil sekali waktu LaunchedEffect di awal
        fetchTurbidity(unitId)
        fetchTemperature(unitId)
        fetchPh(unitId)
        fetchTds(unitId)
    }

    fun refreshSensorData(unitId: String) {
        // Sama, tapi dipicu dari swipe-to-refresh
        fetchTurbidity(unitId)
        fetchTemperature(unitId)
        fetchPh(unitId)
        fetchTds(unitId)
    }


}
