package org.meatball.lunch.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.readText


private const val LUNCH_SHEET_CONFIGURATION_FILE_NAME = "lunchSheetConfiguration.json"
private const val LUNCH_SHEET_CONFIGURATION_FILE_PATH = "./$LUNCH_SHEET_CONFIGURATION_FILE_NAME"

@Serializable
private data class LunchSheetConfig(val spreadsheetId: String)

fun getLunchSpreadsheetId(): String {
    val file = Path(LUNCH_SHEET_CONFIGURATION_FILE_PATH)
    val lunchSheetConfig = Json.decodeFromString<LunchSheetConfig>(file.readText())
    return lunchSheetConfig.spreadsheetId
}

