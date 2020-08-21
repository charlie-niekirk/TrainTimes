
import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class XXXX(
    @Json(name = "pta")
    val pta: String?,
    @Json(name = "ptd")
    val ptd: String?,
    @Json(name = "tpl")
    val tpl: String?,
    @Json(name = "wta")
    val wta: String?,
    @Json(name = "wtd")
    val wtd: String?,
    @Json(name = "wtp")
    val wtp: String?
)