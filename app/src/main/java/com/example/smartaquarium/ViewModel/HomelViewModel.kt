package com.example.smartaquarium.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartaquarium.network.AddUnitRequest
import com.example.smartaquarium.network.AquariumResponse
import com.example.smartaquarium.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    private val _aquariums = MutableStateFlow<List<Aquarium>>(emptyList()) // Gunakan StateFlow
    val aquariums: StateFlow<List<Aquarium>> get() = _aquariums
    init {
        fetchAquariums() // Auto-fetch saat ViewModel dibuat
    }
    fun fetchAquariums() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users/$userId/units")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val aquariumList = mutableListOf<Aquarium>()
                for (childSnapshot in snapshot.children) {
                    Log.d("Firebase", "Data: ${childSnapshot.value}")
                    val aquarium = childSnapshot.getValue(Aquarium::class.java)
                    aquarium?.let { aquariumList.add(it) }
                }
                _aquariums.value = aquariumList
                Log.d("ViewModel", "Aquarium list updated: $aquariumList")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data: ${error.message}")
            }
        })
    }


    fun addAquarium(name: String, serial: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val request = AddUnitRequest(userId, serial, name)

        viewModelScope.launch {
            try {
                RetrofitInstance.apiService.addAquarium(request)
                fetchAquariums() // Refresh daftar akuarium setelah tambah unit
            } catch (e: Exception) {
                println("Error adding aquarium: ${e.message}")
            }
        }
    }
}
