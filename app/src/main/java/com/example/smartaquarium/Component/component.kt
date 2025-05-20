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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.room.util.copy
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.DetailViewModel
import com.example.smartaquarium.network.ScheduleData
import com.example.smartaquarium.ui.theme.navyblue
import com.example.smartaquarium.ui.theme.softWhite
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import kotlin.math.*
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
//@Composable
//fun InfoCardContainerOld(
//    onAddAquarium: (String, String) -> Unit,
//    aquariumCount: Int,
//    modifier: Modifier = Modifier
//) {
//    var isDialogOpen by remember { mutableStateOf(false) }
//    var aquariumName by remember { mutableStateOf("") }
//    var serialNumber by remember { mutableStateOf("") }
//    var isNameError by remember { mutableStateOf(false) }
//    var isSerialError by remember { mutableStateOf(false) }
//    val user = FirebaseAuth.getInstance().currentUser
//
//    Box(
//        modifier = modifier.fillMaxWidth()
//    ) {
//        InfoCard(
//            modifier = Modifier.align(Alignment.Center),
//            aquariumCount = aquariumCount
//        )
//
//        Box(
//            modifier = Modifier
//                .size(140.dp)
//                .align(Alignment.TopEnd)
//                .offset(y = (-30).dp, x = (-15).dp)
//                .zIndex(1f)
//                .clickable(
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                ) {
//                    isDialogOpen = true
//                }
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.add_device),
//                contentDescription = "Tambah Akuarium",
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    }
//
//    if (isDialogOpen) {
//        AlertDialog(
//            onDismissRequest = { isDialogOpen = false },
//            title = { Text("Tambah Akuarium") },
//            text = {
//                Column {
//                    OutlinedTextField(
//                        value = aquariumName,
//                        onValueChange = {
//                            aquariumName = it
//                            isNameError = it.isBlank()
//                        },
//                        label = { Text("Nama Akuarium") },
//                        placeholder = { Text("Masukkan nama akuarium") },
//                        isError = isNameError,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    if (isNameError) {
//                        Text(
//                            text = "Nama akuarium tidak boleh kosong",
//                            color = Color.Red,
//                            fontSize = 12.sp
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    OutlinedTextField(
//                        value = serialNumber,
//                        onValueChange = {
//                            serialNumber = it
//                            isSerialError = it.isBlank()
//                        },
//                        label = { Text("Serial Number") },
//                        placeholder = { Text("Masukkan serial number") },
//                        isError = isSerialError,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    if (isSerialError) {
//                        Text(
//                            text = "Serial number tidak boleh kosong",
//                            color = Color.Red,
//                            fontSize = 12.sp
//                        )
//                    }
//                }
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        isNameError = aquariumName.isBlank()
//                        isSerialError = serialNumber.isBlank()
//                        if (!isNameError && !isSerialError && user != null) {
//                            postAquariumData(user.uid, serialNumber, aquariumName) {
//                                onAddAquarium(aquariumName, serialNumber)
//                                isDialogOpen = false
//                            }
//                        }
//                    }
//                ) {
//                    Text("Tambah")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { isDialogOpen = false }) {
//                    Text("Batal")
//                }
//            },
//            containerColor = Color.White,
//            textContentColor = Color.Black
//        )
//    }
//}

//@Composable
//fun InfoCardContainer(
//    onAddAquarium: (String, String) -> Unit,
//    aquariumCount: Int,
//    modifier: Modifier = Modifier
//) {
//    var isDialogOpen by remember { mutableStateOf(false) }
//    var aquariumName by remember { mutableStateOf("") }
//    var serialNumber by remember { mutableStateOf("") }
//    var isNameError by remember { mutableStateOf(false) }
//    var isSerialError by remember { mutableStateOf(false) }
//    val user = FirebaseAuth.getInstance().currentUser
//
//    Box(
//        modifier = modifier.fillMaxWidth()
//    ) {
//        InfoCard(
//            modifier = Modifier.align(Alignment.Center),
//            aquariumCount = aquariumCount
//        )
//
//        Box(
//            modifier = Modifier
//                .size(140.dp)
//                .align(Alignment.TopEnd)
//                .offset(y = (-30).dp, x = (-15).dp)
//                .zIndex(1f)
//                .clickable(
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                ) {
//                    isDialogOpen = true
//                }
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.add_device),
//                contentDescription = "Tambah Akuarium",
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    }
//
//    if (isDialogOpen) {
//        AlertDialog(
//            onDismissRequest = { isDialogOpen = false },
//            title = { Text("Tambah Akuarium") },
//            text = {
//                Column {
//                    OutlinedTextField(
//                        value = aquariumName,
//                        onValueChange = {
//                            aquariumName = it
//                            isNameError = it.isBlank()
//                        },
//                        label = { Text("Nama Akuarium") },
//                        placeholder = { Text("Masukkan nama akuarium") },
//                        isError = isNameError,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    if (isNameError) {
//                        Text(
//                            text = "Nama akuarium tidak boleh kosong",
//                            color = Color.Red,
//                            fontSize = 12.sp
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    OutlinedTextField(
//                        value = serialNumber,
//                        onValueChange = {
//                            serialNumber = it
//                            isSerialError = it.isBlank()
//                        },
//                        label = { Text("Serial Number") },
//                        placeholder = { Text("Masukkan serial number") },
//                        isError = isSerialError,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    if (isSerialError) {
//                        Text(
//                            text = "Serial number tidak boleh kosong",
//                            color = Color.Red,
//                            fontSize = 12.sp
//                        )
//                    }
//                }
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        isNameError = aquariumName.isBlank()
//                        isSerialError = serialNumber.isBlank()
//                        if (!isNameError && !isSerialError && user != null) {
//                            postAquariumData(user.uid, serialNumber, aquariumName) {
//                                onAddAquarium(aquariumName, serialNumber)
//                                isDialogOpen = false
//                            }
//                        }
//                    }
//                ) {
//                    Text("Tambah")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { isDialogOpen = false }) {
//                    Text("Batal")
//                }
//            },
//            containerColor = Color.White,
//            textContentColor = Color.Black
//        )
//    }
//}

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
    aquariumCount: Int,
    onAddAquarium: (String, String) -> Unit,
    gradientStops: List<Pair<Float, Color>> = listOf(
        0.0f to Color(0xFFF2F2F2).copy(alpha = 0.02f),
        0.2f to Color(0xFFD8ECE9).copy(alpha = 0.02f),
        0.5f to Color(0xFF85D8CE).copy(alpha = 0.15f),
        1f to Color(0xFFC7F862).copy(alpha = 0.35f),
    )
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var aquariumName by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }
    var isSerialError by remember { mutableStateOf(false) }
    val user = FirebaseAuth.getInstance().currentUser
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    val brush = remember(boxSize) {
        Brush.linearGradient(
            colorStops = gradientStops.toTypedArray(),
            start = Offset(0.0f, 0f),
            end = Offset(boxSize.width * 1f, boxSize.height.toFloat())
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .height(80.dp)
                .onGloballyPositioned { coordinates ->
                    boxSize = coordinates.size
                }
                .clip(RoundedCornerShape(28.dp))
                .background(brush)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f), // putih 20% opacity
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 16.dp) // beri padding horizontal supaya konten ga nempel banget ke tepi
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
                        text = "Aquarium registered : $aquariumCount",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    IconButton(
                        onClick = { isDialogOpen = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp) // ukuran kotak background
                                .background(
                                    color = Color.White.copy(alpha = 0.15f), // putih dengan opacity 15%
                                    shape = CircleShape // biar bulat
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add, // ikon plus dari Material Icons
                                contentDescription = "Add Icon",
                                tint = Color(0xFFced2e4)
                            )
                        }
                    }

                }
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
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF001F54) // Navy blue
                        )
                    ) {
                        Text("Tambah")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { isDialogOpen = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF001F54) // Navy blue
                        )
                    ) {
                        Text("Batal")
                    }
                },
                containerColor = Color.White,
                textContentColor = Color.Black
            )
        }

    }
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
    aquariumSerial: String
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

    // === Fungsi perhitungan skor sesuai logika Python ===
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
            listOf(Color(0xFF4CAF50), Color(0xFF81C784)), // Hijau ke Hijau Muda
            "Aman",
            Color(0xFF4CAF50)
        )
        avgScore >= 50 -> Triple(
            listOf(Color(0xFFFFC107), Color(0xFFFF9800)), // Kuning ke Oranye
            "Beresiko",
            Color(0xFFFFA000)
        )
        else -> Triple(
            listOf(Color(0xFFFF9800), Color(0xFFD32F2F)), // Oranye ke Merah
            "Berbahaya",
            Color.Red
        )
    }

    // === UI tetap seperti sebelumnya ===
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
                    text = "Kualitas Air",
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
                ParameterItem(R.drawable.suhu, "Suhu", "$formattedTemperature °C")
                ParameterItem(R.drawable.ph, "pH", "$formattedPh pH")
                ParameterItem(R.drawable.turbidity, "NTU", "$formattedTurbidity NTU")
                ParameterItem(R.drawable.ppm, "TDS", "$formattedTds ppm")
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












