
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PushPortMessageItem(
    @Json(name = "TS")
    val tS: List<TS>?,
    @Json(name = "$")
    val x: XXXXXX?
)