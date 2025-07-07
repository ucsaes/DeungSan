package com.example.deungsan.tabs


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deungsan.components.MountainList
import com.example.deungsan.components.ReviewGallery
import com.example.deungsan.data.loader.JsonLoader

@Composable
fun MyPageTab(context: Context, navController: NavController) {
    Column (
        modifier = Modifier.fillMaxSize(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { navController.navigate("viewFavorite") },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.5.dp, Color.LightGray.copy(alpha=0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("즐겨찾는 산")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { navController.navigate("viewMyReview") },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.5.dp, Color.LightGray.copy(alpha=0.3f)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("내 리뷰")
            }
        }
    }
}

@Composable
fun MyFavPage(context: Context, navController: NavController) {
    val mountains = JsonLoader.loadMountainsFromAssets(context)
//    navController.popBackStack()
    MountainList(mountains = mountains, navController = navController, onlyFav = true)
}

@Composable
fun MyReviewPage(context: Context, navController: NavController) {
    val reviews = JsonLoader.loadReviewsFromAssets(context)
//    navController.popBackStack()
    ReviewGallery(reviews, navController)
}