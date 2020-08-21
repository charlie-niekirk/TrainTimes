
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class XXXXX(
    @Json(name = "rid")
    val rid: String?,
    @Json(name = "ssd")
    val ssd: String?,
    @Json(name = "uid")
    val uid: String?
)