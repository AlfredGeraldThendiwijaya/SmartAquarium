package com.example.smartaquarium.Component


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.Aquarium
import com.example.smartaquarium.ViewModel.DetailViewModel
import com.example.smartaquarium.network.ScheduleData
import com.example.smartaquarium.network.deleteUnit
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun AddAquariumDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var serial by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Aquarium") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Aquarium Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = serial,
                    onValueChange = { serial = it },
                    label = { Text("Serial Device Number") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && serial.isNotBlank()) {
                    onConfirm(name, serial)
                }
            }) {
                Text("Tambah")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}


@Composable
fun InfoCardContainer(
    onAddAquarium: (String, String) -> Unit,
    aquariumCount: Int,
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var aquariumName by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }
    var isSerialError by remember { mutableStateOf(false) }
    val user = FirebaseAuth.getInstance().currentUser

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        InfoCard(
            modifier = Modifier.align(Alignment.Center),
            aquariumCount = aquariumCount
        )

        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(y = (-30).dp, x = (-15).dp)
                .zIndex(1f)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    isDialogOpen = true
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.add_device),
                contentDescription = "Tambah Akuarium",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Tambah Akuarium") },
            text = {
                Column {
                    OutlinedTextField(
                        value = aquariumName,
                        onValueChange = {
                            aquariumName = it
                            isNameError = it.isBlank()
                        },
                        label = { Text("Nama Akuarium") },
                        placeholder = { Text("Masukkan nama akuarium") },
                        isError = isNameError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isNameError) {
                        Text(
                            text = "Nama akuarium tidak boleh kosong",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = serialNumber,
                        onValueChange = {
                            serialNumber = it
                            isSerialError = it.isBlank()
                        },
                        label = { Text("Serial Number") },
                        placeholder = { Text("Masukkan serial number") },
                        isError = isSerialError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isSerialError) {
                        Text(
                            text = "Serial number tidak boleh kosong",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isNameError = aquariumName.isBlank()
                        isSerialError = serialNumber.isBlank()
                        if (!isNameError && !isSerialError && user != null) {
                            postAquariumData(user.uid, serialNumber, aquariumName) {
                                onAddAquarium(aquariumName, serialNumber)
                                isDialogOpen = false
                            }
                        }
                    }
                ) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White,
            textContentColor = Color.Black
        )
    }
}

fun postAquariumData(userId: String, unitId: String, unitName: String, onSuccess: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val json = JSONObject()
            json.put("userId", userId)
            json.put("unitId", unitId)
            json.put("unitName", unitName)

            val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("https://us-central1-smart-aquarium-fe20f.cloudfunctions.net/api/add-unit")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("FirebaseAPI", "Success: ${response.body?.string()}")
                onSuccess()
            } else {
                Log.e("FirebaseAPI", "Error: ${response.body?.string()}")
            }
        } catch (e: Exception) {
            Log.e("FirebaseAPI", "Exception: ${e.message}")
        }
    }
}






@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    aquariumCount: Int
) {
    val user = FirebaseAuth.getInstance().currentUser
    val displayName = user?.displayName?.split(" ")?.firstOrNull() ?: "Guest"

    Box(
        modifier = modifier
            .width(600.dp)
            .height(170.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Color.Transparent)
    ) {
        // Efek Frosted Glass (Blur)
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.5f),
                        size = size,
                        cornerRadius = CornerRadius(20f, 20f)
                    )
                }
                .blur(25.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hi, $displayName",
                    color = Color(0xff111827),
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(y = (-20).dp)
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "Aquarium already registered : $aquariumCount",
                    color = Color(0xff374151),
                    fontSize = 14.sp
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListAquariumCard(
    modifier: Modifier = Modifier,
    name: String,
    unitId: String,
    percentage: Int, // ⬅️ ambil dari GaugeMeterWithStatus
    statusText: String,
    onClick: () -> Unit,
    onDelete: (String) -> Unit,
) {
    val statusInfo = getStatusInfoFromPercentage(percentage)

    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(107.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onDelete(unitId) }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(107.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.8f))
                .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(10.dp))
        )

        // 🐟 Gambar Aquarium
        Image(
            painter = painterResource(id = R.drawable.fish_tank),
            contentDescription = "fish-tank_2173805 2",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 28.dp)
                .size(50.dp)
        )

        // 🐠 Nama Aquarium
        Text(
            text = name,
            color = Color(0xff4a628a),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 79.dp, y = 28.dp)
        )

        // ⏰ Info Jadwal
        Text(
            text = "Tidak ada penjadwalan pemberian makan",
            color = Color(0xff6c7278),
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 79.dp, y = 67.dp)
        )

        // 🚨 Status Icon & Text
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = statusInfo.iconResId),
                contentDescription = "Status Icon",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusInfo.statusText,
                fontSize = 14.sp,
                color = statusInfo.statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
fun DetailInfoCard(
    name: String,
    id: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            // Gambar
            Image(
                painter = painterResource(id = R.drawable.fish_tank),
                contentDescription = "fish-tank_2173805 2",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    color = Color(0xff4a628a),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "serial ID : $id",
                    color = Color(0xff6c7278),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class StatusInfo(
    val gradientColors: List<Color>,
    val statusText: String,
    val statusColor: Color,
    val iconResId: Int
)

fun getStatusInfoFromPercentage(percentage: Int): StatusInfo {
    return when {
        percentage < 50 -> StatusInfo(
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFFFFC107)),
            statusText = "Aman",
            statusColor = Color(0xFF05B89D),
            iconResId = R.drawable.clean // ✅ icon aman
        )
        percentage in 50..79 -> StatusInfo(
            gradientColors = listOf(Color(0xFFFFC107), Color(0xFFFF9800)),
            statusText = "Beresiko",
            statusColor = Color(0xFFFD5E00),
            iconResId = R.drawable.risk // ✅ icon risiko
        )
        else -> StatusInfo(
            gradientColors = listOf(Color(0xFFFF9800), Color(0xFFD32F2F)),
            statusText = "Berbahaya",
            statusColor = Color.Red,
            iconResId = R.drawable.danger // ✅ icon bahaya
        )
    }
}

