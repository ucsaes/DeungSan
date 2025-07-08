package com.example.deungsan.components

import android.R
import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.model.Mountain
import com.example.deungsan.ui.theme.myBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MountainDetailScreen(mountainName: String) {
    val context: Context = LocalContext.current
    val mountains: List<Mountain> = remember { JsonLoader.loadMountainsFromAssets(context) }
    val mountain = mountains.find { it.name == mountainName }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

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
                            .height(50.dp) // 원하는 두께로 조절
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
                    .padding(top = 15.dp, bottom = 6.dp, start = 15.dp, end = 15.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                // 이미지
                AsyncImage(
                    model = "file:///android_asset/mountains/${mountain.imagePath}",
                    contentDescription = mountain.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
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
            }
        }
    }
}
