package com.example.smartaquarium.ViewUI

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.example.smartaquarium.ui.theme.darkgray
import com.example.smartaquarium.ui.theme.navyblue
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

@SuppressLint("UnusedBoxWithConstraintsScope")
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
        viewModel.startRealtimeUpdates(aquariumSerial)
    }

    val midnightGradient = listOf(
        0.1f to Color(0xFF193F62),
        0.27f to Color(0xFF0C1C3E),
        0.65f to Color(0xFF0C1C3E),
        0.75f to Color(0xFF0C1C3E),
        1f to Color(0xFF35336B)
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val brush = Brush.radialGradient(
                colorStops = midnightGradient.toTypedArray(),
                center = Offset(0f, 0f),
                radius = hypot(width, height)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            )

            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    coroutineScope.launch {
                        viewModel.refreshSensorData(aquariumSerial)
                        delay(1000)
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
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = "Aquarium Info",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        IconButton(onClick = { showDialog.value = true }) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Info Icon",
                                    tint = Color(0xFFced2e4)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailInfoCard(
                        name = aquariumName,
                        id = aquariumSerial,
                        navController = navController,
                        aquariumSerial = aquariumSerial
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            text = "Detail Info:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xffe1e8ef))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GaugeMeterWithStatus(
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
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF5E3))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "\uD83D\uDFE2 Water Quality is Safe",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = darkgray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Safe water duration:",
                                    fontSize = 14.sp,
                                    color = darkgray
                                )
                                Text(
                                    text = "8 days 16 hours",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = darkgray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "\uD83D\uDCCB Predicted to worsen on:",
                                    fontSize = 14.sp,
                                    color = darkgray
                                )
                                Text(
                                    text = "2023-02-04 03:00:00",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = darkgray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("schedule/$aquariumSerial") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colorStops = arrayOf(
                                        0.02f to Color(0xFFD0F861),
                                        0.56f to Color(0xFFA4F869),
                                        0.97f to Color(0xFF3FAE3B)
                                    ),
                                    start = Offset.Zero,
                                    end = Offset.Infinite
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = "Set Feeding Schedule",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = navyblue
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    confirmButton = {
                        TextButton(
                            onClick = { showDialog.value = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = navyblue)
                        ) {
                            Text("Close", color = navyblue)
                        }
                    },
                    title = {
                        Text(
                            text = "Indicator Explanation",
                            fontWeight = FontWeight.Bold,
                            color = darkgray
                        )
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoItem(R.drawable.suhu, "Temperature", "Indicates the water temperature in the aquarium.")
                            InfoItem(R.drawable.ph, "pH", "Measures the acidity level of the water.")
                            InfoItem(R.drawable.ppm, "TDS", "Indicates the total dissolved solids (ppm).")
                            InfoItem(R.drawable.turbidity, "Turbidity", "Indicates the clarity level of the water (NTU).")
                            InfoItem(R.drawable.risk, "Risk Status", "Displays Safe, Risky, or Dangerous status.")
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

