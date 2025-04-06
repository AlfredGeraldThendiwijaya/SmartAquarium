package com.example.smartaquarium.ViewUI

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.smartaquarium.Component.InfoCardContainer
import com.example.smartaquarium.Component.ListAquariumCard
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.Aquarium
import com.example.smartaquarium.ViewModel.HomeViewModel
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.darkBlue
import com.example.smartaquarium.ui.theme.navyblue
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    BackHandler {
        // Langsung exit aplikasi kalau di HomeScreen
        (navController.context as? Activity)?.finish()
    }

    val user = FirebaseAuth.getInstance().currentUser
    val userPhotoUrl = user?.photoUrl?.toString() ?: "https://via.placeholder.com/100"
    val aquariumList = viewModel.aquariums
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var selectedUnitId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "LaunchedEffect dipanggil")
        viewModel.fetchAquariums() // Ambil data saat pertama kali masuk ke layar
    }
    Log.d("UI", "Aquarium List di UI: $aquariumList")


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        if (showDialog && selectedUnitId != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = "Konfirmasi Hapus")
                },
                text = {
                    Text(text = "Yakin ingin menghapus aquarium ini? Tindakan ini tidak bisa dibatalkan.")
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null && selectedUnitId != null) {
                                viewModel.deleteSchedule(userId, selectedUnitId!!)
                            }
                            showDialog = false
                        }
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }
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

                IconButton(
                    onClick = { navController.navigate("setting") },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 28.dp, end = 16.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = userPhotoUrl),
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50.dp))
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
                            viewModel.addAquarium(name, serial)
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

                IconButton(onClick = { viewModel.sortAquariumsByName()}) {
                    Icon(
                        painter = painterResource(id = R.drawable.sorting),
                        contentDescription = "Filter Icon",
                        modifier = Modifier.size(24.dp),
                        tint = navyblue
                    )
                }
            }

            if (isLoading) {
                // 🔥 Menampilkan loading saat sedang fetch data
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = navyblue)
                    Text(
                        text = "Loading...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = navyblue,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else {
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp)
                            .offset(y = (-30).dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(aquariumList) { aquarium ->
                            ListAquariumCard(
                                name = aquarium.unitName,
                                unitId = aquarium.unitId,
                                percentage = 50,
                                statusText = "Berisiko",
                                onClick = {
                                    navController.navigate("detail/${aquarium.unitName}/${aquarium.unitId}")
                                },
                                onDelete = { unitId ->
                                    // munculin AlertDialog konfirmasi delete
                                    selectedUnitId = unitId
                                    showDialog = true
                                }
                            )
                        }

                    }
                }

            }
        }
    }
}

