package org.meatball.traveldesign.bot

import org.meatball.traveldesign.bot.command.RegisterCommandHandler
import org.meatball.traveldesign.bot.state.TelegramBotState
import org.meatball.traveldesign.bot.state.handler.dto.StateHandlerResponse
import org.meatball.traveldesign.bot.state.handler.impl.NewUserStateHandler
import org.meatball.traveldesign.bot.state.handler.impl.RegisteredStateHandler
import org.meatball.traveldesign.bot.state.handler.impl.WaitingForRegisterDataStateHandler
import org.meatball.traveldesign.bot.token.getTelegramBotToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap


private val TG_BOT_TOKEN = getTelegramBotToken()

class TravelDesignTelegramBot : TelegramLongPollingBot(TG_BOT_TOKEN) {

    private val today = LocalDateTime.now()

    private val userStateMap = ConcurrentHashMap<String, TelegramBotState>()

    init {
        logger.info("Telegram bot is available. Init date = $today")
    }

    override fun getBotUsername(): String = "Travel Design Bot"

    override fun onUpdateReceived(update: Update) {
        if (update.hasCallbackQuery()) {
            val msg = update.callbackQuery.message
            val callbackData = update.callbackQuery.data
            val username = msg.chat.userName
            val chatId = msg.chatId

            val replyText = when (callbackData) {
                GENERAL_TRAVEL_INFO_KEY -> getGeneralTravelInfo(username)
                COMMUTING_INFO_KEY -> listOf("Добраться до туда проще простого!!! едем на поезде, потом на самолёте, потом на тачке".replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1"))
                ITINERARY_SCHEME_KEY -> listOf("Сначала идём туда, потом сюда, потом сюда!!! Кайфуем".replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1"))
                HOTEL_INFO_KEY -> listOf("Живём у бабули на хате и кушаем драники!!! Со сметаной".replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1"))
                else -> listOf("хз че вы тут тыкнули, я так не умею")
            }
            val iterator = replyText.listIterator()
            while (iterator.hasNext()) {
                val smBuilder = SendMessage.builder()
                    .chatId(chatId)
                    .text(iterator.next())
                    .parseMode(ParseMode.MARKDOWNV2)

                if (!iterator.hasNext()) {
                    smBuilder
                        .replyMarkup(constructInlineKeyboard())
                }
                val sm = smBuilder.build()

                try {
                    execute(sm)
                } catch (ex: TelegramApiException) {
                    println(ex.message)
                    throw RuntimeException(ex)
                }
            }
            return
        }

        val msg = update.message
        val chatId = msg.chatId
        val user = msg.from

        val smBuilder = SendMessage.builder()
            .chatId(chatId)
            .parseMode(ParseMode.MARKDOWNV2)

        if (user.isAdmin()) {
            smBuilder
                .text("Выберите ")
        }

        if (msg.from.userName !in tgUsernameWhiteList) {
            smBuilder
                .text("Я вас не знаю")

            val sm = smBuilder.build()

            try {
                execute(sm)
            } catch (ex: TelegramApiException) {
                println(ex.message)
                throw RuntimeException(ex)
            }
            return
        }

        smBuilder
            .text("Привет! Я помощник в вашем путешествии с Travel Design.\n".replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1") +
                "Выберите опцию из меню.".replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1"))
            .replyMarkup(constructInlineKeyboard())

        val sm = smBuilder.build()

        try {
            execute(sm)
        } catch (ex: TelegramApiException) {
            println(ex.message)
            throw RuntimeException(ex)
        }



//        if (msg.isRegister) {
//            val response = registerCommandHandler.handle(userId, msg)
//
//            // Handling state handler response
//            handleStateResponse(userId, response)
//            return
//        }
//
//        if (msg.isShowLunch) {
//            val showLunchDate = getShowLunchDate(msg)
//            val response = stateHandlerMap.getValue(TelegramBotState.REGISTERED).handle(userId, msg, showLunchDate)
//
//            // Handling state handler response
//            handleStateResponse(userId, response)
//            return
//        }
//
//        // Getting user state
//        val userState = userStateMap.getOrDefault(userId, TelegramBotState.NEW)
//
//        // Obtaining state handler
//        val stateHandler = stateHandlerMap.getValue(userState)
//        val response = stateHandler.handle(userId, msg, today)
//
//        // Handling state handler response
//        handleStateResponse(userId, response)
    }

    private fun getGeneralTravelInfo(username: String): List<String> {
        val res = mutableListOf<String>()
        for (i in 1..7) {
            val bufferedReader = File("$username/1_generalTravelInfo$i.md").bufferedReader()
            res += bufferedReader.use { it.readText() }.replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1")
        }
        return res
    }

    private fun handleStateResponse(userId: String, response: StateHandlerResponse) {
        // Memorizing user state
        userStateMap[userId] = response.nextState

        // Sending text
        sendText(userId, response)
    }

    private fun sendText(userId: String, response: StateHandlerResponse) {
        val smBuilder = SendMessage.builder()
            .chatId(userId)
            .text(response.text)
            .parseMode(ParseMode.MARKDOWNV2)
            .replyMarkup(constructInlineKeyboard())

        if (response.nextState == TelegramBotState.REGISTERED) {
            smBuilder
                .replyMarkup(constructInlineKeyboard())
        }

        val sm = smBuilder.build()

        try {
            execute(sm)
        } catch (ex: TelegramApiException) {
            println(ex.message)
            throw RuntimeException(ex)
        }
    }

    private fun constructInlineKeyboard(): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.builder()
            .keyboardRow(listOf(constructInlineButton(GENERAL_TRAVEL_INFO, GENERAL_TRAVEL_INFO_KEY)))
            .keyboardRow(listOf(constructInlineButton(COMMUTING_INFO, COMMUTING_INFO_KEY)))
            .keyboardRow(listOf(constructInlineButton(ITINERARY_SCHEME, ITINERARY_SCHEME_KEY)))
            .keyboardRow(listOf(constructInlineButton(HOTEL_INFO, HOTEL_INFO_KEY)))
            .build()
    }

    private fun constructReplyKeyboard(): ReplyKeyboard {
        return ReplyKeyboardMarkup.builder()
            .keyboardRow(KeyboardRow(listOf(constructReplyButton(GENERAL_TRAVEL_INFO))))
            .keyboardRow(KeyboardRow(listOf(constructReplyButton(COMMUTING_INFO))))
            .keyboardRow(KeyboardRow(listOf(constructReplyButton(ITINERARY_SCHEME))))
            .keyboardRow(KeyboardRow(listOf(constructReplyButton(HOTEL_INFO))))
            .build()
    }

    private fun constructReplyButton(text: String): KeyboardButton {
        return KeyboardButton.builder()
            .text(text)
            .build()
    }

    private fun constructInlineButton(text: String, callback: String): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text(text)
            .callbackData(callback)
            .build()
    }

    private fun constructReplyRegisterButton(): KeyboardButton {
        return KeyboardButton.builder()
            .text("Зарегистрироваться")
            .build()
    }

    private fun constructRegisterButton(): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text("Зарегистрироваться").callbackData("register")
            .build()
    }

    private fun User.isAdmin() = userName in admins

    private companion object {
        // State handlers
        private val stateHandlerMap = mapOf(
            TelegramBotState.NEW to NewUserStateHandler(),
            TelegramBotState.WAITING_FOR_REGISTER_DATA to WaitingForRegisterDataStateHandler(),
            TelegramBotState.REGISTERED to RegisteredStateHandler()
        )
        private val registerCommandHandler = RegisterCommandHandler()

        private const val GENERAL_TRAVEL_INFO = "Общая информация о направлении"
        private const val GENERAL_TRAVEL_INFO_KEY = "generalTravelInfo"

        private const val COMMUTING_INFO = "Информация о переезде"
        private const val COMMUTING_INFO_KEY = "commutingInfo"

        private const val ITINERARY_SCHEME = "Схема маршрута"
        private const val ITINERARY_SCHEME_KEY = "itineraryScheme"

        private const val HOTEL_INFO = "Локации проживания"
        private const val HOTEL_INFO_KEY = "hotelInfo"

        private val Message.isRegister: Boolean
            get() = text == "Зарегистрироваться" || (isCommand && text == "/register")

        private val Message.isShowLunch: Boolean
            get() = text == GENERAL_TRAVEL_INFO || text == COMMUTING_INFO

        private val tgUsernameWhiteList = setOf(
            "Rokurokubi",
            "raccoonsta",
            "stmeat",
            "rinochka_a7",
            "IiiuryS"
        )

        private val admins = setOf(
            "raccoonsta",
            "stmeat",
            "rinochka_a7",
        )

        private val REGEX_MARKDOWN_V2_ESCAPE = Regex("([|{\\[\\]_~}+)(#>!=\\-.])")

        private val logger: Logger = LoggerFactory.getLogger(TravelDesignTelegramBot::class.java)
    }
}