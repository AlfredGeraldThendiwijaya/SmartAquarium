package com.example.smartaquarium.ViewUI

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.smartaquarium.R
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.navyblue
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val displayName = user?.displayName?.split(" ")?.firstOrNull() ?: "Guest"
    val userEmail = user?.email ?: "No email"
    val userPhotoUrl = user?.photoUrl?.toString() ?: "https://via.placeholder.com/100"
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        BoxWithConstraints {
            val screenWidth = maxWidth
            val screenHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = screenWidth * 0.05f), // Padding responsif
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = screenHeight * 0.05f), // Padding responsif
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp), // Ukuran icon lebih fleksibel
                            tint = navyblue
                        )
                    }
                    Text(
                        text = "Info Account",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = navyblue
                    )
                }
                Spacer(Modifier.height(screenHeight * 0.03f)) // Spacer responsif

                Image(
                    painter = rememberAsyncImagePainter(model = userPhotoUrl),
                    contentDescription = "user_avatar",
                    modifier = Modifier
                        .size(screenWidth * 0.25f) // Ukuran gambar responsif
                        .clip(RoundedCornerShape(50))
                )

                Spacer(Modifier.height(screenHeight * 0.02f))

                Text(
                    text = displayName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = userEmail,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(screenHeight * 0.05f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 16.dp)
                            .clickable{
                                navController.navigate("termsandcondition")
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.paper_11023693),
                            contentDescription = "terms condition",
                            modifier = Modifier.size(25.dp),
                            tint = navyblue
                        )
                        Spacer(modifier = Modifier.width(16.dp)) // Tambahkan jarak antara ikon & teks
                        Text(
                            text = "Syarat dan Ketentuan",
                            color = Color(0xff4a628a),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(10.dp))
                        .clickable {
                            FirebaseAuth.getInstance().signOut()  // Logout dari Firebase
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true } // Hapus HomeScreen dari backstack
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.exit_320140),
                            contentDescription = "Logout",
                            modifier = Modifier.size(25.dp),
                            tint = navyblue
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Log out",
                            color = Color(0xff4a628a),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
}
