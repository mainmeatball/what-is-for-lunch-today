package org.meatball.traveldesign.bot.state.handler.dto

import org.meatball.traveldesign.bot.state.TelegramBotState

data class StateHandlerResponse(val text: String, val nextState: TelegramBotState)