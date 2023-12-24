package org.meatball.lunch.bot.state.handler.dto

import org.meatball.lunch.bot.state.TelegramBotState

data class StateHandlerResponse(val response: String, val nextState: TelegramBotState)