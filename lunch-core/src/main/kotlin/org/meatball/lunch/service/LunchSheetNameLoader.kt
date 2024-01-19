package org.meatball.lunch.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

private const val LUNCH_SHEET_NAME_FILE_NAME = "lunchSheetName.json"
private const val LUNCH_SHEET_NAME_FILE_PATH = "./$LUNCH_SHEET_NAME_FILE_NAME"

@Serializable
private data class LunchSheetName(val lunchSheetName: String)

fun getLunchSheetName(): String {
    val file = Path(LUNCH_SHEET_NAME_FILE_PATH)
    return Json.decodeFromString<LunchSheetName>(file.readText()).lunchSheetName
}

fun writeLunchSheetName(lunchSheetName: String) {
    val jsonString = Json.encodeToString(LunchSheetName(lunchSheetName))
    val file = Path(LUNCH_SHEET_NAME_FILE_PATH)
    file.writeText(jsonString)
}