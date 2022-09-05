package com.sdercolin.vlabeler.repository.update.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Asset(
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    val name: String,
)
