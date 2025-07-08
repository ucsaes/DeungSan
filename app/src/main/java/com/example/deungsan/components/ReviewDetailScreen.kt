package com.example.deungsan.components

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.deungsan.data.loader.ReportViewModel
import com.example.deungsan.data.model.Mountain
import com.example.deungsan.data.model.Review
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deungsan.ui.theme.myBlack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    reviewId: Int,
    navController: NavController,
    currentUser: String,
    reportViewModel: ReportViewModel = viewModel() // ViewModel 사용
) {
    val context = LocalContext.current
    val reviews = remember { JsonLoader.loadReviewsFromAssets(context) }
    val review = reviews.find { it.id == reviewId }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var showMenu by remember { mutableStateOf(false) }
    val reportedIds by reportViewModel.reported.collectAsState() // 신고 목록 관찰

    val hasReported by remember(reportedIds, review?.id) {
        derivedStateOf { review?.id.toString() in reportedIds }


        }
    LaunchedEffect(Unit) { reportViewModel.loadReports(context)}


    // 트리거가 발생하면 5초 후 신고 상태 true로
    var triggerReportDelay by remember { mutableStateOf(false) }


    LaunchedEffect(triggerReportDelay) {
        if (triggerReportDelay && review != null) {
            delay(5000)
            reportViewModel.addReport(context, review.id.toString())
            triggerReportDelay = false
        }
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("등산 기록", fontSize = 20.sp, color = myBlack) },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = myBlack)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = myBlack)
                        }

                        //상단 메뉴 설정
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(Color(0xFFF7F7F7)) // 메뉴 전체 배경색
                        ) {
                            if (review.author == currentUser) {
                                DropdownMenuItem(
                                    text = { Text("수정", color = myBlack) },
                                    onClick = {
                                        showMenu = false
                                        navController.navigate("editReview/${review.id}")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("삭제", color = myBlack) },
                                    onClick = {
                                        showMenu = false
                                        deleteReview(context, review)
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(if (hasReported) "신고 취소" else "신고", color = myBlack) },
                                    onClick = {
                                        showMenu = false
                                        if (hasReported) {
                                            reportViewModel.removeReport(context, review.id.toString())
                                            Toast.makeText(context, "신고가 취소되었습니다.", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            reportViewModel.addReport(context, review.id.toString())
                                            Toast.makeText(context, "리뷰가 신고되었습니다.", Toast.LENGTH_SHORT).show()
                                            triggerReportDelay = true
                                            navController.popBackStack()
                                        }
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
                .padding(top = 0.dp, bottom = 0.dp, start = 15.dp, end =15.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp, end = 3.dp, top = 2.dp, bottom = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.author,
                    color = myBlack,
                    fontSize = 16.sp)

                Box(
                    modifier = Modifier
                        .clickable(onClick = {navController.navigate("detail/${review.mountain}")})
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 7.dp, vertical = (2.5).dp)
                ) {
                    Text(
                        text = "${review.mountain} >",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = myBlack
                    )
                }
            }
            AsyncImage(
                model = "file://${File(context.filesDir, "reviews/${review.imagePath}")}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = review.text, style = MaterialTheme.typography.bodyLarge, color = myBlack, fontSize = 14.sp)
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




