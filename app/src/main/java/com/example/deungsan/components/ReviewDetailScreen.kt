package com.example.deungsan.components

import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.model.Mountain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(reviewId: Int) {
    val context = LocalContext.current
    val reviews = remember { JsonLoader.loadReviewsFromAssets(context) }
    val review = reviews.find { it.id == reviewId }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("리뷰 상세") })
        }
    ) { innerPadding ->
        if (review != null) {
            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)) {
                AsyncImage(
                    model = "file:///android_asset/reviews/${review.imagePath}",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = review.text, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "- ${review.author}", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text("리뷰를 찾을 수 없습니다.")
        }
    }
}
