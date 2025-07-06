package com.example.deungsan.data.loader

import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.deungsan.data.model.Mountain

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🏔️ ${mountain.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
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

            Column {
                Text("📍 위치", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(mountain.location, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text("🗻 고도:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${mountain.height}m", fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text("📝 설명:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(mountain.text, fontSize = 15.sp)
            }
        }
    }
}
