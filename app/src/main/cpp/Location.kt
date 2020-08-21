
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class Location(
    @Json(name = "arr")
    val arr: List<Arr>?,
    @Json(name = "dep")
    val dep: List<Dep>?,
    @Json(name = "pass")
    val pass: List<Pas>?,
    @Json(name = "plat")
    val plat: List<Any>?,
    @Json(name = "$")
    val x: XXXX?
)