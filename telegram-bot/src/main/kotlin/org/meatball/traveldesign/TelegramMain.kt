package org.meatball.traveldesign

import org.meatball.traveldesign.bot.TravelDesignTelegramBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    val tgBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
    val tgBot = TravelDesignTelegramBot()
    tgBotsApi.registerBot(tgBot)
}