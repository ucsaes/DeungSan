package com.example.deungsan.components

import GoogleMapView
import android.content.Context
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import com.example.deungsan.R
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.AsyncImage
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.loader.MountainViewModel
import com.example.deungsan.data.model.Mountain
import com.example.deungsan.ui.theme.GreenPrimaryDark
import com.example.deungsan.ui.theme.myBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MountainDetailScreen(mountainName: String, navController: NavController, viewModel: MountainViewModel = viewModel()) {
    val context: Context = LocalContext.current
    val mountains: List<Mountain> = remember { JsonLoader.loadMountainsFromAssets(context) }
    val mountain = mountains.find { it.name == mountainName }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    LaunchedEffect(Unit) {
        viewModel.loadFavorites(context)
    }
    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = (mountainName in favorites)

    Log.d("imagePath", mountain!!.imagePath)

    val imageModel = if (mountain!!.imagePath.startsWith("http")) {
        mountain.imagePath
    } else {
        "file:///android_asset/mountains/${mountain.imagePath}"
    }



    if (mountain == null) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("산 정보 없음") })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(20.dp)
            ) {
                Text("해당 산 정보를 찾을 수 없습니다.")
            }
        }
        return
    }
    Box( //
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF7F7F7)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp) // 원하는 두께로 조절
                            .background(Color.Transparent)
                            .statusBarsPadding()
                    ) {
                        IconButton(
                            onClick = { backDispatcher?.onBackPressed() },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 0.dp)
                                .size(40.dp)  // 아이콘 버튼도 작게
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIos,
                                contentDescription = "뒤로가기",
                                tint = myBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = mountainName,
                            fontSize = 16.sp,
                            color = myBlack,
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = { viewModel.toggleFavorite(
                                context,
                                mountain.name
                            ) },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 5.dp)
                                .size(40.dp)  // 아이콘 버튼도 작게
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Toggle Favorite",
                                tint = if (isFavorite) Color(0xFFFF6262) else Color(0xFFB9B9B9)
                            )
                        }
                    }
                    Divider(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        thickness = 0.5.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 0.dp, bottom = 6.dp, start = 15.dp, end = 15.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                // 이미지
                AsyncImage(
                    model = imageModel,
                    contentDescription = mountain.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder), // 로딩 중 보여줄 이미지
                    error = painterResource(R.drawable.error) // 실패 시 대체 이미지                )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)), //윤곽선
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("위치", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = myBlack)
                        Text(mountain.location, fontSize = 14.sp, color = myBlack)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)), //윤곽선
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("고도", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = myBlack)
                        Text("${mountain.height}m", fontSize = 14.sp, color = myBlack)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)), //윤곽선
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("설명", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = myBlack)
                        Text(mountain.text, fontSize = 14.sp, color = myBlack)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)), //윤곽선
                ) {
                    GoogleMapView(
                        context = context,
                        mountains = listOf(mountain),
                        navController = navController,
                        favorites= favorites
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .clickable{ navController.navigate("ReviewMountain/${mountainName}") },
                    colors = CardDefaults.cardColors(containerColor = GreenPrimaryDark),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)), //윤곽선
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "리뷰 보기 >",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
