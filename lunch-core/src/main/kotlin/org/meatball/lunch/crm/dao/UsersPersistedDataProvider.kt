package org.meatball.lunch.crm.dao

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

private const val USERS_FILE_NAME = "users.json"
private const val USERS_FILE_PATH = "./$USERS_FILE_NAME"

fun readUsersFromDisk(): Map<String, String> {
    val file = Path(USERS_FILE_PATH)
    return Json.decodeFromString<Map<String, String>>(file.readText())
}

fun writeUsersToDisk(users: Map<String, String>) {
    val jsonString = Json.encodeToString(users)
    val file = Path(USERS_FILE_PATH)
    file.writeText(jsonString)
}