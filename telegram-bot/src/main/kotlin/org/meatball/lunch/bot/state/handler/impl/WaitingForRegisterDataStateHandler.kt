package org.meatball.lunch.bot.state.handler.impl

import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.TelegramBotStateHandler
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.meatball.lunch.singletone.userService
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

class WaitingForRegisterDataStateHandler : TelegramBotStateHandler {

    override fun handle(userId: Long, msg: Message, date: LocalDate?): StateHandlerResponse {
        val userName = msg.text
        if (userName.isBlank()) {
            return StateHandlerResponse("You entered blank username", TelegramBotState.WAITING_FOR_REGISTER_DATA)
        }

        val isValid = validate(userName)
        if (!isValid) {
            return StateHandlerResponse("You entered invalid username \"$userName\"", TelegramBotState.WAITING_FOR_REGISTER_DATA)
        }

        val isRegistered = userService.register(userId, msg.text)
        if (!isRegistered) {
            return StateHandlerResponse("Try again", TelegramBotState.WAITING_FOR_REGISTER_DATA)
        }

        return StateHandlerResponse("Пользователь \"$userName\" успешно зарегистрирован в системе", TelegramBotState.REGISTERED)
    }

    private fun validate(userName: String): Boolean {
        return true
    }
}