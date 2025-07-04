package com.example.deungsan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.deungsan.ui.theme.DeungSanTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import com.example.deungsan.tabs.ListTab
import com.example.deungsan.tabs.GalleryTab
import com.example.deungsan.tabs.MyPageTab

import androidx.compose.ui.unit.dp


import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Person

import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.Color


import com.example.deungsan.ui.theme.GreenPrimary
import com.example.deungsan.ui.theme.GreenPrimaryDark
import com.example.deungsan.ui.theme.GreenPrimaryLight


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeungSanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TabWithSwipe()
                }
            }
        }
    }
}

// 탭 + 스와이프 화면
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabWithSwipe() {
    val items = listOf("명산", "소통창구", "마이페이지")
    val filledIcons = listOf(
        Icons.Filled.Terrain,
        Icons.Filled.Article,
        Icons.Filled.Person
    )

    val outlinedIcons = listOf(
        Icons.Outlined.Terrain,
        Icons.Outlined.Article,
        Icons.Outlined.Person
    )

    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar (
                modifier = Modifier.height(60.dp), // ← 원하는 높이로 설정
                containerColor = Color.White // 하단 바 배경 흰색
            ){
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            val icon = if (pagerState.currentPage == index) filledIcons[index] else outlinedIcons[index]
                            Icon(icon, contentDescription = label)
                        },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimaryDark,
                            selectedTextColor = GreenPrimaryDark,
                            indicatorColor = Color.Transparent
                        )

                    )

                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            count = items.size,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> ListTab()
                1 -> GalleryTab()
                2 -> MyPageTab()
            }
        }
    }
}