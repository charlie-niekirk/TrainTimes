
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class Arr(
    @Json(name = "$")
    val x: X?
)