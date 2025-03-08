package com.example.smartaquarium.ViewUI

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartaquarium.Component.CustomTimePicker
import com.example.smartaquarium.Component.ScheduleCard
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.navyblue
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(onDismiss: () -> Unit) {
    var selectedHour by remember { mutableStateOf(9) }
    var selectedMinute by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Atur Jadwal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = navyblue)
        Spacer(modifier = Modifier.height(16.dp))

        // Custom Time Picker
        CustomTimePicker(
            selectedHour = selectedHour,
            selectedMinute = selectedMinute
        ) { hour, minute ->
            selectedHour = hour
            selectedMinute = minute
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol OK dan Simpan dalam satu baris
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /* Bisa ditambahkan fungsi penyimpanan */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = navyblue)
            ) {
                Text("OK", color = Color.White)
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = navyblue)
            ) {
                Text("Simpan", color = Color.White)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(accentMint)
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
                        tint = navyblue
                    )
                }
                Text(
                    text = "Penjadwalan Makan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = navyblue
                )
            }
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Jadwal",
                    tint = navyblue
                )
            }
        }
        ScheduleCard()
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = Color.White, // Pastikan warna solid
            dragHandle = { BottomSheetDefaults.DragHandle() } // ✅ Tambahkan kembali pegangan
        ) {
            BottomSheetContent {
                showBottomSheet = false
            }
        }
    }
}
