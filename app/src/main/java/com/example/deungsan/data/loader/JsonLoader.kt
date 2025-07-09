package com.example.deungsan.data.loader

import android.content.Context
import com.example.deungsan.BuildConfig
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
        val apiKey = BuildConfig.MAPS_API_KEY

        // 1. 캐시가 있으면 캐시 반환
        if (cacheFile.exists()) {
            val json = cacheFile.readText()
            val type = object : TypeToken<List<Mountain>>() {}.type
            return gson.fromJson(json, type)
        }

        // 2. 원본 JSON 불러오기
        val originalFile = File(context.filesDir, ORIGINAL_FILE)
        val originalJson = originalFile.readText()
        val type = object : TypeToken<List<Mountain>>() {}.type
        val mountainList: List<Mountain> = gson.fromJson(originalJson, type)

        // 3. 요약 & 위도경도 붙이기
        val updatedList = runBlocking {
            mountainList.map { mountain ->
                val summary = fetchWikipediaSummary(mountain.name)
                val (lat, lng) = fetchCoordinatesFromGoogle(mountain.name, apiKey)
                mountain.copy(
                    text = summary,
                    latitude = lat,
                    longitude = lng
                )
            }
        }

        // 4. 캐시 저장
        val updatedJson = gson.toJson(updatedList)
        cacheFile.writeText(updatedJson)

        return updatedList
    }

    suspend fun fetchWikipediaSummary(name: String): String {
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

    suspend fun fetchCoordinatesFromGoogle(name: String, apiKey: String): Pair<Double, Double> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedName = URLEncoder.encode("${name} 산", "UTF-8")
                val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedName&key=$apiKey"

                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    val results = json.optJSONArray("results")
                    if (results != null && results.length() > 0) {
                        val location = results.getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                        val lat = location.getDouble("lat")
                        val lng = location.getDouble("lng")
                        return@withContext lat to lng
                    }
                }
                0.0 to 0.0
            } catch (e: Exception) {
                e.printStackTrace()
                0.0 to 0.0
            }
        }
    }

    fun loadReviewsFromAssets(context: Context): List<Review> {
        val json = File(context.filesDir, "reviews.json").readText()
        val type = object : TypeToken<List<Review>>() {}.type
        return gson.fromJson(json, type)
    }
}
