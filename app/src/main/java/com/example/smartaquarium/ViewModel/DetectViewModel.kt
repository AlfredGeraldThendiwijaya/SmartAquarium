package com.example.smartaquarium.ViewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
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
import java.text.SimpleDateFormat
import java.util.*

class DetectViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _latestImageUrl = MutableStateFlow<String?>(null)
    val latestImageUrl: StateFlow<String?> = _latestImageUrl

    private val _predictionResult = MutableStateFlow<String?>(null)
    val predictionResult: StateFlow<String?> = _predictionResult

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _isWirelessImage = MutableStateFlow(false)
    val isWirelessImage: StateFlow<Boolean> = _isWirelessImage

    private var capturedImageFile: File? = null
    private var pendingImageFile: File? = null

    // Public getter for UI
    val currentCapturedImageFile: File?
        get() = capturedImageFile

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
                    // Trigger Camera
                    val triggerConnection = URL(triggerUrl).openConnection() as HttpURLConnection
                    triggerConnection.requestMethod = "POST"
                    triggerConnection.doOutput = true
                    triggerConnection.setRequestProperty("Content-Type", "application/json")
                    triggerConnection.setRequestProperty("Authorization", "Bearer $token")
                    triggerConnection.connect()

                    val triggerCode = triggerConnection.responseCode
                    triggerConnection.disconnect()
                    Log.d("DETECT_VM", "Trigger response code: $triggerCode")

                    if (triggerCode != 200) {
                        _isLoading.value = false
                        onFailure?.invoke()
                        return@launch
                    }

                    delay(10000)

                    // Get Latest Image URL
                    val imageRequest = URL(imageUrlApi).openConnection() as HttpURLConnection
                    imageRequest.requestMethod = "GET"
                    imageRequest.setRequestProperty("Cache-Control", "no-cache")
                    imageRequest.setRequestProperty("Pragma", "no-cache")
                    imageRequest.setRequestProperty("Authorization", "Bearer $token")
                    imageRequest.useCaches = false
                    imageRequest.connect()

                    val responseCode = imageRequest.responseCode
                    val responseMessage = imageRequest.responseMessage
                    Log.d("DETECT_VM", "Image URL response: $responseCode - $responseMessage")

                    if (responseCode == 200) {
                        val reader = imageRequest.inputStream.bufferedReader()
                        val jsonString = reader.readText()
                        imageRequest.disconnect()

                        val rawImageUrl = JSONObject(jsonString).getString("url")
                        Log.d("DETECT_VM", "✅ rawImageUrl: $rawImageUrl")

                        val imageUrl = fetchLatestImageWithinTime(rawImageUrl, 16000)

                        if (imageUrl != null) {
                            Log.d("DETECT_VM", "✅ Image URL final: $imageUrl")
                            _latestImageUrl.value = imageUrl
                            _isWirelessImage.value = true
                        } else {
                            Log.w("DETECT_VM", "⚠️ Gagal mengambil gambar yang valid")
                            onFailure?.invoke()
                        }
                    } else {
                        Log.w("DETECT_VM", "⚠️ Image URL not ready: $responseCode - $responseMessage")
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
                // Tambahkan cache buster HANYA kalau URL belum mengandung ?
                val finalUrl = if (baseUrl.contains("?")) {
                    "$baseUrl&t=${System.currentTimeMillis()}"
                } else {
                    "$baseUrl?t=${System.currentTimeMillis()}"
                }

                val connection = URL(finalUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Cache-Control", "no-cache")
                connection.setRequestProperty("Pragma", "no-cache")
                connection.setRequestProperty("User-Agent", "Mozilla/5.0") // Tambahan penting
                connection.useCaches = false
                connection.connect()

                val code = connection.responseCode
                val msg = connection.responseMessage
                Log.d("DETECT_VM", "Check image: $finalUrl -> $code $msg")

                if (code == 200) {
                    val imageBytes = connection.inputStream.readBytes()
                    connection.disconnect()

                    if (imageBytes.size > expectedSizeThreshold) {
                        Log.d("DETECT_VM", "✅ Gambar diterima (${imageBytes.size} bytes)")
                        return finalUrl
                    } else {
                        Log.w("DETECT_VM", "⚠️ Gambar terlalu kecil (${imageBytes.size} bytes), retrying...")
                    }
                } else {
                    Log.w("DETECT_VM", "⚠️ Gagal ambil gambar: $code $msg")
                    connection.disconnect()
                }

                Thread.sleep(1500)
            } catch (e: Exception) {
                Log.e("DETECT_VM", "❌ Fetch image attempt gagal", e)
            }
        }

        return null
    }

    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).also {
            capturedImageFile = it
        }
    }
    fun setPendingImageFile(file: File) {
        pendingImageFile = file
    }
    fun getFinalImageFileIfExists(success: Boolean): File? {
        return if (success) pendingImageFile else null
    }

    fun confirmCapturedImage() {
        // bisa set state lain kalau mau
        pendingImageFile = null
    }

    fun clearPendingImageFile() {
        pendingImageFile = null
    }
    fun getImageUri(context: Context): Uri {
        return capturedImageFile?.let {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                it
            )
        } ?: throw IllegalStateException("File gambar belum dibuat.")
    }

    fun analyzeImage(
        selectedUri: Uri?,
        capturedImageFile: File?,
        imageUrl: String?
    ) {
        _isWirelessImage.value = false

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

                    capturedImageFile != null -> capturedImageFile

                    imageUrl != null -> {
                        try {
                            val connection = URL(imageUrl).openConnection() as HttpURLConnection
                            connection.setRequestProperty("Cache-Control", "no-cache")
                            connection.setRequestProperty("Pragma", "no-cache")
                            connection.useCaches = false
                            connection.connect()

                            if (connection.responseCode != 200) {
                                _predictionResult.emit("❌ Gagal mengambil gambar dari URL (code: ${connection.responseCode})")
                                return@launch
                            }

                            val inputStream = connection.inputStream
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream.close()

                            if (bitmap == null) {
                                _predictionResult.emit("❌ File dari URL tidak bisa dibaca sebagai gambar.")
                                return@launch
                            }

                            val tempFile = File.createTempFile("from_url", ".jpg")
                            val outputStream = FileOutputStream(tempFile)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.close()
                            tempFile
                        } catch (e: Exception) {
                            Log.e("DetectViewModel", "❌ Error ambil & decode gambar dari URL", e)
                            _predictionResult.emit("❌ Gagal memproses gambar dari URL: ${e.message}")
                            return@launch
                        }
                    }

                    else -> null
                }

                if (imageFile == null) {
                    _predictionResult.emit("❌ Gagal menyiapkan file gambar.")
                    return@launch
                }

                Log.d("DetectViewModel", "📸 File path: ${imageFile.absolutePath}, size: ${imageFile.length()} bytes")

                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                val response = AnalyzerRetrofitInstance.api.analyzeImage(body)

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: "{}"
                    val json = JSONObject(responseBody)
                    val result = json.optString("result", "Unknown")

                    val formattedResult = when (result.lowercase()) {
                        "healthy" -> "Result : Healthy"
                        "sick" -> "Result : Sick"
                        else -> "Unknown result: $result"
                    }

                    _predictionResult.emit(formattedResult)
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        "Tidak bisa baca error body (${e.message})"
                    }

                    Log.e("DetectViewModel", "❌ Analysis failed. Code: ${response.code()}, Reason: $errorBody")
                    _predictionResult.emit("❌ Gagal analisis. ${response.code()} | $errorBody")
                }
            } catch (e: Exception) {
                Log.e("DetectViewModel", "❌ Exception analyzeImage", e)
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

//    fun clearWirelessImage() {
//        viewModelScope.launch {
//            _latestImageUrl.emit(null)
//            _isWirelessImage.emit(false)
//        }
//    }

    fun hasValidImageSelected(): Boolean {
        return capturedImageFile != null || _latestImageUrl.value != null
    }

    fun clearAllImageSources() {
        capturedImageFile = null
        viewModelScope.launch {
            _latestImageUrl.emit(null)
            _isWirelessImage.emit(false)
        }
    }
}
