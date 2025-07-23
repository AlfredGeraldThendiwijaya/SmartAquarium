package com.example.smartaquarium.Component


import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.DetailViewModel
import com.example.smartaquarium.network.ScheduleData
import com.example.smartaquarium.ui.theme.navyblue
import com.example.smartaquarium.ui.theme.softWhite
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.viewinterop.AndroidView
import com.example.smartaquarium.ViewModel.HomeViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("RememberReturnType")


@Composable
fun ForecastChartScreen(
    phForecast: List<Float>,
    tdsForecast: List<Float>,
    tempForecast: List<Float>,
    timeForecast: List<Long>,
    modifier: Modifier = Modifier
) {
    var selectedParam by remember { mutableStateOf("pH") }
    val (forecast, sampledTime) = when (selectedParam) {
        "TDS" -> tdsForecast to timeForecast
        "Temperature" -> tempForecast to timeForecast
        else -> phForecast to timeForecast
    }

    val sampledIndices = forecast.indices step 1 // tampilkan semua titik
    val forecastEntries = sampledIndices.map {
        Entry(sampledTime[it].toFloat(), forecast[it])
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Parameter: $selectedParam")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("pH", "TDS", "Temperature").forEach { label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedParam = label
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 600
                    )
                    setTouchEnabled(true)
                    setPinchZoom(true)
                    legend.isEnabled = true
                    description.isEnabled = true
                }
            },
            update = { chart ->
                val forecastSet = LineDataSet(forecastEntries, selectedParam).apply {
                    color = when (selectedParam) {
                        "pH" -> android.graphics.Color.rgb(0,175,0)
                        "TDS" -> android.graphics.Color.rgb(255, 152, 0)
                        else -> android.graphics.Color.BLUE
                    }
                    setDrawCircles(true)
                    setCircleColor(color)
                    lineWidth = 2f
                    setDrawValues(false)
                }

                chart.data = LineData(forecastSet)

                // ✅ Tambahkan label deskripsi custom di pojok kanan bawah
                chart.description.text = when (selectedParam) {
                    "pH" -> "Prediksi pH"
                    "TDS" -> "Prediksi TDS (ppm)"
                    else -> "Prediksi Suhu (°C)"
                }
                chart.description.textSize = 10f
                chart.description.textColor = android.graphics.Color.DKGRAY


                val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                chart.xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    labelRotationAngle = 45f
                    setDrawGridLines(true)
                    granularity = 3600f * 1000f // 1 jam
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val date = Date(value.toLong())
                            return sdfDate.format(date)
                        }
                    }
                }

                chart.axisLeft.apply {
                    textSize = 10f
                    axisMinimum = when (selectedParam) {
                        "pH" -> forecast.minOrNull()?.minus(0.01f) ?: 7f
                        "TDS" -> forecast.minOrNull()?.minus(1f) ?: 100f
                        else -> forecast.minOrNull()?.minus(1f) ?: 25f
                    }
                    axisMaximum = when (selectedParam) {
                        "pH" -> forecast.maxOrNull()?.plus(0.01f) ?: 8f
                        "TDS" -> forecast.maxOrNull()?.plus(1f) ?: 140f
                        else -> forecast.maxOrNull()?.plus(1f) ?: 32f
                    }
                }

                chart.axisRight.isEnabled = false
                val marker = CustomMarkerView(
                    context = chart.context,
                    layoutResource = R.layout.marker_view,
                    timeFormat = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()),
                    label = selectedParam
                ).apply {
                    chartView = chart
                }
                chart.marker = marker
                var lastHighlight: Highlight? = null
                chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (lastHighlight != null &&
                            lastHighlight!!.x == h?.x &&
                            lastHighlight!!.dataIndex == h.dataIndex
                        ) {
                            chart.highlightValue(null)
                            lastHighlight = null
                        } else {
                            lastHighlight = h
                        }
                    }

                    override fun onNothingSelected() {
                        lastHighlight = null
                    }
                })
                chart.invalidate()
            }
        )

    }
}






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


fun postAquariumData(
    userId: String,
    unitId: String,
    unitName: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val json = JSONObject().apply {
                put("userId", userId)
                put("unitId", unitId)
                put("unitName", unitName)
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("https://us-central1-smart-aquarium-fe20f.cloudfunctions.net/api/add-unit")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } else {
                val errorBody = response.body?.string()
                val rawError = if (!errorBody.isNullOrEmpty()) {
                    try {
                        JSONObject(errorBody).optString("error")
                    } catch (e: Exception) {
                        null
                    }
                } else null

                val friendlyMessage = when {
                    rawError?.contains("tidak terdaftar", ignoreCase = true) == true ->
                        "Serial number tidak valid. Silakan hubungi pihak produksi untuk mendaftarkan perangkat."
                    rawError?.contains("sudah terdaftar", ignoreCase = true) == true ||
                            rawError?.contains("sudah digunakan", ignoreCase = true) == true ->
                        "Serial number ini sudah digunakan. Silakan gunakan unit lain."
                    else -> rawError ?: "Gagal menambahkan unit. Silakan coba lagi."
                }

                withContext(Dispatchers.Main) {
                    onError(friendlyMessage)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Terjadi kesalahan jaringan: ${e.message}")
            }
        }
    }
}








