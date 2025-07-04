package com.example.deungsan.tabs


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context

@Composable
fun MyPageTab(context: Context) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("여기는 마이페이지 탭입니다!", fontSize = 20.sp)
    }
}