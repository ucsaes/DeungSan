package com.example.deungsan.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import com.example.deungsan.components.MountainList
import com.example.deungsan.data.loader.JsonLoader

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.deungsan.ui.theme.GreenPrimaryLight
import androidx.navigation.NavController
import com.example.deungsan.R
import kotlinx.coroutines.delay
import androidx.compose.ui.text.TextStyle
import com.example.deungsan.ui.theme.myBlack

@Composable
fun ListTab(context: Context, navController: NavController) {
    val mountains = remember { JsonLoader.loadMountainsFromAssets(context) }
    val gradientHeight = 400.dp
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFFF7F7F7), Color(0xFFF7F7F7)),
        startY = 0f,
        endY = with(LocalDensity.current) { gradientHeight.toPx() }
    )
    var searchQuery by remember { mutableStateOf("") }
    var isSearchMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // TextField 포커스 강제 요청
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            delay(100)
            focusRequester.requestFocus()
        }
    }



    Column(modifier = Modifier.fillMaxSize()) {
        DeungSanTopBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            isSearchMode = isSearchMode,
            onSearchClick = { isSearchMode = true },
            onClearClick = {
                searchQuery = ""
                isSearchMode = false
            },
            focusRequester = focusRequester
        )

        // 제목
        Column (modifier = Modifier
        .background(brush=backgroundBrush)) {
            Text(
                text = "명산 리스트",
                fontSize = 20.sp,
                color = myBlack,
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )


            // 리스트 표시 (기존)
            MountainList(mountains = mountains, navController = navController, searchQuery = searchQuery)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeungSanTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchMode: Boolean,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit,
    focusRequester: FocusRequester
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // res/drawable/logo.png
                    contentDescription = "앱 로고",
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        },
        actions = {
            if (isSearchMode || searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(40.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            onSearchQueryChange(it)
                            if (it.isBlank()) onClearClick()
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = myBlack
                        ),
                        cursorBrush = SolidColor(myBlack),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "산 이름을 입력하세요",
                                    color = Color.Gray,
                                    fontSize = 15.sp,
                                    modifier = Modifier.fillMaxWidth()  // 여기를 추가
                                )
                            }
                            innerTextField()
                        }
                    )
                }
                if (searchQuery.isNotBlank()) {
                    IconButton(
                        onClick = onClearClick
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "지우기", tint = Color.Gray)
                    }
                } else {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "지우기", tint = Color.Transparent)
                    }
                }
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFFFFFF), // 여기서 배경색 지정!
            titleContentColor = Color.White,
            actionIconContentColor = Color.DarkGray
        )
    )
}