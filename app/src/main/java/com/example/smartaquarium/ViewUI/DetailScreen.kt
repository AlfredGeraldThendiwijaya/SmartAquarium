package com.example.smartaquarium.ViewUI

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.navigation.NavController
import com.example.smartaquarium.Component.DetailInfoCard
import com.example.smartaquarium.Component.GaugeMeterWithStatus
import com.example.smartaquarium.Component.LineChartComponent
import com.example.smartaquarium.ViewModel.DetailViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.navyblue

@Composable
fun DetailScreen(
    navController: NavController,
    aquariumName: String,
    aquariumSerial: String,
    viewModel: DetailViewModel = viewModel()
) {
    val sensorData by viewModel.sensorDataPoints.collectAsState()
    val turbidity by viewModel.turbidity.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val scrollState = rememberScrollState() // Tambahkan state scroll
    LaunchedEffect(aquariumSerial) {
        Log.d("DETAIL_SCREEN", "Fetching turbidity for serial: $aquariumSerial")
        viewModel.startRealtimeUpdates(aquariumSerial)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState), // Tambahkan scroll di sini
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(top = 50.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = navyblue
                    )
                }
                Text(
                    text = "Info Aquarium",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = navyblue
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DetailInfoCard(name = aquariumName, id = aquariumSerial)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Detail Info: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = navyblue,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LineChartComponent(sensorData)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GaugeMeterWithStatus(
                        percentage = 51,
                        ph = 7.2f.toDouble(),
                        temperature = (temperature ?: 0.0).toDouble(),
                        ntu = (turbidity ?: 0.0).toDouble(),
                        pph = 150f.toDouble(),
                        viewModel = viewModel
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("schedule") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Atur Jadwal Pemberian Makan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}