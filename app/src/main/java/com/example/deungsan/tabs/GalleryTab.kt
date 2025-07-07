package com.example.deungsan.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deungsan.components.AddReviewButton
import com.example.deungsan.components.ReviewGallery
import com.example.deungsan.data.loader.JsonLoader

@Composable
fun GalleryTab(context: Context, navController: NavController) {
    val reviews = remember {JsonLoader.loadReviewsFromAssets(context) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ReviewGallery(reviews, navController)
        AddReviewButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),

            onClick = { navController.navigate("addReview") }
        )
    }

}
