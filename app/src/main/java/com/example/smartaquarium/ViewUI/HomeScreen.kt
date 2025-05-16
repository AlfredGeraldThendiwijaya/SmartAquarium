package com.example.smartaquarium.ViewUI

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.smartaquarium.Component.InfoCard
import com.example.smartaquarium.Component.ListAquariumCard
import com.example.smartaquarium.R
import com.example.smartaquarium.ViewModel.HomeViewModel
import com.example.smartaquarium.ui.theme.*
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
    val displayName = user?.displayName?.split(" ")?.firstOrNull() ?: "Guest"

    Surface(modifier = Modifier.fillMaxSize(), color = softWhite) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                isRefreshing = true
                viewModel.fetchAquariums()
                isRefreshing = false
            }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // ✅ Header user (span 2 kolom)
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        IconButton(onClick = { navController.navigate("setting") }) {
                            Image(
                                painter = rememberAsyncImagePainter(model = userPhotoUrl),
                                contentDescription = "User Profile",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                        }
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "Good Day. $displayName!",
                                fontSize = 20.sp,
                                color = darkgray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Manage Your Discus",
                                fontSize = 14.sp,
                                color = mediumgray,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        InfoCard(aquariumCount = aquariumList.size)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "List Aquarium",
                                fontSize = 18.sp,
                                color = darkgray,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconButton(onClick = { viewModel.sortAquariumsByName() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.sorting),
                                    contentDescription = "Sort",
                                    tint = darkgray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // ✅ Loading
                if (isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = navyblue)
                            Text(
                                text = "Loading...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = darkgray,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }

                // ✅ Kosong
                if (!isLoading && aquariumList.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
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
                                color = darkgray,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            Text(
                                text = "Tambahkan akuarium ke list terlebih dahulu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = darkgray,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }

                // ✅ List Aquarium
                items(aquariumList) { aquarium ->
                    ListAquariumCard(
                        modifier = Modifier.aspectRatio(1f),
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

        if (showDialog && selectedUnitId != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text("Konfirmasi Hapus", color = darkgray)
                },
                text = {
                    Text(
                        "Yakin ingin menghapus aquarium ini? Tindakan ini tidak bisa dibatalkan.",
                        color = darkgray
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null && selectedUnitId != null) {
                                viewModel.deleteSchedule(userId, selectedUnitId!!)
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = navyblue)
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = navyblue)
                    ) {
                        Text("Batal")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
