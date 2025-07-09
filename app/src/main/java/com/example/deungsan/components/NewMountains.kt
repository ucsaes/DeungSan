import android.content.Context
import com.example.deungsan.BuildConfig
import com.example.deungsan.data.loader.JsonLoader
import com.example.deungsan.data.loader.JsonLoader.fetchCoordinatesFromGoogle
import com.example.deungsan.data.loader.JsonLoader.fetchWikipediaSummary
import com.example.deungsan.data.model.Mountain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

data class WikidataMountainInfo(
    val height: Int,
    val location: String,
    val imagePath: String
)

private val httpClient = OkHttpClient()

// 1. Wikidata에서 고도(height), 위치(location), 이미지(imagePath) 가져오기
suspend fun fetchFromWikidata(name: String): WikidataMountainInfo? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedName = URLEncoder.encode(name, "UTF-8")
            val searchUrl = "https://www.wikidata.org/w/api.php?action=wbsearchentities&search=$encodedName&language=ko&format=json"
            val searchRequest = Request.Builder().url(searchUrl).build()

            val qid = httpClient.newCall(searchRequest).execute().use { searchResponse ->
                val searchJson = JSONObject(searchResponse.body?.string() ?: return@use null)
                searchJson.getJSONArray("search").optJSONObject(0)?.optString("id")
            } ?: return@withContext null

            val entityUrl = "https://www.wikidata.org/wiki/Special:EntityData/$qid.json"
            val entityRequest = Request.Builder().url(entityUrl).build()

            return@withContext httpClient.newCall(entityRequest).execute().use { entityResponse ->
                val entityJson = JSONObject(entityResponse.body?.string() ?: return@use null)
                val entity = entityJson.getJSONObject("entities").getJSONObject(qid)
                val claims = entity.getJSONObject("claims")

                // 고도 (P2044)
                val height = claims.optJSONArray("P2044")?.optJSONObject(0)
                    ?.getJSONObject("mainsnak")?.getJSONObject("datavalue")
                    ?.getJSONObject("value")?.getString("amount")?.toFloat()?.toInt() ?: 0

                // 위치 설명 (ko)
                val location = entity.optJSONObject("descriptions")
                    ?.optJSONObject("ko")?.optString("value") ?: "위치 정보 없음"

                // 이미지 (P18)
                val imageFileName = claims.optJSONArray("P18")?.optJSONObject(0)
                    ?.getJSONObject("mainsnak")?.getJSONObject("datavalue")
                    ?.getString("value") ?: ""



                val request = Request.Builder()
                    .url("https://commons.wikimedia.org/wiki/Special:FilePath/${URLEncoder.encode(imageFileName, "UTF-8")}")
                    .head() // HEAD 요청으로 리디렉션만 추적
                    .build()

                val response = httpClient.newCall(request).execute()
                val resolvedUrl = response.request.url.toString()


                WikidataMountainInfo(
                    height = height,
                    location = location,
                    imagePath = resolvedUrl
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// 2. 모든 정보를 종합하여 Mountain 객체 생성
suspend fun fetchMountainData(context: Context, name: String): Mountain? {
    val summary = fetchWikipediaSummary(name)
    val wiki = fetchFromWikidata(name)
    val latlng = fetchCoordinatesFromGoogle(name, BuildConfig.MAPS_API_KEY)

    if (wiki == null || latlng == null) return null

    val existing = JsonLoader.loadMountainsFromAssets(context)
    val newId = (existing.maxByOrNull { it.id }?.id ?: 0) + 1
    val address = getAddressFromLatLng(latlng.first, latlng.second, BuildConfig.MAPS_API_KEY)

    return Mountain(
        id = newId,
        name = name,
        height = wiki.height,
        imagePath = wiki.imagePath,
        location = address,
        text = summary,
        latitude = latlng.first,
        longitude = latlng.second
    )
}

suspend fun getAddressFromLatLng(lat: Double, lng: Double, apiKey: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val url =
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&language=ko&key=$apiKey"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext "주소 없음"
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: return@withContext "주소 없음"

                for (i in 0 until results.length()) {
                    val result = results.getJSONObject(i)
                    val types = result.optJSONArray("types")
                    if (types != null && !"plus_code".equals(types.optString(0))) {
                        val fullAddress = result.getString("formatted_address")
                        val parts = fullAddress.split(" ")
                        if (parts.size > 1) {
                            return@withContext parts.dropLast(1).joinToString(" ")
                        } else {
                            return@withContext fullAddress
                        }
                    }
                }

                "주소 없음"
            }
        } catch (e: Exception) {
            "주소 변환 실패"
        }
    }
}

