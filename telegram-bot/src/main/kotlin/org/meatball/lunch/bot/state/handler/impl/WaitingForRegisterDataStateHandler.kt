package org.meatball.lunch.bot.state.handler.impl

import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.TelegramBotStateHandler
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.meatball.lunch.singletone.userService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

class WaitingForRegisterDataStateHandler : TelegramBotStateHandler {

    override fun handle(userId: String, msg: Message, date: LocalDate?): StateHandlerResponse {
        val userLunchName = msg.text
        if (userLunchName.isBlank()) {
            return StateHandlerResponse("You entered blank username", TelegramBotState.WAITING_FOR_REGISTER_DATA)
        }

        val isValid = validate(userLunchName)
        if (!isValid) {
            return StateHandlerResponse("You entered invalid username \"$userLunchName\"", TelegramBotState.WAITING_FOR_REGISTER_DATA)
        }

        val isRegistered = userService.register(userId, msg.text)
        if (!isRegistered) {
            return StateHandlerResponse("Try again", TelegramBotState.WAITING_FOR_REGISTER_DATA)
        }

        logger.info("User (userId = ${msg.from.id}, tgUserName = ${msg.from.userName}, tgName = ${msg.from.firstName} ${msg.from.lastName}, userLunchName = \"$userLunchName\") has been successfully registered in the application")
        return StateHandlerResponse("Пользователь \"$userLunchName\" успешно зарегистрирован в системе", TelegramBotState.REGISTERED)
    }

    private fun validate(userName: String): Boolean {
        return true
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(WaitingForRegisterDataStateHandler::class.java)
    }
}