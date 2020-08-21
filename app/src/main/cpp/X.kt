
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class X(
    @Json(name = "delayed")
    val delayed: String?,
    @Json(name = "et")
    val et: String?,
    @Json(name = "src")
    val src: String?
)