package org.meatball.lunch.bot

import org.meatball.lunch.bot.command.RegisterCommandHandler
import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.meatball.lunch.bot.state.handler.impl.NewUserStateHandler
import org.meatball.lunch.bot.state.handler.impl.RegisteredStateHandler
import org.meatball.lunch.bot.state.handler.impl.WaitingForRegisterDataStateHandler
import org.meatball.lunch.bot.token.getTelegramBotToken
import org.meatball.lunch.singletone.userService
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

    private val initDate = LocalDate.parse("2023-12-26")
    private val userStateMap = ConcurrentHashMap<Long, TelegramBotState>()

    init {
        val allUsers = userService.getAllUsers()
        allUsers.keys.forEach { userStateMap[it] = TelegramBotState.REGISTERED }
    }

    override fun getBotUsername(): String = "What is for lunch today"

    override fun onUpdateReceived(update: Update) {
        val msg = update.message
        val userId = msg.from.id

        if (msg.isRegister) {
            val response = registerCommandHandler.handle(userId, msg)

            // Handling state handler response
            handleStateResponse(userId, response)
            return
        }

        if (msg.isShowLunch) {
            val response = stateHandlerMap.getValue(TelegramBotState.REGISTERED).handle(userId, msg, initDate)

            // Handling state handler response
            handleStateResponse(userId, response)
            return
        }

        // Getting user state
        val userState = userStateMap.getOrDefault(userId, TelegramBotState.NEW)

        // Obtaining state handler
        val stateHandler = stateHandlerMap.getValue(userState)
        val response = stateHandler.handle(userId, msg, initDate)

        // Handling state handler response
        handleStateResponse(userId, response)
    }

    private fun handleStateResponse(userId: Long, response: StateHandlerResponse) {
        // Memorizing user state
        userStateMap[userId] = response.nextState

        // Sending text
        sendText(userId, response.response)
    }

    private fun sendText(userId: Long, text: String) {
        val sm = SendMessage.builder()
            .chatId(userId)
            .text(text)
            .replyMarkup(constructKeyboard())
            .parseMode(ParseMode.MARKDOWNV2)
            .build()

        try {
            execute(sm)
        } catch (ex: TelegramApiException) {
            println(ex.message)
            throw RuntimeException(ex)
        }
    }

    private fun constructKeyboard(): ReplyKeyboard {
        return ReplyKeyboardMarkup.builder()
            .keyboardRow(KeyboardRow(listOf(constructShowLunchButton())))
            .keyboardRow(KeyboardRow(listOf(constructReplyRegisterButton())))
            .build()
    }

    private fun constructShowLunchButton(): KeyboardButton {
        return KeyboardButton.builder()
            .text("Показать заказ")
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

    private companion object {
        // State handlers
        private val stateHandlerMap = mapOf(
            TelegramBotState.NEW to NewUserStateHandler(),
            TelegramBotState.WAITING_FOR_REGISTER_DATA to WaitingForRegisterDataStateHandler(),
            TelegramBotState.REGISTERED to RegisteredStateHandler()
        )
        private val registerCommandHandler = RegisterCommandHandler()

        private val Message.isRegister: Boolean
            get() = text == "Зарегистрироваться"

        private val Message.isShowLunch: Boolean
            get() = text == "Показать заказ"
    }
}