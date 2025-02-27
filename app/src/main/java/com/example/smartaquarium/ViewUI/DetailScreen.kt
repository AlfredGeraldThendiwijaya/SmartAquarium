package com.example.smartaquarium.ViewUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartaquarium.Component.AddAquariumDialog
import com.example.smartaquarium.Component.DetailInfoCard
import com.example.smartaquarium.Component.LineChartComponent
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.DetailViewModel
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.navyblue

@Composable


fun DetailScreen(navController: NavController, aquariumName: String, aquariumSerial: String,viewModel: DetailViewModel = viewModel()) {
    val sensorData by viewModel.sensorDataPoints
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        Column {
            Row(
                modifier = Modifier.padding(top = 50.dp),
                verticalAlignment = Alignment.CenterVertically
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                ) }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp), // Biar sudutnya melengkung
                        colors = CardDefaults.cardColors(containerColor = Color.White) // Background putih
                    ) {
                        LineChartComponent(sensorData) // Tampilkan grafik
                    }
                }

            }
        }
    }
}
