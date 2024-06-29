package org.meatball.lunch.app

import com.slack.api.bolt.App
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.model.event.AppHomeOpenedEvent
import org.meatball.lunch.config.getAppProperties
import org.meatball.lunch.config.getSlackAppConfig
import org.meatball.lunch.food.FoodData
import org.meatball.lunch.singletone.lunchService
import org.meatball.lunch.singletone.userService
import org.meatball.lunch.view.homeTabView
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class SlackApplication {

    private val appProperties = getAppProperties()
    private val slackAppConfig = getSlackAppConfig()
    private val slackApp = App(slackAppConfig)

    private var today = LocalDate.now()

    private val registeredUserIds = ConcurrentHashMap.newKeySet<String>()

    init {
        val allUsers = userService.getAllUsers()
        registeredUserIds += allUsers.keys
        logger.info("Slack application is available. Init date = $today")
    }

    fun run() {
        registeredUserIds.forEach { userId ->
            publishHomeTabView(userId)
        }

        slackApp.event(AppHomeOpenedEvent::class.java) { e, ctx ->
            updateDateIfChanged()

            val userId = e.event.user

            if (userId !in registeredUserIds) {
                publishHomeTabView(userId, text = "Сначала необходимо зарегистрироваться")
                return@event ctx.ack()
            }

            val userLunchName = userService.getLunchName(userId)
                ?: throw IllegalStateException("User (id=$userId) is registered, but anyway not found")

            val foodList = lunchService.getFoodList(userLunchName, today)

            if (foodList == null) {
                publishHomeTabView(userId, text = "Пользователь $userLunchName не был найден в таблице")
                return@event ctx.ack()
            }

            publishHomeTabView(userId, foodList)

            ctx.logger.info("${LocalDateTime.now()} - app home was opened - (userId=\"${userId}\", userLunchName=\"${userLunchName}\") requested lunch info for $today")

            return@event ctx.ack()
        }

        slackApp.command("/register") { e, ctx ->
            val lunchSheetName = e.payload.text
            val slackUserId = ctx.requestUserId
            userService.register(slackUserId, lunchSheetName)
            ctx.respond { it.text("Пользователь $lunchSheetName успешно зарегистрирован в системе") }
            logger.info("${LocalDateTime.now()} - user $lunchSheetName has registered. UserId = $slackUserId")
            registeredUserIds += slackUserId
            ctx.ack()
        }

        val server = SlackAppServer(slackApp, appProperties.endpoint, appProperties.port)
        server.start()
    }

    private fun publishHomeTabView(userId: String, foodList: List<FoodData>? = null, text: String? = null) {
        val userLunchName = userService.getLunchName(userId)
        slackApp.client.viewsPublish {
            val view = homeTabView(userLunchName, userId, today, foodList, text)
            it.token(slackAppConfig.singleTeamBotToken)
                .userId(userId)
                .view(view)
                .hash(view.hash)
        }
    }

    private fun updateDateIfChanged() {
        val now = LocalDate.now()
        if (today != now) {
            today = now
        }
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(SlackApplication::class.java)
    }
}