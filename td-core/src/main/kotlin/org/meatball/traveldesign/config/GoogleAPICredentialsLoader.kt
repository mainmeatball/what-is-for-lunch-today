package org.meatball.traveldesign.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.readText


private const val GOOGLE_API_CREDENTIALS_FILE_NAME = "googleApiCredentials.json"
private const val GOOGLE_API_CREDENTIALS_FILE_PATH = "./$GOOGLE_API_CREDENTIALS_FILE_NAME"

@Serializable
private data class GoogleApiKey(val apiKey: String)

fun getGoogleApiKey(): String {
    val file = Path(GOOGLE_API_CREDENTIALS_FILE_PATH)
    return Json.decodeFromString<GoogleApiKey>(file.readText()).apiKey
}