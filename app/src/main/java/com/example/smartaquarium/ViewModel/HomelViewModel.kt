package com.example.smartaquarium.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
data class Aquarium(val name: String, val serial: String)

class HomeViewModel : ViewModel() {
    private val _aquariums = mutableStateListOf<Aquarium>()
    val aquariums: List<Aquarium> get() = _aquariums

    fun addAquarium(name: String, serial: String) {
        _aquariums.add(Aquarium(name, serial))
    }
}