// English-translated version of InfoCard.kt

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    aquariumCount: Int,
    viewModel: HomeViewModel,
    onAddAquarium: (String, String) -> Unit,
    gradientStops: List<Pair<Float, Color>> = listOf(
        0.0f to Color(0xFFF2F2F2).copy(alpha = 0.02f),
        0.2f to Color(0xFFD8ECE9).copy(alpha = 0.02f),
        0.5f to Color(0xFF85D8CE).copy(alpha = 0.15f),
        1f to Color(0xFFC7F862).copy(alpha = 0.35f),
    )
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    val brush = remember(boxSize) {
        Brush.linearGradient(
            colorStops = gradientStops.toTypedArray(),
            start = Offset(0f, 0f),
            end = Offset(boxSize.width.toFloat(), boxSize.height.toFloat())
        )
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .height(80.dp)
                .onGloballyPositioned { coordinates -> boxSize = coordinates.size }
                .clip(RoundedCornerShape(28.dp))
                .background(brush)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp))
                .padding(horizontal = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registered aquariums: $aquariumCount",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { isDialogOpen = true }) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color.White.copy(alpha = 0.15f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Icon",
                                tint = Color(0xFFced2e4)
                            )
                        }
                    }
                }
            }
        }
    }

    if (isDialogOpen) {
        AddUnitDialog(
            viewModel = viewModel,
            onAddSuccess = { name, serial ->
                onAddAquarium(name, serial)
                isDialogOpen = false
            },
            onDismiss = {
                isDialogOpen = false
            }
        )
    }
}
@Composable
fun AddUnitDialog(
    viewModel: HomeViewModel,
    onAddSuccess: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var aquariumName by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }
    var isSerialError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val user = FirebaseAuth.getInstance().currentUser

    fun resetForm() {
        aquariumName = ""
        serialNumber = ""
        isNameError = false
        isSerialError = false
        errorText = null
    }

    AlertDialog(
        onDismissRequest = {
            resetForm()
            onDismiss()
        },
        title = { Text("Add Aquarium") },
        text = {
            Column {
                OutlinedTextField(
                    value = aquariumName,
                    onValueChange = {
                        aquariumName = it
                        isNameError = it.isBlank()
                    },
                    label = { Text("Aquarium Name") },
                    isError = isNameError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isNameError) {
                    Text("Aquarium name cannot be empty", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = serialNumber,
                    onValueChange = {
                        serialNumber = it
                        isSerialError = it.isBlank()
                    },
                    label = { Text("Serial Number") },
                    isError = isSerialError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isSerialError) {
                    Text("Serial number cannot be empty", color = Color.Red, fontSize = 12.sp)
                }

                if (!errorText.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorText ?: "", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                isNameError = aquariumName.isBlank()
                isSerialError = serialNumber.isBlank()
                if (!isNameError && !isSerialError && user != null) {
                    postAquariumData(
                        userId = user.uid,
                        unitId = serialNumber,
                        unitName = aquariumName,
                        onSuccess = {
                            resetForm()
                            viewModel.fetchAquariums()
                            onAddSuccess(aquariumName, serialNumber)
                        },
                        onError = { msg -> errorText = msg }
                    )
                }
            },
                colors = ButtonDefaults.textButtonColors(contentColor = navyblue)
                ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                resetForm()
                onDismiss()
            },
                colors = ButtonDefaults.textButtonColors(contentColor = navyblue)) {
                Text("Cancel")
            }
        },
        containerColor = Color.White,
        textContentColor = Color.Black
    )
}










@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListAquariumCard(
    modifier: Modifier = Modifier,
    name: String,
    unitId: String,
    percentage: Int,
    statusText: String,
    onClick: () -> Unit,
    onDelete: (String) -> Unit,
) {
    val cornerRadius = 20.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Agar persegi
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onDelete(unitId) }
            )
    ) {
        // Shadow belakang
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 6.dp, y = 6.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    ambientColor = Color(0xFFB0BEC5),
                    spotColor = Color(0xFFA3B1C6)
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = (-6).dp, y = (-6).dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    ambientColor = softWhite,
                    spotColor = Color(0xFFE0E0E0)
                )
        )

        // Main card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color(0xFFFAFAFA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fish_tank),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    color = Color(0xff4a628a),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}






