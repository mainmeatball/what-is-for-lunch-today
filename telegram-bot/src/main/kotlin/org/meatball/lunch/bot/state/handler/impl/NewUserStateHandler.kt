package org.meatball.lunch.bot.state.handler.impl

import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.TelegramBotStateHandler
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.meatball.lunch.singletone.userService
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

class NewUserStateHandler : TelegramBotStateHandler {

    override fun handle(userId: String, msg: Message, date: LocalDate?): StateHandlerResponse {
        val userLunchName = userService.getLunchName(userId)
            ?: return StateHandlerResponse(
                "Для начала работы необходимо зарегистрироваться\\. Введите свои Фамилию Имя, как в гугл таблице",
                TelegramBotState.WAITING_FOR_REGISTER_DATA
            )

        // User is registered, probably wants to re-register
        return StateHandlerResponse(
            "Вы уже были зарегистрированы в системе под именем \"$userLunchName\"\\. Хотите зарегистрироваться заново?",
            TelegramBotState.WAITING_FOR_REGISTER_DATA
        )
    }
}