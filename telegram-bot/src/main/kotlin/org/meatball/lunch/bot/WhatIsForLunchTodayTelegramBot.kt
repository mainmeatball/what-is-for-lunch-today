package org.meatball.lunch.bot

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update


private val TG_BOT_TOKEN = getTelegramBotToken()
class WhatIsForLunchTodayTelegramBot : TelegramLongPollingBot(TG_BOT_TOKEN) {

    override fun getBotUsername(): String = "What is for lunch today"

    override fun onUpdateReceived(update: Update) {

    }

    private companion object {
        private val registerActionHandler = null
        private val registerCommands = setOf("/start", "/register")
    }
}