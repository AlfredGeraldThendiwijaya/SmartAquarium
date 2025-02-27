package com.example.smartaquarium.Component


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




