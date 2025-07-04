package com.example.deungsan

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import java.io.File
import java.io.FileOutputStream

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

import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyJsonIfNotExists(this)
        setContent {
            DeungSanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TabWithSwipe(this)
                }
            }
        }
    }
}


// 탭 + 스와이프 화면
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabWithSwipe(context: Context) {
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
    val navController = rememberNavController()  // ← NavController 추가

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        NavHost(
            navController = navController,
            startDestination = "mainTabs"
        ) {
            composable("mainTabs") {
                Scaffold(
                    bottomBar = {
                        Surface(
                            tonalElevation = 4.dp,
                            shadowElevation = 10.dp
                        ) {
                            NavigationBar(
                                modifier = Modifier.height(100.dp),
                                containerColor = Color.White
                            ) {
                                items.forEachIndexed { index, label ->
                                    NavigationBarItem(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        },
                                        icon = {
                                            val icon = if (pagerState.currentPage == index)
                                                filledIcons[index]
                                            else
                                                outlinedIcons[index]
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
                    }
                ) { innerPadding ->
                    HorizontalPager(
                        state = pagerState,
                        count = items.size,
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(Color(0xFFFCFCFC))
                    ) { page ->
                        when (page) {
                            0 -> ListTab(context, navController)
                            1 -> GalleryTab(context)
                            2 -> MyPageTab(context)
                        }
                    }
                }
            }

            // 상세 페이지 등록
            composable("detail/{name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                MountainDetailScreen(name)
            }
        }
    }
}

fun copyJsonIfNotExists(context: Context) {
    val file_1 = File(context.filesDir, "reviews.json")
    if (!file_1.exists()) {
        val assetManager = context.assets
        val inputStream = assetManager.open("reviews.json")
        val outputStream = FileOutputStream(file_1)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }
    val file_2 = File(context.filesDir, "mountain.json")
    if (!file_2.exists()) {
        val assetManager = context.assets
        val inputStream = assetManager.open("mountain.json")
        val outputStream = FileOutputStream(file_2)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }
}