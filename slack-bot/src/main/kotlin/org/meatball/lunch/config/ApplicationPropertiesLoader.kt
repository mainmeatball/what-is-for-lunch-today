package org.meatball.lunch.config

import java.io.FileInputStream
import java.util.*

private const val APP_PROPERTIES_FILE_NAME = "properties"
private const val APP_PROPERTIES_FILE_PATH = "./$APP_PROPERTIES_FILE_NAME"
private const val APP_PROPERTIES_KEY = "port"

data class AppProperties(val port: Int)

fun getAppProperties(): AppProperties {
    val tokenProperties = Properties()
    val fileInputStream = FileInputStream(APP_PROPERTIES_FILE_PATH)

    // Loading token into properties
    tokenProperties.load(fileInputStream)

    fileInputStream.close()

    return AppProperties(
        port = tokenProperties.getProperty(APP_PROPERTIES_KEY).toInt()
    )
}