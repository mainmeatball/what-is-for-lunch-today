package org.meatball.lunch

import org.meatball.lunch.bot.WhatIsForLunchTodayTelegramBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    val tgBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
    val tgBot = WhatIsForLunchTodayTelegramBot()
    tgBotsApi.registerBot(tgBot)
}