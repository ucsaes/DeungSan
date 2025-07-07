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
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.model.Review
import com.google.gson.Gson
import java.io.File

@Composable
fun AddReviewButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFFA7D5A9),     // 연녹색
        shape = RoundedCornerShape(25.dp),
        contentColor = Color.White,      // 흰색 아이콘,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Review"
        )
    }
}

@Composable
fun AddReviewScreen(currentUser: String,
                    onReviewAdded: () -> Unit) {
    val context = LocalContext.current
    var author by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("등산 기록", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))



        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("내용") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 1
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA7D5A9), // 연녹색
                contentColor = Color.White         // 텍스트 색상 (선택)
            )
        ) {
            Text("이미지 선택")
        }
        imageUri?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val reviews = JsonLoader.loadReviewsFromAssets(context).toMutableList()
                val newId = (reviews.maxByOrNull { it.id }?.id ?: 0) + 1
                val fileName = "$newId.jpeg"

                // 이미지 파일 저장
                imageUri?.let { uri ->
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val file = File(context.filesDir, "reviews/$fileName")
                    file.parentFile?.mkdirs()
                    val outputStream = file.outputStream()
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                }

                // 리뷰 추가
                val newReview = Review(
                    id = newId,
                    author = author,
                    text = text,
                    imagePath = fileName
                )
                reviews.add(newReview)

                // JSON 저장
                saveReviewsToFile(context, reviews)

                onReviewAdded()
            },
            enabled = text.isNotBlank() && imageUri != null
        ) {
            Text("등록")
        }
    }
}

fun saveReviewsToFile(context: Context, reviews: List<Review>) {
    val json = Gson().toJson(reviews)
    val file = File(context.filesDir, "reviews.json")
    file.writeText(json)
}