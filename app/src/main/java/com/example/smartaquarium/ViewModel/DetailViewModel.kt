package com.example.smartaquarium.ViewModel

import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry

class DetailViewModel : ViewModel() {
    private val _sensorDataPoints = mutableStateOf<List<Entry>>(emptyList())
    val sensorDataPoints: State<List<Entry>> = _sensorDataPoints

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        val data = listOf(
            Entry(1f, 0f), // Senin - Aman
            Entry(2f, 1f), // Selasa - Berisiko
            Entry(3f, 2f), // Rabu - Berbahaya
            Entry(4f, 1f), // Kamis - Berisiko
            Entry(5f, 0f), // Jumat - Aman
            Entry(6f, 2f), // Sabtu - Berbahaya
            Entry(7f, 1f)  // Minggu - Berisiko
        )
        _sensorDataPoints.value = data
    }
}

