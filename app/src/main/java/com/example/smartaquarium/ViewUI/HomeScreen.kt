// final versi: HomeScreen hanya refresh manual (swipe), tidak auto-fetch saat balik dari DetailScreen
package com.example.smartaquarium.ViewUI

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
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
import com.example.smartaquarium.ViewModel.HomeViewModel
import com.example.smartaquarium.ui.theme.accentMint
import com.example.smartaquarium.ui.theme.darkBlue
import com.example.smartaquarium.ui.theme.navyblue
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    BackHandler { (navController.context as? Activity)?.finish() }

    val user = FirebaseAuth.getInstance().currentUser
    val userPhotoUrl = user?.photoUrl?.toString() ?: "https://via.placeholder.com/100"
    val aquariumList = viewModel.aquariums
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var selectedUnitId by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = accentMint
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    isRefreshing = true
                    viewModel.fetchAquariums()
                    isRefreshing = false
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.discus_dashboard),
                            contentDescription = "discus-fish-tank-set-up",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .align(Alignment.TopCenter)
                                .scale(1.2f)
                                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                            contentScale = ContentScale.Crop
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
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = if (isLandscape) 60.dp else 140.dp)
                        ) {
                            InfoCardContainer(
                                onAddAquarium = { name, serial -> viewModel.addAquarium(name, serial) },
                                modifier = Modifier.widthIn(min = 250.dp, max = 360.dp),
                                aquariumCount = aquariumList.size
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isLandscape) 32.dp else 100.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "List Aquarium",
                            fontSize = 24.sp,
                            color = navyblue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.sortAquariumsByName() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.sorting),
                                contentDescription = "Sort",
                                tint = navyblue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    if (isLoading) {
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
                    } else if (aquariumList.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.akuarium_not_found),
                                contentDescription = "akuarium gada",
                                modifier = Modifier.padding(top = 40.dp)
                            )
                            Text(
                                text = "Tidak ada akuarium",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = navyblue,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            Text(
                                text = "Tambahkan akuarium ke list terlebih dahulu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = darkBlue,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            aquariumList.forEach { aquarium ->
                                ListAquariumCard(
                                    name = aquarium.unitName,
                                    unitId = aquarium.unitId,
                                    percentage = 50,
                                    statusText = "Beresiko",
                                    onClick = {
                                        navController.navigate("detail/${aquarium.unitName}/${aquarium.unitId}")
                                    },
                                    onDelete = { unitId ->
                                        selectedUnitId = unitId
                                        showDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showDialog && selectedUnitId != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Konfirmasi Hapus") },
                    text = { Text("Yakin ingin menghapus aquarium ini? Tindakan ini tidak bisa dibatalkan.") },
                    confirmButton = {
                        TextButton(onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null && selectedUnitId != null) {
                                viewModel.deleteSchedule(userId, selectedUnitId!!)
                            }
                            showDialog = false
                        }) { Text("Hapus") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) { Text("Batal") }
                    }
                )
            }
        }
    }
}
