package com.example.deungsan.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.foundation.background
import com.example.deungsan.components.MountainList
import com.example.deungsan.data.loader.JsonLoader

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.deungsan.ui.theme.GreenPrimaryLight
import androidx.navigation.NavController

@Composable
fun ListTab(context: Context, navController: NavController) {
    val mountains = JsonLoader.loadMountainsFromAssets(context)

    Column(modifier = Modifier.fillMaxSize()) {

        // 제목

            Text(
                text = "명산 리스트",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )


        // 리스트 표시 (기존)
        MountainList(mountains = mountains, navController = navController)
    }
    }
