package com.example.smartaquarium.Component


import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.smartaquarium.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.wear.compose.material.PickerDefaults
import com.example.smartaquarium.ui.theme.navyblue
import com.chargemap.compose.numberpicker.NumberPicker

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
    aquariumCount: Int, // Tambahkan parameter ini
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        InfoCard(
            modifier = Modifier.align(Alignment.Center),
            aquariumCount = aquariumCount // Gunakan parameter yang benar
        )

        Image(
            painter = painterResource(id = R.drawable.add_device),
            contentDescription = "discus-fish-add device",
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(y = (-30).dp, x = (-15).dp)
                .zIndex(1f)
                .clickable { isDialogOpen = true }
        )
    }

    if (isDialogOpen) {
        AddAquariumDialog(
            onDismiss = { isDialogOpen = false },
            onConfirm = { name, serial ->
                onAddAquarium(name, serial)
                isDialogOpen = false
            }
        )
    }
}



@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    aquariumCount: Int
) {
    Box(
        modifier = modifier
            .width(600.dp) // Fix lebar ke 400dp
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
                    text = "Hi, Cindy",
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
@Composable
fun ListAquariumCard(modifier: Modifier = Modifier , name: String ,onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.9f) // Biar nggak terlalu mepet ke kiri
            .height(107.dp)
            .clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(107.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.8f))
                .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(10.dp))
        )
        Image(
            painter = painterResource(id = R.drawable.fish_tank),
            contentDescription = "fish-tank_2173805 2",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 28.dp)
                .size(50.dp)
        )
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
        Text(
            text = "Tidak ada penjadwalan pemberian makan",
            color = Color(0xff6c7278),
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 79.dp, y = 67.dp)
        )
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

@Composable
fun GaugeMeterWithStatus(
    percentage: Int,
    ph: Float,
    temperature: Float,
    ntu: Float,
    pph: Float,
    modifier: Modifier = Modifier
) {
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
                ParameterItem(R.drawable.suhu, "Suhu", "$temperature °C")
                ParameterItem(R.drawable.ph, "pH", "$ph")
                ParameterItem(R.drawable.turbidity, "NTU", "$ntu NTU")
                ParameterItem(R.drawable.tds, "TDS", "$pph ppm")
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCard(modifier: Modifier = Modifier) {
    var isActive by remember { mutableStateOf(true) } // ✅ State untuk switch

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                    text = "09:00",
                    color = Color(0xff4a628a),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ✅ Switch untuk on/off jadwal
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












