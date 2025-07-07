package com.example.deungsan.components

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.model.Mountain
import com.example.deungsan.data.model.Review
import com.google.gson.Gson
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    reviewId: Int,
    navController: NavController,
    currentUser: String,
    hiddenReviewIds: MutableList<Int> // ← 추가
) {
    val context = LocalContext.current
    val reviews = remember { JsonLoader.loadReviewsFromAssets(context) }
    val review = reviews.find { it.id == reviewId }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var showMenu by remember { mutableStateOf(false) }

    if (review == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("리뷰 없음") },
                    navigationIcon = {
                        IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).padding(20.dp)) {
                Text("해당 리뷰를 찾을 수 없습니다.")
            }
        }
        return
    }

    fun hideReview(reviewId: Int) {
        if (!hiddenReviewIds.contains(reviewId)) {
            hiddenReviewIds.add(reviewId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("등산 기록", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (review.author == currentUser) {
                                DropdownMenuItem(
                                    text = { Text("수정") },
                                    onClick = {
                                        showMenu = false
                                        navController.navigate("editReview/${review.id}")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("삭제") },
                                    onClick = {
                                        showMenu = false
                                        deleteReview(context, review)
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("신고") },
                                    onClick = {
                                        showMenu = false
                                        hideReview(review.id) // 숨기기 수행
                                        Toast.makeText(context, "리뷰가 신고되었습니다.", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("차단") },
                                    onClick = {
                                        showMenu = false
                                        hideReview(review.id)
                                        Toast.makeText(context, "${review.author} 님이 차단되었습니다.", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F7F7))
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            AsyncImage(
                model = "file://${File(context.filesDir, "reviews/${review.imagePath}")}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = review.author, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = review.text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}




fun deleteReview(context: Context, review: Review) {
    // 1. 기존 리뷰 목록 불러오기
    val reviews = JsonLoader.loadReviewsFromAssets(context).toMutableList()

    // 2. 삭제할 리뷰 제거
    reviews.removeIf { it.id == review.id }

    // 3. 리뷰 이미지 파일 삭제
    val imageFile = File(context.filesDir, "reviews/${review.imagePath}")
    if (imageFile.exists()) {
        imageFile.delete()
    }

    // 4. 수정된 리뷰 목록 저장
    val json = Gson().toJson(reviews)
    val file = File(context.filesDir, "reviews.json")
    file.writeText(json)
}




