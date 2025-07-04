package com.example.deungsan.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import com.example.deungsan.components.ReviewGallery
import com.example.deungsan.data.loader.JsonLoader

@Composable
fun GalleryTab(context: Context) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val reviews = JsonLoader.loadReviewsFromAssets(context)
        ReviewGallery(reviews)
    }
}
