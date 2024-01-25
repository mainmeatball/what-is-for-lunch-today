package org.meatball.lunch.config

import java.io.FileInputStream
import java.util.*

private const val APP_PROPERTIES_FILE_NAME = "properties"
private const val APP_PROPERTIES_FILE_PATH = "./$APP_PROPERTIES_FILE_NAME"
private const val PORT_KEY = "port"
private const val SLACK_EVENT_ENDPOINT_KEY = "slackEventsEndpoint"

data class AppProperties(val port: Int, val endpoint: String)

fun getAppProperties(): AppProperties {
    val tokenProperties = Properties()
    val fileInputStream = FileInputStream(APP_PROPERTIES_FILE_PATH)

    // Loading token into properties
    tokenProperties.load(fileInputStream)

    fileInputStream.close()

    return AppProperties(
        port = tokenProperties.getProperty(PORT_KEY).toInt(),
        endpoint = tokenProperties.getProperty(SLACK_EVENT_ENDPOINT_KEY).trim()
    )
}