package org.meatball.traveldesign.bot.command

import org.meatball.traveldesign.bot.state.TelegramBotState
import org.meatball.traveldesign.bot.state.handler.TelegramBotStateHandler
import org.meatball.traveldesign.bot.state.handler.dto.StateHandlerResponse
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

class RegisterCommandHandler : TelegramBotStateHandler {

    override fun handle(userId: String, msg: Message, date: LocalDate?): StateHandlerResponse {
        return StateHandlerResponse(
            "Введите свои Фамилию Имя, как в гугл таблице",
            TelegramBotState.WAITING_FOR_REGISTER_DATA
        )
    }
}