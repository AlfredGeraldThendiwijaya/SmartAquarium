package com.example.smartaquarium.ViewModel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartaquarium.network.analyzer.AnalyzerRetrofitInstance
import com.example.smartaquarium.utils.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DetectViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _latestImageUrl = MutableStateFlow<String?>(null)
    val latestImageUrl: StateFlow<String?> = _latestImageUrl

    private val _predictionResult = MutableStateFlow<String?>(null)
    val predictionResult: StateFlow<String?> = _predictionResult

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    fun triggerWirelessCamera(unitId: String, onFailure: (() -> Unit)? = null) {
        _isLoading.value = true
        val triggerUrl = "https://us-central1-smart-aquarium-fe20f.cloudfunctions.net/api/trigger-capture/$unitId"
        val imageUrlApi = "https://us-central1-smart-aquarium-fe20f.cloudfunctions.net/api/get-latest-image/$unitId"

        AuthManager.getToken { token ->
            if (token == null) {
                Log.e("DETECT_VM", "❌ Token tidak ditemukan")
                onFailure?.invoke()
                _isLoading.value = false
                return@getToken
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val triggerConnection = URL(triggerUrl).openConnection() as HttpURLConnection
                    triggerConnection.requestMethod = "POST"
                    triggerConnection.doOutput = true
                    triggerConnection.setRequestProperty("Content-Type", "application/json")
                    triggerConnection.setRequestProperty("Authorization", "Bearer $token")
                    triggerConnection.connect()

                    val triggerCode = triggerConnection.responseCode
                    Log.d("DETECT_VM", "Trigger response code: $triggerCode")
                    triggerConnection.disconnect()

                    if (triggerCode != 200) {
                        _isLoading.value = false
                        onFailure?.invoke()
                        return@launch
                    }

                    delay(10000)

                    val imageRequest = URL(imageUrlApi).openConnection() as HttpURLConnection
                    imageRequest.requestMethod = "GET"
                    imageRequest.setRequestProperty("Cache-Control", "no-cache")
                    imageRequest.setRequestProperty("Pragma", "no-cache")
                    imageRequest.useCaches = false
                    imageRequest.connect()

                    val responseCode = imageRequest.responseCode
                    Log.d("DETECT_VM", "Image URL response: $responseCode")

                    if (responseCode == 200) {
                        val reader = imageRequest.inputStream.bufferedReader()
                        val jsonString = reader.readText()
                        imageRequest.disconnect()

                        val rawImageUrl = JSONObject(jsonString).getString("url")

                        val imageUrl = fetchLatestImageWithinTime(rawImageUrl, 16000)
                        if (imageUrl != null) {
                            _latestImageUrl.value = imageUrl
                        } else {
                            Log.w("DETECT_VM", "Gagal mengambil gambar yang valid")
                            onFailure?.invoke()
                        }
                    } else {
                        Log.w("DETECT_VM", "Image URL not ready: $responseCode")
                        onFailure?.invoke()
                    }
                } catch (e: Exception) {
                    Log.e("DETECT_VM", "❌ Error triggering or fetching image", e)
                    onFailure?.invoke()
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun fetchLatestImageWithinTime(baseUrl: String, timeoutMs: Long): String? {
        val expectedSizeThreshold = 1500
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                val finalUrl = "$baseUrl&t=${System.currentTimeMillis()}"
                val connection = URL(finalUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Cache-Control", "no-cache")
                connection.setRequestProperty("Pragma", "no-cache")
                connection.useCaches = false
                connection.connect()

                val code = connection.responseCode
                if (code == 200) {
                    val imageBytes = connection.inputStream.readBytes()
                    if (imageBytes.size > expectedSizeThreshold) {
                        connection.disconnect()
                        return finalUrl
                    } else {
                        Log.w("DETECT_VM", "Gambar terlalu kecil, retrying...")
                    }
                }
                connection.disconnect()
                Thread.sleep(1500)
            } catch (e: Exception) {
                Log.e("DETECT_VM", "❌ Fetch image attempt gagal", e)
            }
        }

        return null
    }

    fun analyzeImage(selectedUri: Uri?, capturedBitmap: Bitmap?, imageUrl: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isUploading.value = true
            try {
                val imageFile = when {
                    selectedUri != null -> {
                        val inputStream = getApplication<Application>().contentResolver.openInputStream(selectedUri)
                        val tempFile = File.createTempFile("gallery", ".jpg")
                        val outputStream = FileOutputStream(tempFile)
                        inputStream?.copyTo(outputStream)
                        outputStream.close()
                        inputStream?.close()
                        tempFile
                    }
                    capturedBitmap != null -> {
                        val tempFile = File.createTempFile("camera", ".jpg")
                        val outputStream = FileOutputStream(tempFile)
                        capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.close()
                        tempFile
                    }
                    imageUrl != null -> {
                        val connection = URL(imageUrl).openConnection() as HttpURLConnection
                        connection.connect()
                        val inputStream = connection.inputStream
                        val tempFile = File.createTempFile("downloaded", ".jpg")
                        val outputStream = FileOutputStream(tempFile)
                        inputStream.copyTo(outputStream)
                        outputStream.close()
                        inputStream.close()
                        tempFile
                    }
                    else -> null
                }

                if (imageFile == null) {
                    _predictionResult.emit("❌ Gagal menyiapkan file gambar.")
                    return@launch
                }

                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                val response = AnalyzerRetrofitInstance.api.analyzeImage(body)

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: "{}"
                    val json = JSONObject(responseBody)
                    val result = json.optString("result", "Unknown")

                    val formattedResult = when (result.lowercase()) {
                        "red_melon" -> "Red Melon (Healthy)"
                        "blue_diamond" -> "Blue Diamond (Healthy)"
                        "anomaly" -> "⚠️ Anomaly detected!"
                        else -> "Unknown result: $result"
                    }

                    _predictionResult.emit(formattedResult)
                } else {
                    _predictionResult.emit("❌ Analysis failed. Code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ANALYZE", "❌ Error:", e)
                _predictionResult.emit("❌ Terjadi kesalahan: ${e.message}")
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun resetPrediction() {
        viewModelScope.launch {
            _predictionResult.emit(null)
        }
    }

    fun clearWirelessImage() {
        viewModelScope.launch {
            _latestImageUrl.emit(null)
        }
    }
}
