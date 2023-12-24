package org.meatball.lunch.bot.state.handler.impl

import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.TelegramBotStateHandler
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.meatball.lunch.singletone.lunchService
import org.meatball.lunch.singletone.userService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        logger.info("User (userId = ${msg.from.id}, userLunchName = \"$userLunchName\") requested lunch info for $effectiveDate")
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

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(RegisteredStateHandler::class.java)
    }
}