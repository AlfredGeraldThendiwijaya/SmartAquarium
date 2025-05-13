package com.example.smartaquarium.ViewUI

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.smartaquarium.R
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.navyblue

@Composable
fun DetectScreen(navController: NavController, aquariumSerial: String) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Ambil dari galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        capturedBitmap = null
    }

    // Ambil dari kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        capturedBitmap = bitmap
        selectedImageUri = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== Header =====
            Row(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = navyblue
                    )
                }
                Text(
                    text = "Deteksi Anomali",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = navyblue
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ===== Input Options =====
            Text(
                text = "Pilih Metode Input Gambar",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = navyblue,
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
                label = "Kamera Wireless (Coming Soon)",
                onClick = { },
                enabled = false
            )

            // ===== Preview Section =====
            val hasImage = selectedImageUri != null || capturedBitmap != null
            if (hasImage) {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Preview Gambar yang Dipilih:", color = navyblue)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(top = 8.dp)
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    IconButton(
                        onClick = {
                            selectedImageUri = null
                            capturedBitmap = null
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

            // ===== Analisis Button =====
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: Analisis logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = navyblue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Analisis Sekarang", color = Color.White, fontSize = 16.sp)
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
