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

@Composable
fun ReviewGallery(mountains: List<Mountain>) {
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ) {
        items(mountains) { mountain ->
            ReviewItem(mountain)
        }
    }
}

@Composable
fun MountainItem(
    mountain: Mountain
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        AsyncImage(
            model = "file:///android_asset/mountains/${mountain.imagePath}",
            contentDescription = mountain.name,
            modifier = Modifier
                .size(80.dp)
                .padding(end = 12.dp),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.stat_notify_error)
        )

        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = mountain.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${mountain.id}m — ${mountain.name}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}