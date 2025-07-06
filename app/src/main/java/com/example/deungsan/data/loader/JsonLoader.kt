package com.example.deungsan.data.loader

import android.content.Context
import com.example.deungsan.data.model.Mountain
import com.example.deungsan.data.model.Review
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder

object JsonLoader {
    private const val ORIGINAL_FILE = "mountain.json"
    private const val CACHED_FILE = "mountain_with_summary.json"

    private val gson = Gson()

    fun loadMountainsFromAssets(context: Context): List<Mountain> {
        val cacheFile = File(context.filesDir, CACHED_FILE)

        // 캐시된 요약 파일이 있으면 그걸 사용
        if (cacheFile.exists()) {
            val json = cacheFile.readText()
            val type = object : TypeToken<List<Mountain>>() {}.type
            return gson.fromJson(json, type)
        }

        // 없으면 원본 json 불러오고 Wikipedia 요약 추가 후 저장
        val originalFile = File(context.filesDir, ORIGINAL_FILE)
        val originalJson = originalFile.readText()
        val type = object : TypeToken<List<Mountain>>() {}.type
        val mountainList: List<Mountain> = gson.fromJson(originalJson, type)

        val updatedList = runBlocking {
            mountainList.map { mountain ->
                val summary = fetchWikipediaSummary(mountain.name)
                mountain.copy(text = summary)
            }
        }

        // 결과를 캐시 파일로 저장
        val updatedJson = gson.toJson(updatedList)
        cacheFile.writeText(updatedJson)

        return updatedList
    }

    private suspend fun fetchWikipediaSummary(name: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val encodedName = URLEncoder.encode(name, "UTF-8")
                val url = "https://ko.wikipedia.org/api/rest_v1/page/summary/$encodedName"

                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val summary = JSONObject(json ?: "").optString("extract", "")
                    summary.ifBlank { "설명이 없습니다." }
                } else {
                    "위키백과 응답 오류"
                }
            } catch (e: Exception) {
                "위키백과 가져오기 실패: ${e.localizedMessage}"
            }
        }
    }
    fun loadReviewsFromAssets(context: Context): List<Review> {
        val json = File(context.filesDir,"reviews.json").readText()
        val gson = Gson()
        val type = object : TypeToken<List<Review>>() {}.type
        return gson.fromJson(json, type)
    }
}
