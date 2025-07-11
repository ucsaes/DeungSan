package com.example.deungsan

import EditReviewScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deungsan.components.AddReviewScreen
import com.example.deungsan.components.ReviewDetailScreen
import com.example.deungsan.components.ReviewGallery
import com.example.deungsan.components.ReviewItem
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.tabs.BlockedReviewTab
import com.example.deungsan.tabs.MyFavPage
import com.example.deungsan.tabs.MyReviewPage
import com.example.deungsan.ui.theme.myBlack

val LocalCurrentUser = compositionLocalOf { "unknown" }  // 기본값

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyJsonIfNotExists(this)
        setContent {
            CompositionLocalProvider(LocalCurrentUser provides "김등산") {
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
        val items = listOf("산", "커뮤니티", "마이페이지")
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

        val gradientHeight = 400.dp
        val backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color.White, Color(0xFFF7F7F7), Color(0xFFF7F7F7)),
            startY = 0f,
            endY = with(LocalDensity.current) { gradientHeight.toPx() }
        )
        val hiddenReviewIds = remember { mutableStateListOf<Int>() }

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
                                                unselectedIconColor = Color(0xFFADADAD),
                                                selectedTextColor = GreenPrimaryDark,
                                                unselectedTextColor = Color(0xFFADADAD),
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
                                .background(brush = backgroundBrush)
                        ) { page ->
                            when (page) {
                                0 -> ListTab(context, navController)
                                1 -> GalleryTab(context, navController, hiddenReviewIds)
                                2 -> MyPageTab(context, navController)
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
                    MountainDetailScreen(name, navController)
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
                        ReviewDetailScreen(
                            reviewId = it,
                            navController = navController,     // 여기에서 navController 전달
                            currentUser = LocalCurrentUser.current,        // 현재 사용자 이름 전달

                        )
                    }
                }

                //탭2 등산 기록 추가
                composable(
                    route = "addReview",
                    enterTransition = {
                        slideInVertically(
                            initialOffsetY = { it },           // 아래에서 위로 등장
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutVertically(
                            targetOffsetY = { -it },           // 위로 사라짐
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideInVertically(
                            initialOffsetY = { -it },          // 위에서 아래로 등장 (뒤로가기로 돌아올 때)
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutVertically(
                            targetOffsetY = { it },            // 아래로 사라짐 (뒤로가기)
                            animationSpec = tween(300)
                        )
                    }
                ) { backStackEntry ->
                    AddReviewScreen(
                        onReviewAdded = {
                            navController.popBackStack()
                        },
                        currentUser = LocalCurrentUser.current,
                        navController = navController
                    )
                }


                // 탭2 등산 기록 수정
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
                )
                { backStackEntry ->
                    val reviewId = backStackEntry.arguments?.getString("reviewId")?.toIntOrNull()
                    if (reviewId != null) {
                        EditReviewScreen(
                            reviewId = reviewId,
                            onReviewUpdated = {
                                navController.popBackStack() // 수정 후 뒤로가기
                            },
                            currentUser = LocalCurrentUser.current,
                            navController = navController
                        )
                    } else {
                        // 예외 처리: ID 파싱 실패
                        Text("잘못된 등산 기록 ID입니다.")
                    }
                }



                composable(
                    route = "viewFavorite",
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
                    MyFavPage(navController)
                }

                composable(
                    route = "viewMyReview",
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
                    MyReviewPage(navController)
                }

                composable(
                    route = "viewBlocked",
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
                    BlockedReviewTab(
                            navController = navController,

                        )



                }

                //탭3&1
                composable(
                    route = "mountain_detail/{mountainName}",
                    arguments = listOf(navArgument("mountainName") { type = NavType.StringType }),
                    enterTransition = {
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(300)
                        )
                    }
                ) { backStackEntry ->
                    val mountainName = backStackEntry.arguments?.getString("mountainName") ?: ""
                    MountainDetailScreen(mountainName, navController)
                }

                composable(
                    route = "ReviewMountain/{name}",
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
                    val reviews = remember {JsonLoader.loadReviewsFromAssets(context) }
                    val visibleReviews = remember(reviews, name) {
                        reviews.filter { it.mountain == name }
                    }
                    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                    Scaffold(
                        topBar = {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp) // 원하는 두께로 조절
                                        .background(Color(0xFFF7F7F7))
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
                                        text = "${name} 등산 기록들",
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
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .background(Color(0xFFF7F7F7))
                        ) {
                            ReviewGallery(visibleReviews, navController, hiddenReviewIds)
                        }
                    }
                }


            }
        }
    }
}
