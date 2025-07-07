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
import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.deungsan.data.loader.MountainViewModel
import kotlin.text.contains


@Composable
fun MountainList(viewModel: MountainViewModel = viewModel(), mountains: List<Mountain>, navController: NavController, onlyFav: Boolean = false) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadFavorites(context)
    }
    val favorites by viewModel.favorites.collectAsState()


    val gradientHeight = 400.dp


    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(mountains) { mountain ->
            if (!onlyFav || (mountain.name in favorites)) {
                MountainItem(
                    mountain = mountain,
                    isFavorite = (mountain.name in favorites),
                    onDetailsClick = { navController.navigate("detail/${mountain.name}") },
                    onFavClick = {
                        viewModel.toggleFavorite(
                            context,
                            mountain.name
                        )
                    }
                )
            }
        }
        if (onlyFav && favorites.isEmpty()) {
            item {
                Text(
                    text = "좋아하는 산을 만들어보세요!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MountainItem(
    mountain: Mountain,
    isFavorite: Boolean,
    onDetailsClick: () -> Unit,
    onFavClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onDetailsClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)), //윤곽선
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mountain.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${mountain.height}m — ${mountain.name}\n${mountain.location}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = { onFavClick() },
                modifier = Modifier
                    .size(34.dp) // ← 원하는 고정 크기
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) Color(0xFFFF6262) else Color(0xFFB9B9B9)
                )
            }
        }
    }
}