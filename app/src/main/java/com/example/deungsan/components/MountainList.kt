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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import android.R

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke


@Composable
fun MountainList(mountains: List<Mountain>) {
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ) {
        items(mountains) { mountain ->
            MountainItem(mountain)
        }
    }
}

@Composable
fun MountainItem(
    mountain: Mountain
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "file:///android_asset/mountains/${mountain.imagePath}",
                contentDescription = mountain.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = mountain.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${mountain.id}m — ${mountain.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}