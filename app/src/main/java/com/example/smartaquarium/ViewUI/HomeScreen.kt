package com.example.smartaquarium.ViewUI

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.example.smartaquarium.ui.theme.darkgray
import com.example.smartaquarium.ui.theme.navyblue
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.max

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    BackHandler { (navController.context as? Activity)?.finish() }

    val user = FirebaseAuth.getInstance().currentUser
    val userPhotoUrl = user?.photoUrl?.toString() ?: "https://via.placeholder.com/100"
    val aquariumList = viewModel.aquariums
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var selectedUnitId by remember { mutableStateOf<String?>(null) }
    val displayName = user?.displayName?.split(" ")?.firstOrNull() ?: "Guest"

    val midnightGradient = listOf(
        0.1f to Color(0xFF193F62),
        0.37f to Color(0xFF0C1C3E),
        0.65f to Color(0xFF0C1C3E),
        0.85f to Color(0xFF35336B)
    )

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Surface(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val aspectRatio = width / height

            val radius = if (aspectRatio > 1f) {
                width * 0.6f
            } else {
                height * 0.6f
            }

            val centerX = width * 0.0f
            val centerY = height * 0.0f

            val brush = Brush.radialGradient(
                colorStops = midnightGradient.toTypedArray(),
                center = Offset(centerX, centerY),
                radius = radius.coerceAtLeast(1f)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            )

            // SwipeRefresh membungkus seluruh LazyColumn
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    viewModel.fetchAquariums()
                    isRefreshing = false
                },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 22.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
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
                                IconButton(
                                    onClick = {  navController.navigate("setting")  },
                                ) {
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
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Add",
                                            tint = Color(0xFFced2e4),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Text(
                                    text = "Good Day. $displayName!",
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Manage Your Discus",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoCard(
                                onAddAquarium = { name, serial ->
                                    viewModel.addAquarium(name, serial)
                                }
                                ,aquariumCount = aquariumList.size
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Box putih berisi LazyVerticalGrid aquarium tetap di sini
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xffe1e8ef),
                                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                                )
                                .padding(16.dp)
                                .heightIn(min = 200.dp, max = if (isLandscape) 600.dp else 800.dp)
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(if (isLandscape) 4 else 2),
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                userScrollEnabled = true
                            ) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
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

                                if (isLoading) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = navyblue)
                                        }
                                    }
                                } else if (aquariumList.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.akuarium_not_found),
                                                    contentDescription = "akuarium gada"
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
                                } else {
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
                        }
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
}
