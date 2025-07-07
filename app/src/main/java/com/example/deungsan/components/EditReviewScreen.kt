import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.deungsan.components.saveReviewsToFile
import com.example.deungsan.data.loader.JsonLoader
import java.io.File

@Composable
fun EditReviewScreen(
    reviewId: Int,
    onReviewUpdated: () -> Unit
) {
    val context = LocalContext.current
    val reviews = remember { JsonLoader.loadReviewsFromAssets(context).toMutableList() }
    val review = reviews.find { it.id == reviewId }

    var author by remember { mutableStateOf(review?.author ?: "") }
    var text by remember { mutableStateOf(review?.text ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    if (review == null) {
        Text("리뷰를 찾을 수 없습니다.")
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("등산 기록", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("작성자") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("내용") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("이미지 변경")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 미리보기
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "file://${File(context.filesDir, "reviews/${review.imagePath}")}"
                ),
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
                val newImagePath = imageUri?.let { uri ->
                    val newFileName = "${review.id}.jpeg"
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val file = File(context.filesDir, "reviews/$newFileName")
                    file.parentFile?.mkdirs()
                    val outputStream = file.outputStream()
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    newFileName
                } ?: review.imagePath

                val updatedReviews = reviews.map {
                    if (it.id == review.id) {
                        it.copy(author = author, text = text, imagePath = newImagePath)
                    } else {
                        it
                    }
                }

                saveReviewsToFile(context, updatedReviews)
                onReviewUpdated()
            },
            enabled = author.isNotBlank() && text.isNotBlank()
        ) {
            Text("수정 완료")
        }
    }
}
