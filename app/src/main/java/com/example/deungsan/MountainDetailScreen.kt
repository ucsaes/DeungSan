package com.example.deungsan

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.model.Mountain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MountainDetailScreen(mountainName: String) {
    val context: Context = LocalContext.current
    val mountains: List<Mountain> = remember { JsonLoader.loadMountainsFromAssets(context) }
    val mountain = mountains.find { it.name == mountainName }

    if (mountain == null) {
        // fallback: 찾을 수 없는 경우
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("산 정보 없음") })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(20.dp)
            ) {
                Text("해당 산 정보를 찾을 수 없습니다.")
            }
        }
        return
    }

    // 정상적으로 찾은 경우
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(mountain.name) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(text = mountain.text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
