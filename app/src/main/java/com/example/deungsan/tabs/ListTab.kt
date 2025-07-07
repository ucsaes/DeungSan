package com.example.deungsan.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Brush
import com.example.deungsan.components.MountainList
import com.example.deungsan.data.loader.JsonLoader

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.deungsan.ui.theme.GreenPrimaryLight
import androidx.navigation.NavController
import com.example.deungsan.R

@Composable
fun ListTab(context: Context, navController: NavController) {
    val mountains = JsonLoader.loadMountainsFromAssets(context)
    val gradientHeight = 400.dp
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFFF7F7F7), Color(0xFFF7F7F7)),
        startY = 0f,
        endY = with(LocalDensity.current) { gradientHeight.toPx() }
    )



    Column(modifier = Modifier.fillMaxSize()) {
        DeungSanTopBar(
            onSearchClick = {
                // 검색 아이콘 클릭 시 동작
                println("검색 클릭됨!")
            }
        )

        // 제목
        Column (modifier = Modifier
        .background(brush=backgroundBrush)) {

            Text(
                text = "명산 리스트",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )


            // 리스트 표시 (기존)
            MountainList(mountains = mountains, navController = navController)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeungSanTopBar(
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // res/drawable/logo.png
                    contentDescription = "앱 로고",
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFFFFFF), // 여기서 배경색 지정!
            titleContentColor = Color.White,
            actionIconContentColor = Color.DarkGray
        )
    )
}