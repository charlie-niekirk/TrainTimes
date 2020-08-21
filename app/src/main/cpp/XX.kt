
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class XX(
    @Json(name = "delayed")
    val delayed: String?,
    @Json(name = "et")
    val et: String?,
    @Json(name = "etUnknown")
    val etUnknown: String?,
    @Json(name = "etmin")
    val etmin: String?,
    @Json(name = "src")
    val src: String?
)