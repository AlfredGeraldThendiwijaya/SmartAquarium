package com.example.smartaquarium.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartaquarium.network.ApiService
import com.example.smartaquarium.network.RetrofitInstance
import com.example.smartaquarium.network.ScheduleRequest
import com.example.smartaquarium.network.ScheduleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ScheduleViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitInstance.apiService

    // ✅ Perbaiki: Sekarang menyimpan List<ScheduleData>
    private val _schedules = MutableStateFlow<List<ScheduleData>>(emptyList())
    val schedules: StateFlow<List<ScheduleData>> = _schedules


    @RequiresApi(Build.VERSION_CODES.O)

    fun addSchedule(unitId: String, newTime: String) {
        viewModelScope.launch {
            try {
                val response = apiService.addSchedule(ScheduleRequest(idUnit = unitId, time = newTime))

                if (response.message.contains("berhasil", true)) { // Pastikan response sukses
                    Log.d("ScheduleViewModel", "Jadwal berhasil ditambahkan: $newTime")

                    // ✅ Jangan tambahkan secara manual, langsung refresh dari API
                    getSchedules(unitId)

                } else {
                    Log.e("ScheduleViewModel", "Gagal tambah jadwal: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Error: ${e.message}")
            }
        }
    }



    fun getSchedules(unitId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getServoSchedule(unitId)
                val scheduleList = response.schedules ?: emptyList()

                _schedules.value = scheduleList // ✅ Kalau kosong, tetap update biar UI nggak crash

                Log.d("ScheduleViewModel", "Jadwal tersimpan: $scheduleList")
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    Log.e("ScheduleViewModel", "Unit ID tidak ditemukan (404)")
                    _schedules.value = emptyList() // ✅ Jangan crash, biar UI bisa handle
                } else {
                    Log.e("ScheduleViewModel", "Error API: ${e.message()}")
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Error lainnya: ${e.message}")
            }
        }
    }

    fun deleteSchedule(unitId: String, scheduleId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteSchedule(unitId, scheduleId)

                // ✅ Cek ulang pesan yang diterima API
                Log.d("ScheduleViewModel", "Response message: ${response.message}")

                if (response.message.lowercase().contains("berhasil dihapus")) {
                    Log.d("ScheduleViewModel", "Jadwal $scheduleId berhasil dihapus")

                    // ✅ Update UI secara langsung
                    _schedules.value = _schedules.value.filterNot { it.id == scheduleId }

                } else {
                    Log.e("ScheduleViewModel", "Gagal hapus jadwal: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "DELETE Error: ${e.message}")
            }
        }
    }




    // ✅ Fungsi untuk validasi format waktu (Pastikan hanya "HH:mm")
    private fun validateTimeFormat(time: String): String? {
        return try {
            val parsedTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
            parsedTime.format(DateTimeFormatter.ofPattern("HH:mm")) // Kembalikan string yang valid
        } catch (e: DateTimeParseException) {
            null // Return null jika format salah
        }
    }
}
