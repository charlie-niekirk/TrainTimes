
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class TS(
    @Json(name = "LateReason")
    val lateReason: List<String>?,
    @Json(name = "Location")
    val location: List<Location>?,
    @Json(name = "$")
    val x: XXXXX?
)