import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.deungsan.components.saveReviewsToFile
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.model.Review
import com.example.deungsan.ui.theme.GreenPrimaryDark
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReviewScreen(
    reviewId: Int,
    currentUser: String,
    navController: NavController,
    onReviewUpdated: () -> Unit
) {
    val context = LocalContext.current
    val reviews = remember { JsonLoader.loadReviewsFromAssets(context).toMutableList() }
    val review = reviews.find { it.id == reviewId }

    // 예외 처리
    if (review == null) {
        Text("리뷰를 찾을 수 없습니다.")
        return
    }

    var text by remember { mutableStateOf(review.text) }

    // 화면 구성
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("리뷰 수정", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "취소")
                    }
                },
                actions = {
                    val isEnabled = text.isNotBlank()
                    TextButton(
                        onClick = {
                            val updatedReviews = reviews.map {
                                if (it.id == review.id) {
                                    it.copy(text = text) // 글만 수정
                                } else it
                            }
                            saveReviewsToFile(context, updatedReviews)
                            onReviewUpdated()
                            navController.popBackStack()
                        },
                        enabled = isEnabled
                    ) {
                        Text("수정", color = if (isEnabled) GreenPrimaryDark else Color.Gray, fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F7F7)
                )
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 이미지 보여주기
            Image(
                painter = rememberAsyncImagePainter(
                    model = "file://${File(context.filesDir, "reviews/${review.imagePath}")}"
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 내용 수정
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("내용을 수정해주세요") },
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

