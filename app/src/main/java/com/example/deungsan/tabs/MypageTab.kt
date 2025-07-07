package com.example.deungsan.tabs


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.navigation.NavController
import com.example.deungsan.components.MountainList
import com.example.deungsan.data.loader.JsonLoader

@Composable
fun MyPageTab(context: Context, navController: NavController) {
    val mountains = JsonLoader.loadMountainsFromAssets(context)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MountainList(mountains = mountains, navController = navController, onlyFav = true)
    }
}