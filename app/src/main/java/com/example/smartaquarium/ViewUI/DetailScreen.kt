package com.example.smartaquarium.ViewUI

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartaquarium.Component.DetailInfoCard
import com.example.smartaquarium.Component.GaugeMeterWithStatus
import com.example.smartaquarium.Component.InfoItem
import com.example.smartaquarium.Component.LineChartComponent
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.DetailViewModel
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.navyblue
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val ph by viewModel.ph.collectAsState()
    val tds by viewModel.tds.collectAsState()
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(aquariumSerial) {
        Log.d("DETAIL_SCREEN", "Fetching turbidity for serial: $aquariumSerial")
        viewModel.startRealtimeUpdates(aquariumSerial)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                isRefreshing = true
                coroutineScope.launch {
                    viewModel.refreshSensorData(aquariumSerial)
                    delay(1000) // Simulasi loading agar user bisa lihat efek refresh
                    isRefreshing = false
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 50.dp, start = 8.dp, end = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info Icon",
                            tint = navyblue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                DetailInfoCard(name = aquariumName, id = aquariumSerial, navController = navController, aquariumSerial = aquariumSerial)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GaugeMeterWithStatus(
//                            percentage = 85,
                            ph = (ph ?: 0.0).toDouble(),
                            temperature = (temperature ?: 0.0).toDouble(),
                            ntu = (turbidity ?: 0.0).toDouble(),
                            pph = (tds ?: 0.0).toDouble(),
                            viewModel = viewModel
                        )
                    }
                }


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF5E3)) // Hijau muda untuk status aman
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🟢 Kualitas Air Aman",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32) // Hijau gelap
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Total air sehat bertahan selama:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "8 hari 16 jam",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🗓️ Setelah itu memburuk pada:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "2023-02-04 03:00:00",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("schedule/${aquariumSerial}") },
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

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Tutup")
                    }
                },
                title = {
                    Text(
                        text = "Penjelasan Indikator",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoItem(R.drawable.suhu, "Suhu", "Menunjukkan suhu air dalam akuarium.")
                        InfoItem(R.drawable.ph, "pH", "Mengukur tingkat keasaman air.")
                        InfoItem(R.drawable.turbidity, "Turbidity", "Menunjukkan tingkat kejernihan air (NTU).")
                        InfoItem(R.drawable.tds, "TDS", "Menunjukkan jumlah padatan terlarut (ppm).")
                        InfoItem(R.drawable.risk, "Status Resiko", "Menunjukkan status Aman, Beresiko, atau Berbahaya.")
                    }
                }
            )
        }
    }
}
