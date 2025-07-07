package com.example.deungsan

import EditReviewScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.deungsan.ui.theme.DeungSanTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalConfiguration


import com.example.deungsan.ui.theme.GreenPrimaryDark
import com.example.deungsan.data.loader.copyJsonIfNotExists

import androidx.navigation.compose.rememberNavController

import com.example.deungsan.components.MountainDetailScreen


import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.example.deungsan.components.AddReviewScreen
import com.example.deungsan.components.ReviewDetailScreen


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
@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
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

    val density = LocalConfiguration.current.densityDpi

    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        //화면 간 전환 정의
        AnimatedNavHost(
            navController = navController,
            startDestination = "mainTabs",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable("mainTabs") {
                Scaffold(
                    bottomBar = {
                        //하단 탭 그림자, 톤
                        Surface(
                            tonalElevation = 4.dp,
                            shadowElevation = 20.dp
                        ) {
                            NavigationBar(
                                modifier = Modifier.height(100.dp),
                                containerColor = Color.White
                            ) {
                                items.forEachIndexed { index, label ->
                                    NavigationBarItem(
                                        selected = pagerState.currentPage == index,
                                        //탭 클릭시 이동
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
                            .background(Color(0xFFFFFFFF))
                    ) { page ->
                        when (page) {
                            0 -> ListTab(context, navController)
                            1 -> GalleryTab(context, navController)
                            2 -> MyPageTab(context)
                        }
                    }
                }
            }

            // 탭1 상세 페이지로 이동
            composable(
                route = "detail/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                MountainDetailScreen(name)
            }

            //탭2 상세페이지로 이동
            composable(
                route = "reviewDetail/{reviewId}",
                arguments = listOf(navArgument("reviewId") { type = NavType.IntType }),
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val reviewId = backStackEntry.arguments?.getInt("reviewId")
                reviewId?.let {
                    val currentUser = "한다인이"
                    ReviewDetailScreen(
                        reviewId = it,
                        navController = navController,     // 여기에서 navController 전달
                        currentUser = currentUser          // 현재 사용자 이름 전달
                    )
                }
            }

            //탭2 리뷰 추가
            composable(
                route = "addReview",
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                }
            ) {
                AddReviewScreen(onReviewAdded = {
                    navController.popBackStack() // 등록 후 뒤로가기
                })
            }


            // 탭2 리뷰 수정
            composable(
                route = "editReview/{reviewId}",
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val reviewId = backStackEntry.arguments?.getString("reviewId")?.toIntOrNull()
                if (reviewId != null) {
                    EditReviewScreen(
                        reviewId = reviewId,
                        onReviewUpdated = {
                            navController.popBackStack() // 수정 후 뒤로가기
                        }
                    )
                } else {
                    // 예외 처리: ID 파싱 실패
                    Text("잘못된 리뷰 ID입니다.")
                }
            }


        }
    }
    }