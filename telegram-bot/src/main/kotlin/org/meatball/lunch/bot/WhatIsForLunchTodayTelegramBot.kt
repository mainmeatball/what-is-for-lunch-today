package org.meatball.lunch.bot

import org.meatball.lunch.bot.command.RegisterCommandHandler
import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.meatball.lunch.bot.state.handler.impl.NewUserStateHandler
import org.meatball.lunch.bot.state.handler.impl.RegisteredStateHandler
import org.meatball.lunch.bot.state.handler.impl.WaitingForRegisterDataStateHandler
import org.meatball.lunch.bot.token.getTelegramBotToken
import org.meatball.lunch.singletone.userService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap


private val TG_BOT_TOKEN = getTelegramBotToken()

class WhatIsForLunchTodayTelegramBot : TelegramLongPollingBot(TG_BOT_TOKEN) {

    private val today = LocalDate.now()
    private val tomorrow = today.plusDays(1L)

    private val userStateMap = ConcurrentHashMap<String, TelegramBotState>()

    init {
        val allUsers = userService.getAllUsers()
        allUsers.keys.forEach { userStateMap[it] = TelegramBotState.REGISTERED }
        logger.info("Telegram bot is available. Init date = $today")
    }

    override fun getBotUsername(): String = "What is for lunch today"

    override fun onUpdateReceived(update: Update) {
        val msg = update.message
        val userId = msg.from.id.toString()

        if (msg.isRegister) {
            val response = registerCommandHandler.handle(userId, msg)

            // Handling state handler response
            handleStateResponse(userId, response)
            return
        }

        if (msg.isShowLunch) {
            val showLunchDate = getShowLunchDate(msg)
            val response = stateHandlerMap.getValue(TelegramBotState.REGISTERED).handle(userId, msg, showLunchDate)

            // Handling state handler response
            handleStateResponse(userId, response)
            return
        }

        // Getting user state
        val userState = userStateMap.getOrDefault(userId, TelegramBotState.NEW)

        // Obtaining state handler
        val stateHandler = stateHandlerMap.getValue(userState)
        val response = stateHandler.handle(userId, msg, today)

        // Handling state handler response
        handleStateResponse(userId, response)
    }

    private fun handleStateResponse(userId: String, response: StateHandlerResponse) {
        // Memorizing user state
        userStateMap[userId] = response.nextState

        // Sending text
        sendText(userId, response)
    }

    private fun sendText(userId: String, response: StateHandlerResponse) {
        val smBuilder = SendMessage.builder()
            .chatId(userId)
            .text(response.text)
            .parseMode(ParseMode.MARKDOWNV2)

        if (response.nextState == TelegramBotState.REGISTERED) {
            smBuilder
                .replyMarkup(constructKeyboard())
        }

        val sm = smBuilder.build()

        try {
            execute(sm)
        } catch (ex: TelegramApiException) {
            println(ex.message)
            throw RuntimeException(ex)
        }
    }

    private fun constructKeyboard(): ReplyKeyboard {
        return ReplyKeyboardMarkup.builder()
            .keyboardRow(KeyboardRow(listOf(constructShowLunchButton(SHOW_TODAY_LUNCH_TEXT))))
            .keyboardRow(KeyboardRow(listOf(constructShowLunchButton(SHOW_TOMORROW_LUNCH_TEXT))))
            .build()
    }

    private fun constructShowLunchButton(text: String): KeyboardButton {
        return KeyboardButton.builder()
            .text(text)
            .build()
    }

    private fun constructReplyRegisterButton(): KeyboardButton {
        return KeyboardButton.builder()
            .text("Зарегистрироваться")
            .build()
    }

    private fun constructRegisterButton(): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text("Зарегистрироваться").callbackData("register")
            .build()
    }

    private fun getShowLunchDate(msg: Message): LocalDate {
        return when {
            msg.text == SHOW_TOMORROW_LUNCH_TEXT -> tomorrow
            else -> today
        }
    }

    private companion object {
        // State handlers
        private val stateHandlerMap = mapOf(
            TelegramBotState.NEW to NewUserStateHandler(),
            TelegramBotState.WAITING_FOR_REGISTER_DATA to WaitingForRegisterDataStateHandler(),
            TelegramBotState.REGISTERED to RegisteredStateHandler()
        )
        private val registerCommandHandler = RegisterCommandHandler()

        private const val SHOW_TODAY_LUNCH_TEXT = "Показать заказ на сегодня"
        private const val SHOW_TOMORROW_LUNCH_TEXT = "Показать заказ на завтра"

        private val Message.isRegister: Boolean
            get() = text == "Зарегистрироваться" || (isCommand && text == "/register")

        private val Message.isShowLunch: Boolean
            get() = text == SHOW_TODAY_LUNCH_TEXT || text == SHOW_TOMORROW_LUNCH_TEXT

        private val logger: Logger = LoggerFactory.getLogger(WhatIsForLunchTodayTelegramBot::class.java)
    }
}