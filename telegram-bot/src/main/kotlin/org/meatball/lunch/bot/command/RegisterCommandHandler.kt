package org.meatball.lunch.bot.command

import org.meatball.lunch.bot.state.TelegramBotState
import org.meatball.lunch.bot.state.handler.TelegramBotStateHandler
import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

class RegisterCommandHandler : TelegramBotStateHandler {

    override fun handle(userId: Long, msg: Message, date: LocalDate?): StateHandlerResponse {
        return StateHandlerResponse(
            "Введите свои Фамилию Имя, как в гугл таблице",
            TelegramBotState.WAITING_FOR_REGISTER_DATA
        )
    }
}