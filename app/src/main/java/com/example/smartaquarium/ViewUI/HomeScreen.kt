package com.example.smartaquarium.ViewUI

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartaquarium.R
import com.example.smartaquarium.Component.InfoCardContainer
import coil.compose.rememberAsyncImagePainter

import com.example.smartaquarium.Component.ListAquariumCard
import com.example.smartaquarium.ViewModel.HomeViewModel
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.darkBlue
import com.example.smartaquarium.ui.theme.navyblue
import com.example.smartaquarium.ui.theme.skyBlue
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    BackHandler {
        // Langsung exit aplikasi kalau di HomeScreen
        (navController.context as? Activity)?.finish()
    }
    val user = FirebaseAuth.getInstance().currentUser
    val userPhotoUrl = user?.photoUrl?.toString() ?: "https://via.placeholder.com/100"
    val aquariumList by viewModel.aquariums.collectAsState() // Gunakan collectAsState untuk mendapatkan update otomatis

    LaunchedEffect(Unit) {
        viewModel.fetchAquariums() // Ambil data saat pertama kali masuk ke layar
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.discus_dashboard),
                    contentDescription = "discus-fish-tank-set-up",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .align(Alignment.TopCenter)
                        .scale(1.2f)
                        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                )
                // Pindahkan IconButton ke dalam Box ini
                IconButton(
                    onClick = { navController.navigate("setting") },
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Pastikan ada di sudut kanan atas
                        .padding(top = 28.dp, end = 16.dp) // Kasih padding biar nggak terlalu mepet
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = userPhotoUrl),
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50.dp)), // Biar bentuknya bulat
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 140.dp)
                ) {
                    InfoCardContainer(
                        onAddAquarium = { name, serial ->
                            viewModel.addAquarium(name,serial) // Simpan ke ViewModel
                        },
                        modifier = Modifier.widthIn(max = 350.dp),
                        aquariumCount = aquariumList.size

                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "List Aquarium",
                    fontSize = 24.sp,
                    color = navyblue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.sorting),
                        contentDescription = "Filter Icon",
                        modifier = Modifier.size(24.dp),
                        tint = navyblue
                    )
                }
            }

            if (aquariumList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.akuarium_not_found),
                        contentDescription = "akuarium gada",
                        modifier = Modifier.padding(top = 50.dp)
                    )
                    Text(
                        text = "Tidak ada akuarium",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = navyblue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Tambahkan akuarium ke list terlebih dahulu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            } else {
                // Jika ada data, tampilkan dalam LazyColumn
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                        .offset(y = (-30).dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(aquariumList) { aquarium ->
                        Log.d("UI", "Aquarium List in UI: $aquariumList")
                        ListAquariumCard(
                            name = aquarium.unitName,
                            onClick = {
                                navController.navigate("detail/${aquarium.unitName}/${aquarium.unitId}") // ✅ Gunakan unitId sebagai serial
                            }
                        )
                    }
                }
            }
        }
    }
}

