package com.example.smartaquarium.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartaquarium.network.AddUnitRequest
import com.example.smartaquarium.network.AquariumResponse
import com.example.smartaquarium.network.RetrofitInstance
import com.example.smartaquarium.network.RetrofitInstance.apiService
import com.example.smartaquarium.network.ScheduleData
import com.example.smartaquarium.network.deleteUnit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Aquarium(
    val unitId: String = "",
    val unitName: String = "",
    val createdAt: String = ""
) {
    constructor() : this("", "", "")
}



    class HomeViewModel : ViewModel() {
        private val _aquariums = mutableStateListOf<Aquarium>() // 🔥 Ganti ke StateList
        val aquariums: List<Aquarium> get() = _aquariums

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading

        enum class SortOrder { ASCENDING, DESCENDING }

        private val _deleteUnit = MutableStateFlow<List<deleteUnit>>(emptyList())
        val deleteUnit: StateFlow<List<deleteUnit>> = _deleteUnit

        private var currentSortOrder = SortOrder.ASCENDING // default

        init {
            Log.d("ViewModel", "HomeViewModel DI-INISIALISASI")
            fetchAquariums()
        }

        fun sortAquariumsByName() {
            val sorted = when (currentSortOrder) {
                SortOrder.ASCENDING -> _aquariums.sortedBy { it.unitName.lowercase() }
                SortOrder.DESCENDING -> _aquariums.sortedByDescending { it.unitName.lowercase() }
            }

            _aquariums.clear()
            _aquariums.addAll(sorted)

            // toggle sort order biar pas diklik lagi bisa kebalik
            currentSortOrder = if (currentSortOrder == SortOrder.ASCENDING)
                SortOrder.DESCENDING else SortOrder.ASCENDING
        }

        fun fetchAquariums() {
            Log.d("ViewModel", "fetchAquariums() dipanggil")
            viewModelScope.launch(Dispatchers.IO) {
                _isLoading.value = true

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val dbRef = FirebaseDatabase.getInstance().getReference("users/$userId/units")

                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val aquariumSet = mutableSetOf<Aquarium>() // 🔥 Pakai Set untuk menghindari duplikasi

                        snapshot.children.forEach { data ->
                            val aquarium = data.getValue(Aquarium::class.java)
                            if (aquarium != null) aquariumSet.add(aquarium)
                        }

                        Log.d("Firebase", "Data diterima: $aquariumSet") // Debug

                        viewModelScope.launch(Dispatchers.Main) {
                            _aquariums.clear() // Pastikan list dikosongkan dulu
                            _aquariums.addAll(aquariumSet)
                            _isLoading.value = false
                            Log.d("ViewModel", "Aquarium list diperbarui: $_aquariums")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Error fetching data: ${error.message}")
                        viewModelScope.launch(Dispatchers.Main) {
                            _isLoading.value = false
                        }
                    }
                })
            }
        }


    fun addAquarium(name: String, serial: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val request = AddUnitRequest(userId, serial, name)

        viewModelScope.launch {
            try {
                RetrofitInstance.apiService.addAquarium(request)
                fetchAquariums()
                delay(1000) // ✅ Tunggu biar Firebase update otomatis

            } catch (e: Exception) {
                println("Error adding aquarium: ${e.message}")
            }
        }
    }

        fun deleteSchedule(userId: String, unitId: String) {
            viewModelScope.launch {
                try {
                    val response = apiService.deleteUnit(userId,unitId)

                    // ✅ Cek ulang pesan yang diterima API
                    Log.d("HomeViewModel", "Response message: ${response.message}")

                    if (response.message.lowercase().contains("berhasil dihapus")) {
                        Log.d("HomeViewModel", "Jadwal $unitId berhasil dihapus")

                        // ✅ Update UI secara langsung
                        fetchAquariums()

                    } else {
                        Log.e("HomeViewModel", "Gagal hapus unit: ${response.message}")
                    }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "DELETE Error: ${e.message}")
                }
            }
        }
}
