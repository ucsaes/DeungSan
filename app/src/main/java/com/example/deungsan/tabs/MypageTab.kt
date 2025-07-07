package com.example.deungsan.tabs


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Block
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.deungsan.components.MountainList
import com.example.deungsan.components.ReviewGallery
import com.example.deungsan.components.deleteReview
import com.example.deungsan.data.loader.JsonLoader
import java.io.File
import com.example.deungsan.LocalCurrentUser
import com.example.deungsan.ui.theme.GreenPrimaryDark
import java.nio.file.WatchEvent
import com.example.deungsan.R
import com.example.deungsan.components.ReviewItem
import com.example.deungsan.data.model.Review
import java.io.FileOutputStream
import kotlin.math.exp


@Composable
fun MyPageTab(context: Context, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val profileFile = File(context.filesDir, "user_profile.jpg")
    var reloadKey by remember { mutableStateOf(System.currentTimeMillis()) }

    val imageRequest = if (profileFile.exists()) {
        ImageRequest.Builder(context)
            .data(profileFile)
            .diskCachePolicy(CachePolicy.DISABLED)    // 디스크 캐시 끄기
            .memoryCachePolicy(CachePolicy.DISABLED)  // 메모리 캐시 끄기
            .setParameter("reloadKey", reloadKey)
            .build()
    } else {
        ImageRequest.Builder(context)
            .data(R.drawable.default_profile)
            .build()
    }
    val uploadProfile = rememberUploadProfileLauncher(context) { success ->
        if (success) {
            reloadKey = System.currentTimeMillis()
            Toast.makeText(context, "프로필 사진 저장 완료, 재실행해주세요", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "프로필 사진 저장 실패", Toast.LENGTH_SHORT).show()
        }
    }


    Column (
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = { expanded = !expanded }),
            colors = CardDefaults.cardColors(containerColor = GreenPrimaryDark),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Column(
            ) {
                Row(
                    modifier = Modifier.padding(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = "프로필 이미지",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "김등산",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(text = "님, 반갑습니다!", color = Color.White, fontSize = 17.sp)
                }
                if (expanded) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable (onClick = {
                                uploadProfile()
                                expanded = !expanded
                            }),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,

                    ) {
                        Text(
                            "프로필 사진 설정하기",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                    if (profileFile.exists()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    val deleted = deleteProfileImage(context)
                                    if (deleted) {
                                        reloadKey = System.currentTimeMillis()
                                        expanded = !expanded
                                        Toast.makeText(context, "이미지를 기본으로 되돌렸습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                })
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Transparent,
                            ) {
                            Text(
                                "기본 프로필로 설정하기",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp)
                .clickable { navController.navigate("viewBlocked") },  // 목적에 맞는 route
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Block, contentDescription = "신고된 리뷰")
                Spacer(Modifier.width(12.dp))
                Text("신고된 리뷰")
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
            ReviewGallery(myReviews, navController, hiddenReviewIds = emptyList())
        }
    }
}

@Composable
fun rememberUploadProfileLauncher(
    context: Context,
    onResult: (Boolean) -> Unit
): () -> Unit {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Uri 변경 시 내부 저장소에 저장
    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            val success = saveUriToFilesDir(context, uri, "user_profile.jpg")
            onResult(success)
        }
    }

    // 함수 반환: 호출하면 사진 선택기 실행
    return {
        launcher.launch("image/*")
    }
}

// 내부 저장소 저장 함수
fun saveUriToFilesDir(context: Context, imageUri: Uri, fileName: String): Boolean {
    return try {
        context.contentResolver.openInputStream(imageUri)?.use { input ->
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun deleteProfileImage(context: Context, fileName: String = "user_profile.jpg"): Boolean {
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        file.delete()
    } else {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedReviewTab(
    navController: NavController,
    hiddenReviewIds: List<Int>
) {
    val context = LocalContext.current
    val reviews = remember(hiddenReviewIds) {
        JsonLoader.loadReviewsFromAssets(context)
    }
    val hiddenReviews = remember(reviews, hiddenReviewIds) {
        reviews.filter { it.id in hiddenReviewIds }
    }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "신고된 리뷰",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {backDispatcher?.onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F7F7)
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(4.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(hiddenReviews.size) { index ->
                ReviewItem(hiddenReviews[index], navController)
            }
        }
    }
}


