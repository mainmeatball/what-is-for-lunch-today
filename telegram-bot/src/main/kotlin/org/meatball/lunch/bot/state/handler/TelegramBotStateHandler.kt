package org.meatball.lunch.bot.state.handler

import org.meatball.lunch.bot.state.handler.dto.StateHandlerResponse
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDate

interface TelegramBotStateHandler {

    fun handle(userId: String, msg: Message, date: LocalDate? = null): StateHandlerResponse
}