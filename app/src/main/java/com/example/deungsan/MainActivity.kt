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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val items = listOf("리스트", "갤러리", "마이페이지")
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) }, // 아이콘 변경 가능
                        label = { Text(label) }
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