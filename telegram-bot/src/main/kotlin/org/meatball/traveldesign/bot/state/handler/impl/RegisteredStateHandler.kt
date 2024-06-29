package org.meatball.traveldesign.bot.state.handler.impl

import org.meatball.traveldesign.bot.state.TelegramBotState
import org.meatball.traveldesign.bot.state.handler.TelegramBotStateHandler
import org.meatball.traveldesign.bot.state.handler.dto.StateHandlerResponse
//import org.meatball.traveldesign.singletone.lunchService
//import org.meatball.traveldesign.singletone.userService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

class RegisteredStateHandler : TelegramBotStateHandler {

    override fun handle(userId: String, msg: Message, date: LocalDate?): StateHandlerResponse {
        val effectiveDate = date ?: LocalDate.now()
//        val userLunchName = userService.getLunchName(userId)
//            ?: return StateHandlerResponse(
//                "Введите свои Фамилию Имя, как в гугл таблице",
//                TelegramBotState.WAITING_FOR_REGISTER_DATA
//            )
//        logger.info("User (userId = ${msg.from.id}, userLunchName = \"$userLunchName\") requested lunch info for $effectiveDate")
//        val lunchData = lunchService.getLunchData(userLunchName, effectiveDate)
//            ?: return StateHandlerResponse(
//                "Пользователь с именем \"$userLunchName\" не найден в таблице",
//                TelegramBotState.REGISTERED
//            )
        return StateHandlerResponse(
            "",
            TelegramBotState.REGISTERED
        )
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(RegisteredStateHandler::class.java)
    }
}