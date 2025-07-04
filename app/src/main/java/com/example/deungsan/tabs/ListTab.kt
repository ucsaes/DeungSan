package com.example.deungsan.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun ListTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 배경 + 제목
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(GreenPrimaryLight),  // 연두색 배경
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "명산 리스트",
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }
}
