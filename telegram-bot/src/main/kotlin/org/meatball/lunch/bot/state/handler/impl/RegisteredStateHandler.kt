package org.meatball.lunch.bot.state.handler.impl

import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.TelegramBotStateHandler
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.meatball.lunch.singletone.lunchService
import org.meatball.lunch.singletone.userService
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

class RegisteredStateHandler : TelegramBotStateHandler {

    override fun handle(userId: Long, msg: Message, date: LocalDate?): StateHandlerResponse {
        val effectiveDate = date ?: LocalDate.now()
        val userLunchName = userService.getLunchName(userId)
            ?: return StateHandlerResponse(
                "Введите свои Фамилию Имя, как в гугл таблице",
                TelegramBotState.WAITING_FOR_REGISTER_DATA
            )
        val lunchData = lunchService.getLunchData(userLunchName, effectiveDate)
            ?: return StateHandlerResponse(
                "Пользователь с именем \"$userLunchName\" не найден в таблице",
                TelegramBotState.REGISTERED
            )
        return StateHandlerResponse(
            lunchData,
            TelegramBotState.REGISTERED
        )
    }
}