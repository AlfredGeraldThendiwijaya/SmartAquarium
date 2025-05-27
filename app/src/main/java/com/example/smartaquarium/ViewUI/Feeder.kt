package com.example.smartaquarium.ViewUI

import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartaquarium.Component.CustomTimePicker
import com.example.smartaquarium.Component.ScheduleCard
import com.example.smartaquarium.ViewModel.ScheduleViewModel
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.darkgray
import com.example.smartaquarium.ui.theme.navyblue
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.hypot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(onDismiss: () -> Unit, onTimeSet: (String) -> Unit) {
    var selectedHour by remember { mutableStateOf(9) }
    var selectedMinute by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Schedule ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = navyblue)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTimePicker(
            selectedHour = selectedHour,
            selectedMinute = selectedMinute
        ) { hour, minute ->
            selectedHour = hour
            selectedMinute = minute
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = navyblue)
            ) {
                Text("Cancel", color = Color.White)
            }
            Button(
                onClick = {
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                    onTimeSet(formattedTime)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = navyblue)
            ) {
                Text("OK", color = Color.White)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController, aquariumSerial: String, viewModel: ScheduleViewModel = viewModel()) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val schedules by viewModel.schedules.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedScheduleId by remember { mutableStateOf<String?>(null) }
    val midnightGradient = listOf(
        0.1f to Color(0xFF193F62),
        0.27f to Color(0xFF0C1C3E),
        0.65f to Color(0xFF0C1C3E),
        0.75f to Color(0xFF0C1C3E),
        1f to Color(0xFF35336B)
    )
    LaunchedEffect(aquariumSerial) {
        viewModel.getSchedules(aquariumSerial)
//        viewModel.deleteSchedule(aquariumSerial, selectedScheduleId ?: "")
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
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
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.Rounded.KeyboardArrowLeft,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint =  Color.White
                                )
                            }
                            Text(
                                text = "Feeding Schedule",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = { showBottomSheet = true }) {
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
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Jadwal",
                                    tint = Color(0xFFced2e4)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        if (schedules.isEmpty()) {
                            Text(
                                text = "no scheduling",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            schedules.forEach { schedule ->
                                ScheduleCard(
                                    schedule = schedule,
                                    onDelete = { scheduleId ->
                                        selectedScheduleId = scheduleId
                                        showDialog = true
                                    }
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        containerColor = Color.White, // ⬅️ Explicit putih
                        title = {
                            Text(
                                "Delete Schedule",
                                fontWeight = FontWeight.Bold,
                                color = navyblue
                            )
                        },
                        text = {
                            Text(
                                "Are you sure want to delete this schedule?",
                                color = Color.Black
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                selectedScheduleId?.let {
                                    viewModel.deleteSchedule(
                                        aquariumSerial,
                                        it
                                    )
                                }
                                showDialog = false
                            }) {
                                Text("Delete", color = navyblue)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancel", color = navyblue)
                            }
                        }
                    )
                }


                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        containerColor = Color.White,
                        dragHandle = { BottomSheetDefaults.DragHandle() }
                    ) {
                        BottomSheetContent(
                            onDismiss = { showBottomSheet = false },
                            onTimeSet = { newTime ->
                                viewModel.addSchedule(aquariumSerial, newTime)
                                showBottomSheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}