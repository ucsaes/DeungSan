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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathSegment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.model.Review
import com.google.gson.Gson
import java.io.File
import com.example.deungsan.ui.theme.GreenPrimaryDark
@Composable
fun AddReviewButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = GreenPrimaryDark,
        shape = RoundedCornerShape(25.dp),
        contentColor = Color.White,      // 흰색 아이콘,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Review"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    currentUser: String,
    navController: NavController,
    onReviewAdded: () -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var mountain by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    // 상단 앱바
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("등산기록", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "취소")
                    }
                },colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F7F7)
                ),
                actions = {
                    val isEnabled = text.isNotBlank() && imageUri != null && mountain.isNotBlank()

                    TextButton(
                        onClick = {
                            val reviews = JsonLoader.loadReviewsFromAssets(context).toMutableList()
                            val newId = (reviews.maxByOrNull { it.id }?.id ?: 0) + 1
                            val fileName = "$newId.jpeg"

                            imageUri?.let { uri ->
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val file = File(context.filesDir, "reviews/$fileName")
                                file.parentFile?.mkdirs()
                                val outputStream = file.outputStream()
                                inputStream?.copyTo(outputStream)
                                inputStream?.close()
                                outputStream.close()
                            }

                            val newReview = Review(
                                id = newId,
                                author = currentUser,
                                mountain= mountain,
                                text = text,
                                imagePath = fileName
                            )
                            reviews.add(newReview)
                            saveReviewsToFile(context, reviews)
                            onReviewAdded()
                        },
                        enabled = isEnabled
                    ) {
                        Text("제출", color = if (isEnabled) GreenPrimaryDark else Color.Gray, fontSize = 20.sp)
                    }
                }
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // 사진 업로드 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f/3f) //가로:세로
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Text("사진을 넣어주세요", color = Color.White)
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(24.dp))


            // 산 이름 입력
            OutlinedTextField(
                value = mountain,
                onValueChange = { mountain = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("산 이름을 적어주세요") },
                colors = TextFieldDefaults.colors(
                    cursorColor = GreenPrimaryDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(16.dp))


            // 내용 입력
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("내용을 적어주세요") },
                colors = TextFieldDefaults.colors(
                    cursorColor = GreenPrimaryDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )
        }
    }
}





fun saveReviewsToFile(context: Context, reviews: List<Review>) {
    val json = Gson().toJson(reviews)
    val file = File(context.filesDir, "reviews.json")
    file.writeText(json)
}