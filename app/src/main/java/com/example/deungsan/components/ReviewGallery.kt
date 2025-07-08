package com.example.deungsan.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.*
import androidx.compose.material3.Text
import android.content.Context
import com.example.deungsan.data.model.Mountain
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import android.R
import com.example.deungsan.data.model.Review
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.deungsan.ui.theme.myBlack
import java.io.File

inline fun <T> List<T>.partitionIndexed(predicate: (Int, T) -> Boolean): Pair<List<T>, List<T>> {
    val first = mutableListOf<T>()
    val second = mutableListOf<T>()
    this.forEachIndexed { index, item ->
        if (predicate(index, item)) {
            first.add(item)
        } else {
            second.add(item)
        }
    }
    return Pair(first, second)
}

@Composable
fun ReviewGallery(
    reviews: List<Review>,
    navController: NavController,
    hiddenReviewIds: List<Int> // ← 추가
) {

    val visibleReviews = remember(reviews, hiddenReviewIds) {
        reviews.filterNot { it.id in hiddenReviewIds }
    }
    val shuffledReviews = visibleReviews.shuffled()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(visibleReviews.size) { index ->
            ReviewItem(shuffledReviews[index], navController)
        }
    }
    if (reviews.isEmpty()){
        Text(
            text = "커뮤니티 탭에서 리뷰를 작성해보세요!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReviewItem(review: Review, navController: NavController) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 4.dp)
            .wrapContentHeight()
            .clickable {
                navController.navigate("reviewDetail/${review.id}")
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 15.dp)) {

            // 작성자 이름 + 산 이름 한 줄에 양끝 배치
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
                    fontSize = 14.sp)

                Box(
                    modifier = Modifier
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp, vertical = (0.5).dp)
                ) {
                    Text(
                        text = review.mountain,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = myBlack
                    )
                }
            }



            // 사진: Rounded 제거, 가로 꽉 차게
            AsyncImage(
                model = "file://${File(context.filesDir, "reviews/${review.imagePath}")}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(3.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 리뷰 미리보기 텍스트
            Text(
                text = review.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = myBlack,
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 0.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "더보기",
                color = Color.Gray,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 0.dp)
            )
        }
    }
}


