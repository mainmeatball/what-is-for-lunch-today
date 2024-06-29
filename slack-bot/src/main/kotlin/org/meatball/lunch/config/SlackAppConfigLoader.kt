package org.meatball.lunch.config

import com.slack.api.bolt.AppConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.readText

private const val SLACK_API_CREDENTIALS_FILE_NAME = "slackApiCredentials.json"
private const val SLACK_API_CREDENTIALS_FILE_PATH = "./$SLACK_API_CREDENTIALS_FILE_NAME"

@Serializable
private data class SlackSecrets(val botToken: String, val signingSecret: String)

fun getSlackAppConfig(): AppConfig {
    val slackSecrets = getSlackSecrets()
    return AppConfig.builder()
        .singleTeamBotToken(slackSecrets.botToken)
        .signingSecret(slackSecrets.signingSecret)
        .build()
}

private fun getSlackSecrets(): SlackSecrets {
    val file = Path(SLACK_API_CREDENTIALS_FILE_PATH)
    return Json.decodeFromString<SlackSecrets>(file.readText())
}