package com.cniekirk.traintimes.model
import com.squareup.moshi.Json

data class PushPortMessageItem(
    @Json(name = "TS")
    val tS: List<TS>?,
    @Json(name = "$")
    val messageAttrs: MessageAttrs?
)

data class TS(
    @Json(name = "LateReason")
    val lateReason: List<String>?,
    @Json(name = "Location")
    val location: List<Location>?,
    @Json(name = "$")
    val tsAttrs: TSAttrs?
)

data class MessageAttrs(
    @Json(name = "updateOrigin")
    val updateOrigin: String?
)

data class Location(
    @Json(name = "arr")
    val arr: List<Arr>?,
    @Json(name = "dep")
    val dep: List<Dep>?,
    @Json(name = "pass")
    val pass: List<Pass>?,
    @Json(name = "plat")
    val plat: List<Plat>?,
    @Json(name = "$")
    val stationAttrs: StationAttrs?
)

data class TSAttrs(
    @Json(name = "rid")
    val rid: String?,
    @Json(name = "ssd")
    val ssd: String?,
    @Json(name = "uid")
    val uid: String?
)

data class Arr(
    @Json(name = "$")
    val arrPassAttrs: ArrPassAttrs?
)

data class Dep(
    @Json(name = "$")
    var depAttrs: DepAttrs?
)

data class Pass(
    @Json(name = "$")
    val arrPassAttrs: ArrPassAttrs?
)

data class Plat(
    @Json(name = "_")
    val platform: String?,
    @Json(name = "$")
    val platAttrs: PlatAttrs?
)

data class PlatAttrs(
    @Json(name = "platsup")
    val platsup: String?,
    @Json(name = "cisPlatsup")
    val cisPlatsup: String?
)

data class StationAttrs(
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

data class ArrPassAttrs(
    @Json(name = "delayed")
    val delayed: String?,
    @Json(name = "et")
    val et: String?,
    @Json(name = "src")
    val src: String?
)

data class DepAttrs(
    @Json(name = "delayed")
    val delayed: String?,
    @Json(name = "et")
    val et: String?,
    @Json(name = "etUnknown")
    val etUnknown: String?,
    @Json(name = "etmin")
    val etmin: String?,
    @Json(name = "at")
    val at: String?,
    @Json(name = "src")
    val src: String?
)