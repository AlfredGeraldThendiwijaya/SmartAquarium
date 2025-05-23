package com.example.smartaquarium.ViewUI

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.DetectViewModel
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.navyblue
import kotlin.math.hypot

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DetectScreen(navController: NavController, aquariumSerial: String) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val viewModel: DetectViewModel = viewModel()
    val latestImageUrl by viewModel.latestImageUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        capturedBitmap = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        capturedBitmap = bitmap
        selectedImageUri = null
    }

    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar("❌ Gagal mengambil gambar dari kamera wireless. Pastikan ESP32-CAM menyala.")
            showError = false
        }
    }
    var showInfo by remember { mutableStateOf(false) }

    LaunchedEffect(showInfo) {
        if (showInfo) {
            snackbarHostState.showSnackbar("ℹ️ Jika gambar terlihat sama terus, pastikan ESP32-CAM dalam keadaan menyala dan koneksi anda lancar.")
            showInfo = false
        }
    }

    val midnightGradient = listOf(
        0.1f to Color(0xFF193F62),
        0.27f to Color(0xFF0C1C3E),
        0.65f to Color(0xFF0C1C3E),
        0.75f to Color(0xFF0C1C3E),
        1f to Color(0xFF35336B)
    )

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
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 50.dp)
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
                                text = "Deteksi Anomali",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { showInfo = true }) {
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

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Pilih Metode Input Gambar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputOption(
                        iconRes = R.drawable.gallery_ic,
                        label = "Ambil dari Galeri",
                        onClick = { galleryLauncher.launch("image/*") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputOption(
                        iconRes = R.drawable.camera_ic,
                        label = "Ambil dari Kamera",
                        onClick = { cameraLauncher.launch() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputOption(
                        iconRes = R.drawable.wireless_cam_ic,
                        label = if (isLoading) "Mengambil dari Kamera Wireless..." else "Kamera Wireless",
                        onClick = {
                            viewModel.triggerWirelessCamera(
                                unitId = aquariumSerial,
                                onFailure = { showError = true }
                            )
                        },
                        enabled = !isLoading
                    )

                    val hasImage = selectedImageUri != null || capturedBitmap != null || latestImageUrl != null
                    if (hasImage) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Preview Gambar yang Dipilih:", color = Color.White)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                when {
                                    selectedImageUri != null -> {
                                        Image(
                                            painter = rememberAsyncImagePainter(selectedImageUri),
                                            contentDescription = "Preview",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    capturedBitmap != null -> {
                                        Image(
                                            bitmap = capturedBitmap!!.asImageBitmap(),
                                            contentDescription = "Preview",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    latestImageUrl != null -> {
                                        val imageKey = remember(latestImageUrl) { System.currentTimeMillis().toString() }
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data("${latestImageUrl}&key=$imageKey")
                                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                                    .diskCachePolicy(CachePolicy.DISABLED)
                                                    .build()
                                            ),
                                            contentDescription = "Wireless Preview",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        selectedImageUri = null
                                        capturedBitmap = null
                                        viewModel.clearWirelessImage()
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Hapus gambar",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp) // tambahkan padding bawah di luar tombol
                        ) {
                            Button(
                                onClick = { /* TODO: Analisis logic */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = navyblue
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .padding(horizontal = 24.dp)
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
                                Text("Analisis Sekarang", color = navyblue, fontSize = 16.sp)
                            }
                        }
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}


@Composable
fun InputOption(iconRes: Int, label: String, onClick: () -> Unit, enabled: Boolean = true) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) navyblue else Color.Gray
            )
        }
    }
}
