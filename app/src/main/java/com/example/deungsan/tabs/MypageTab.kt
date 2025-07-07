package com.example.deungsan.tabs


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deungsan.components.MountainList
import com.example.deungsan.components.ReviewGallery
import com.example.deungsan.components.deleteReview
import com.example.deungsan.data.loader.JsonLoader
import java.io.File
import com.example.deungsan.LocalCurrentUser
import com.example.deungsan.ui.theme.GreenPrimaryDark
import java.nio.file.WatchEvent
import com.example.deungsan.R


@Composable
fun MyPageTab(context: Context, navController: NavController) {
    val image_source = R.drawable.default_profile

    Column (
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = GreenPrimaryDark),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = image_source,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Text(text="즐겨찾는 산", color=Color.White)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp)
                .clickable { navController.navigate("viewFavorite") },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("즐겨찾는 산")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp)
                .clickable { navController.navigate("viewMyReview") },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.RateReview, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("내 리뷰")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFavPage(navController: NavController) {
    val context = LocalContext.current
    val mountains = JsonLoader.loadMountainsFromAssets(context)
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "등산 기록",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F7F7)
                )
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            MountainList(
                mountains = mountains,
                navController = navController,
                onlyFav = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewPage(navController: NavController) {
    val context = LocalContext.current
    val reviews = JsonLoader.loadReviewsFromAssets(context)
    val currentUser = LocalCurrentUser.current  // 현재 사용자 이름 가져오기

    val myReviews = reviews.filter { it.author == currentUser }  // 내 리뷰만 필터링
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "등산 기록",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F7F7)
                )
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            ReviewGallery(myReviews, navController)
        }
    }
}