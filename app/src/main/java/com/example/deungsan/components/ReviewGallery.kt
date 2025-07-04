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
fun ReviewGallery(reviews: List<Review>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(reviews.size) { index ->
            ReviewItem(reviews[index])
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = "file:///android_asset/reviews/${review.imagePath}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 200.dp), // 높이 다양하게 줘야 masonry 느낌 남
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = review.text)
            Text(text = "- ${review.author}", style = MaterialTheme.typography.bodySmall)
        }
    }
}