package org.meatball.lunch.bot.state.handler.dto

import org.meatball.lunch.bot.state.TelegramBotState

data class StateHandlerResponse(val text: String, val nextState: TelegramBotState)