@Composable
fun DetailInfoCard(
    name: String,
    id: String,
    navController: NavController,
    aquariumSerial: String,
    gradientStops: List<Pair<Float, Color>> = listOf(
        0.0f to Color(0xFFF2F2F2).copy(alpha = 0.02f),
        0.2f to Color(0xFFD8ECE9).copy(alpha = 0.02f),
        0.5f to Color(0xFF85D8CE).copy(alpha = 0.15f),
        1f to Color(0xFFC7F862).copy(alpha = 0.35f),
    )
) {

    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    val brush = remember(boxSize) {
        Brush.linearGradient(
            colorStops = gradientStops.toTypedArray(),
            start = Offset(0.0f, 0f),
            end = Offset(boxSize.width * 1f, boxSize.height.toFloat())
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(10.dp))
            .background(brush)
            .onGloballyPositioned { coordinates ->
                boxSize = coordinates.size
            }
            .border(BorderStroke(1.dp, color = Color.White.copy(alpha = 0.2f),), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            // Gambar akuarium
            Image(
                painter = painterResource(id = R.drawable.fish_tank),
                contentDescription = "fish tank",
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
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "serial ID : $id",
                    color = Color(0xFFBCCCCE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 🔍 Button Camera di pojok kanan bawah
        IconButton(
            onClick = { navController.navigate("detect_fish/${aquariumSerial}") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cam_fish),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(100.dp),
                contentDescription = "Deteksi Ikan",
                tint = Color.Unspecified
            )
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


@SuppressLint("DefaultLocale")
@Composable
fun GaugeMeterWithStatus(
    ph: Double,
    temperature: Double,
    pph: Double,
    ntu: Double,
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier
) {
    val turbidity by viewModel.turbidity.collectAsState()
    val temperatureState by viewModel.temperature.collectAsState()
    val phState by viewModel.ph.collectAsState()
    val tds by viewModel.tds.collectAsState()

    // === Scoring functions matching the Python logic ===
    fun scorePh(ph: Double): Double {
        return when {
            ph < 5.5 || ph > 7.5 -> 0.0
            ph < 6.0 -> (ph - 5.5) * 200
            ph > 7.0 -> (7.5 - ph) * 200
            else -> 100.0
        }
    }

    fun scoreTds(tds: Double): Double {
        return when {
            tds > 180 -> 0.0
            tds <= 150 -> 100.0
            else -> (180 - tds) * (100.0 / 30.0)
        }
    }

    fun scoreTemp(temp: Double): Double {
        return when {
            temp < 25 || temp > 31 -> 0.0
            temp < 27 -> (temp - 25) * 50
            temp > 29 -> (31 - temp) * 50
            else -> 100.0
        }
    }

    val phScore = scorePh(ph)
    val tdsScore = scoreTds(pph)
    val tempScore = scoreTemp(temperature)
    val avgScore = (phScore + tdsScore + tempScore) / 3
    val percentage = avgScore.toInt()

    val (gradientColors, statusText, statusColor) = when {
        avgScore >= 80 -> Triple(
            listOf(Color(0xFF4CAF50), Color(0xFF81C784)), // Green to Light Green
            "Safe",
            Color(0xFF4CAF50)
        )
        avgScore >= 50 -> Triple(
            listOf(Color(0xFFFFC107), Color(0xFFFF9800)), // Yellow to Orange
            "At Risk",
            Color(0xFFFFA000)
        )
        else -> Triple(
            listOf(Color(0xFFFF9800), Color(0xFFD32F2F)), // Orange to Red
            "Dangerous",
            Color.Red
        )
    }

    // === UI remains unchanged ===
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
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Water Quality",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                Box(contentAlignment = Alignment.Center) {
                    val sweepAngle = (percentage / 100f) * 250f

                    Canvas(modifier = Modifier.size(120.dp)) {
                        val strokeWidth = 18.dp.toPx()
                        val startAngle = 145f
                        drawArc(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            startAngle = startAngle,
                            sweepAngle = 250f,
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

                    Text(
                        text = "$percentage%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Text(
                    text = statusText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                val formattedTurbidity = turbidity?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                val formattedTemperature = temperatureState?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                val formattedPh = phState?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                val formattedTds = tds?.toFloat()?.let { String.format("%.1f", it) } ?: "N/A"
                ParameterItem(R.drawable.suhu, "Temperature", "$formattedTemperature °C")
                ParameterItem(R.drawable.ph, "pH", "$formattedPh pH")
                ParameterItem(R.drawable.ppm, "TDS", "$formattedTds ppm")
//                ParameterItem(R.drawable.turbidity, "Turbidity", "$formattedTurbidity NTU")
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
                    color = navyblue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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












