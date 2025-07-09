import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.deungsan.R
import com.example.deungsan.data.model.Mountain
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds

@SuppressLint("MissingPermission")
@Composable
fun GoogleMapView(context: Context,
                  mountains: List<Mountain>,
                  favorites: Set<String>,
                  navController: NavController
) {
    val mapView = rememberMapViewWithLifecycle(context)

    AndroidView(factory = {

        mapView.apply {
            getMapAsync { googleMap ->
                // 전체 산 보이게
                if (mountains.isNotEmpty()) {
                    val builder = LatLngBounds.builder()
                    mountains.forEach { mountain ->
                        builder.include(LatLng(mountain.latitude, mountain.longitude))
                    }
                    val bounds = builder.build()
                    val padding = 200 // 화면 가장자리 여백 (픽셀)
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    googleMap.moveCamera(cameraUpdate)
                }

                for (mountain in mountains) {
                    googleMap.setOnInfoWindowClickListener { marker ->
                        val rawTitle = marker.title ?: return@setOnInfoWindowClickListener
                        val cleanName = rawTitle.removePrefix("🏔️ ").trim()
                        navController.navigate("mountain_detail/${Uri.encode(cleanName)}")
                    }


                    val position = LatLng(mountain.latitude, mountain.longitude)
                    val isFavorite = favorites.contains(mountain.name.toString())

                    val markerOptions = MarkerOptions()
                        .position(position)
                        .title("🏔️ ${mountain.name}")
                        .snippet(mountain.location)

                    if (isFavorite) {
                        // 하트 마커
                        markerOptions.icon(
                            resizeMarkerIcon(context, R.drawable.heart_marker, 80, 80)
                            // BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

                        )
                    } else {
                        // 일반 산 마커
                        markerOptions.icon(
                            resizeMarkerIcon(context, R.drawable.mountain_marker, 80, 80)                        )
                    }

                    googleMap.addMarker(markerOptions)

                }
            }

        }

    }, modifier = Modifier
        .fillMaxWidth()
        .height(500.dp))
}

fun resizeMarkerIcon(context: Context, resId: Int, width: Int, height: Int): BitmapDescriptor {
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    val resized = Bitmap.createScaledBitmap(bitmap, width, height, false)
    return BitmapDescriptorFactory.fromBitmap(resized)
}


@Composable
fun rememberMapViewWithLifecycle(context: Context): MapView {
    val mapView = remember {
        MapView(context).apply {
            id = View.generateViewId()
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()

        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapView.onDestroy()
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}