@Composable
fun LineChartComponent(dataPoints: List<Entry>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)

                // Buat dataset
                val dataSet = LineDataSet(dataPoints, "Kondisi Sensor")
                dataSet.color = Color.Blue.toArgb()
                dataSet.valueTextColor = Color.Black.toArgb()
                dataSet.setDrawValues(true)
                dataSet.setDrawCircles(true)

                val lineData = LineData(dataSet)
                data = lineData

                // Format Sumbu X (1-7 menjadi Senin-Minggu)
                xAxis.valueFormatter = object : ValueFormatter() {
                    private val days = arrayOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt() - 1
                        return if (index in days.indices) days[index] else ""
                    }
                }
                xAxis.granularity = 1f // Langkah per hari

                // Format Sumbu Y (0-2 menjadi Aman, Berisiko, Berbahaya)
                axisLeft.valueFormatter = object : ValueFormatter() {
                    private val labels = arrayOf("Aman", "Berisiko", "Berbahaya")
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index in labels.indices) labels[index] else ""
                    }
                }
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = 2f
                axisLeft.granularity = 1f

                axisRight.isEnabled = false // Nonaktifkan sumbu kanan
                xAxis.position = XAxis.XAxisPosition.BOTTOM

                invalidate() // Refresh Chart
            }
        }
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun GaugeMeterWithStatus(
    percentage: Int,
    ph: Double,
    temperature: Double,
    pph: Double,
    ntu:Double,
    viewModel: DetailViewModel, // Ambil ViewModel
    modifier: Modifier = Modifier
) {
    val turbidity by viewModel.turbidity.collectAsState() // Mengamati perubahan NTU dari ViewModel
    val temperature by viewModel.temperature.collectAsState()
    val ph by viewModel.ph.collectAsState()
    val tds by viewModel.tds.collectAsState()
    val sweepAngle = (percentage / 100f) * 250f // 250° agar lebih dinamis
    val backgroundAngle = 250f

    val (gradientColors, statusText, statusColor) = when {
        percentage < 50 -> Triple(
            listOf(Color(0xFF4CAF50), Color(0xFFFFC107)),
            "Aman",
            Color(0xFF4A628A)
        ) // Hijau ke Kuning
        percentage in 50..79 -> Triple(
            listOf(Color(0xFFFFC107), Color(0xFFFF9800)),
            "Beresiko",
            Color(0xFF4A628A)
        ) // Kuning ke Oranye
        else -> Triple(
            listOf(Color(0xFFFF9800), Color(0xFFD32F2F)),
            "Berbahaya",
            Color.Red
        ) // Oranye ke Merah
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Gauge Meter
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kualitas Air",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        val strokeWidth = 18.dp.toPx()
                        val startAngle = 145f

                        drawArc(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            startAngle = startAngle,
                            sweepAngle = backgroundAngle,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )

                        drawArc(
                            brush = Brush.linearGradient(gradientColors),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Persentase di tengah
                    Text(
                        text = "$percentage%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Status Air
                Text(
                    text = statusText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Status Parameter
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                val formattedTurbidity = turbidity?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                val formattedTemperature = temperature?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                val formattedPh = ph?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                val formattedTds = tds?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                ParameterItem(R.drawable.suhu, "Suhu", "$formattedTemperature °C")
                ParameterItem(R.drawable.ph, "pH", "$formattedPh pH")
                ParameterItem(R.drawable.turbidity, "NTU", "$formattedTurbidity NTU")
                ParameterItem(R.drawable.tds, "TDS", "$formattedTds ppm")
            }
        }
    }
}
@Composable
fun InfoItem(iconResId: Int, label: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier
                .size(34.dp)
                .padding(end = 8.dp)
        )
        Column {
            Text(text = label, fontWeight = FontWeight.Bold)
            Text(text = description, fontSize = 12.sp)
        }
    }
}


@Composable
fun ParameterItem(iconRes: Int, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = ": $value",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleCard(
    schedule: ScheduleData, // ✅ Ubah dari String → ScheduleData biar ada ID-nya
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isActive by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .combinedClickable(
                onClick = {}, // Klik biasa tidak ada aksi
                onLongClick = { onDelete(schedule.id) } // ✅ Long press untuk hapus
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.clock_ic),
                    contentDescription = "Jam",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = schedule.time, // ✅ Ambil time dari ScheduleData
                    color = Color(0xff4a628a),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xff4a628a),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun CustomTimePicker(
    selectedHour: Int,
    selectedMinute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NumberPicker(
            value = selectedHour,
            range = 0..23,
            onValueChange = { onTimeChange(it, selectedMinute) },
            modifier = Modifier
                .size(120.dp, 180.dp)
                .background(Color.Transparent)
                .drawBehind {},
            dividersColor= Color.Transparent,
            textStyle = TextStyle(
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            label = { String.format("%02d", it) }
        )

        Text(text = " : ", fontSize = 32.sp, modifier = Modifier.padding(horizontal = 8.dp))

        NumberPicker(
            value = selectedMinute,
            range = 0..59,
            onValueChange = { onTimeChange(selectedHour, it) },
            modifier = Modifier
                .size(120.dp, 180.dp)
                .background(Color.Transparent) ,
            dividersColor= Color.Transparent,
            textStyle = TextStyle(
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            label = { String.format("%02d", it) }
        )
    }
}












