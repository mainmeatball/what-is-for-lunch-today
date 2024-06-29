package org.meatball.traveldesign.bot.token

import java.io.FileInputStream
import java.util.Properties

private const val TG_TOKEN_FILE_NAME = "telegram_bot_token"
private const val TG_TOKEN_FILE_PATH = "./$TG_TOKEN_FILE_NAME"
private const val TG_TOKEN_KEY = "tg_bot_token"

fun getTelegramBotToken(): String {
    val tokenProperties = Properties()
    val fileInputStream = FileInputStream(TG_TOKEN_FILE_PATH)

    // Loading token into properties
    tokenProperties.load(fileInputStream)

    fileInputStream.close()

    return tokenProperties.getProperty(TG_TOKEN_KEY)
}
