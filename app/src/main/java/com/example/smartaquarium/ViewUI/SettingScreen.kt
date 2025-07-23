package com.example.smartaquarium.ViewUI

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.smartaquarium.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val displayName = user?.displayName?.split(" ")?.firstOrNull() ?: "Guest"
    val userEmail = user?.email ?: "No email"
    val userPhotoUrl = user?.photoUrl?.toString() ?: "https://via.placeholder.com/100"

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val midnightGradient = listOf(
        0.1f to Color(0xFF193F62),
        0.27f to Color(0xFF0C1C3E),
        0.65f to Color(0xFF0C1C3E),
        0.75f to Color(0xFF0C1C3E),
        1f to Color(0xFF35336B)
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val radius = if (width / height > 1f) width * 0.6f else height * 0.6f

            val brush = Brush.radialGradient(
                colorStops = midnightGradient.toTypedArray(),
                center = Offset(0f, 0f),
                radius = radius.coerceAtLeast(1f)
            )

            Box(modifier = Modifier.fillMaxSize().background(brush)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Rounded.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Info Account",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Box
                    val userGradient = Brush.linearGradient(
                        colorStops = listOf(
                            0.0f to Color(0xFFF2F2F2).copy(alpha = 0.02f),
                            0.2f to Color(0xFFD8ECE9).copy(alpha = 0.02f),
                            0.5f to Color(0xFF85D8CE).copy(alpha = 0.15f),
                            1f to Color(0xFFC7F862).copy(alpha = 0.35f)
                        ).toTypedArray(),
                        start = Offset(0f, 0f),
                        end = Offset(width, 100f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isLandscape) 64.dp else 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(userGradient)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(userPhotoUrl),
                                contentDescription = "user_avatar",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = displayName,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = userEmail,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(74.dp))
                    // Box putih bawah
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xffe1e8ef),
                                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = if (isLandscape) 64.dp else 24.dp, vertical = 28.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            NeumorphicSettingItem(
                                cornerRadius = 10.dp,
                                icon = R.drawable.paper_11023693,
                                label = "Terms & Condition"
                            ) {
                                navController.navigate("termsandcondition")
                            }

                            NeumorphicSettingItem(
                                cornerRadius = 10.dp,
                                icon = R.drawable.exit_320140,
                                label = "Log out"
                            ) {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }

                            // Tambah item lain di sini jika diperlukan
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NeumorphicSettingItem(
    cornerRadius: Dp,
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(3.dp, 3.dp)
                .shadow(6.dp, RoundedCornerShape(cornerRadius), ambientColor = Color(0xFFB0BEC5), spotColor = Color(0xFFA3B1C6))
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset((-3).dp, (-3).dp)
                .shadow(4.dp, RoundedCornerShape(cornerRadius), ambientColor = Color(0xFFF6F9FC), spotColor = Color(0xFFE0E0E0))
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color(0xFFFAFAFA))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(25.dp),
                    tint = Color(0xff324b6d)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    color = Color(0xff324b6d),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}





