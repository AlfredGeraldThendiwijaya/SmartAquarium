// Required dependency:
// implementation("com.github.CanHub:Android-Image-Cropper:4.4.0")

package com.example.smartaquarium.ViewUI

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.*
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.DetectViewModel
import com.example.smartaquarium.ui.theme.navyblue
import java.io.File
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
    val isUploading by viewModel.isUploading.collectAsState()
    val predictionResult by viewModel.predictionResult.collectAsState()

    var showError by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedImageUri, capturedBitmap, latestImageUrl) {
        viewModel.resetPrediction()
    }

    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            selectedImageUri = result.uriContent
            capturedBitmap = null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val cropOptions = CropImageContractOptions(
                uri,
                CropImageOptions().apply {
                    activityTitle = "Crop Image"
                    fixAspectRatio = true
                    aspectRatioX = 1
                    aspectRatioY = 1
                    allowRotation = true
                    showCropOverlay = true
                    guidelines = CropImageView.Guidelines.ON
                    cropMenuCropButtonTitle = "Done"
                }
            )
            cropImageLauncher.launch(cropOptions)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            val imageUri = bitmapToUri(context, it)
            val cropOptions = CropImageContractOptions(
                imageUri,
                CropImageOptions().apply {
                    activityTitle = "Crop Image"
                    fixAspectRatio = true
                    aspectRatioX = 1
                    aspectRatioY = 1
                    allowRotation = true
                    showCropOverlay = true
                    guidelines = CropImageView.Guidelines.ON
                    cropMenuCropButtonTitle = "Done"
                }
            )
            cropImageLauncher.launch(cropOptions)
        }
    }


    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar("❌ Failed to retrieve image from wireless camera. Make sure ESP32-CAM is on.")
            showError = false
        }
    }

    LaunchedEffect(showInfo) {
        if (showInfo) {
            snackbarHostState.showSnackbar("ℹ️ If the image appears the same, make sure ESP32-CAM is powered and your connection is stable.")
            showInfo = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.1f to Color(0xFF193F62),
                    0.27f to Color(0xFF0C1C3E),
                    0.65f to Color(0xFF0C1C3E),
                    0.75f to Color(0xFF0C1C3E),
                    1f to Color(0xFF35336B)
                ),
                center = Offset(0f, 0f),
                radius = hypot(width, height)
            )

            Box(modifier = Modifier.fillMaxSize().background(brush)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 100.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(top = 50.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = null, tint = Color.White)
                            }
                            Text("Anomaly Detection", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        IconButton(onClick = { showInfo = true }) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.White.copy(alpha = 0.15f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color(0xFFced2e4))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Choose Image Source", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)

                    Spacer(modifier = Modifier.height(16.dp))

                    InputOption(R.drawable.gallery_ic, "Pick from Gallery", { galleryLauncher.launch("image/*") }, !isUploading)
                    Spacer(modifier = Modifier.height(12.dp))
                    InputOption(R.drawable.camera_ic, "Take a Photo", { cameraLauncher.launch() }, !isUploading)
                    Spacer(modifier = Modifier.height(12.dp))
                    InputOption(
                        R.drawable.wireless_cam_ic,
                        if (isLoading) "Capturing from Wireless Camera..." else "Wireless Camera",
                        {
                            viewModel.triggerWirelessCamera(
                                unitId = aquariumSerial,
                                onFailure = { showError = true }
                            )
                        },
                        enabled = !isLoading && !isUploading
                    )

                    val hasImage = selectedImageUri != null || capturedBitmap != null || latestImageUrl != null
                    if (hasImage) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Image Preview:", color = Color.White)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(top = 8.dp)
                                .background(Color.White, RoundedCornerShape(12.dp))
                        ) {
                            when {
                                selectedImageUri != null -> Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = "Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                                capturedBitmap != null -> Image(
                                    bitmap = capturedBitmap!!.asImageBitmap(),
                                    contentDescription = "Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                                latestImageUrl != null -> {
                                    val imageKey = remember(latestImageUrl) { System.currentTimeMillis().toString() }
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(context)
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
                                    contentDescription = "Remove Image",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                                )
                            }
                        }

                        if (!predictionResult.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "📌 $predictionResult",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.analyzeImage(selectedImageUri, capturedBitmap, latestImageUrl)
                    },
                    enabled = !isUploading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA4F869)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    Text("Analyze Now", color = navyblue, fontSize = 16.sp)
                }

                if (isUploading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
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
fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "captured_image_${System.currentTimeMillis()}.jpg")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // pastikan sama dengan di manifest
        file
    )
}
