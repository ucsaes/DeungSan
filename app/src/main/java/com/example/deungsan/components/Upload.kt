package com.example.deungsan.components


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun AddReviewButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFFA7D5A9),     // 빨간색 배경
        shape = RoundedCornerShape(25.dp),
        contentColor = Color.White,      // 흰색 아이콘,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Review"
        )
    }
}