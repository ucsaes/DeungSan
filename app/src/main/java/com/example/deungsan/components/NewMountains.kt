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

                val imageUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/${URLEncoder.encode(imageFileName, "UTF-8")}"

                WikidataMountainInfo(
                    height = height,
                    location = location,
                    imagePath = imageUrl
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

    return Mountain(
        id = newId,
        name = name,
        height = wiki.height,
        location = wiki.location,
        imagePath = wiki.imagePath,
        text = summary,
        latitude = latlng.first,
        longitude = latlng.second
    )
}