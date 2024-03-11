package org.meatball.lunch.view

import com.slack.api.model.kotlin_extension.block.SectionBlockBuilder
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.view.blocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views.view
import org.meatball.lunch.food.FoodData
import org.meatball.lunch.service.LunchSheetService
import org.meatball.lunch.singletone.lunchService
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

fun homeTabView(userLunchName: String?, today: LocalDate, foodList: List<FoodData>? = null, msg: String? = null): View {
    val todayLunch = msg?.let { emptyList() } ?: foodList ?: lunchService.getFoodList(userLunchName!!, today) ?: emptyList()
    val tomorrow = today.plusDays(1L)
    val tomorrowLunch = if (tomorrow.isWorkingDay()) {
        msg?.let { emptyList() } ?: lunchService.getFoodList(userLunchName!!, tomorrow) ?: emptyList()
    } else {
        emptyList()
    }

    val view = view {
        it
            .type("home")
            .blocks {
                if (msg != null) {
                    section {
                        markdownText(msg)
                    }
                } else {
                    toMarkdown(todayLunch, "сегодня", today)
                    toMarkdown(tomorrowLunch, "завтра", tomorrow)
                }
            }
    }

    return view
}

private fun LocalDate.isWorkingDay() = dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

private fun LayoutBlockDsl.noFoodOrderedFor(day: String) {
    section {
        markdownText("*Вы ничего не заказывали на $day*")
    }
}

private fun SectionBlockBuilder.foodInfo(foodData: FoodData) {
    markdownText("""
        *${foodData.name}*
                                    
        *   К   |  Б  |  Ж  |  У*
        ${foodData.calories} | ${foodData.protein} | ${foodData.fat} | ${foodData.carbs}
        
        *Вес:* ${foodData.weight} г.
        """.trimIndent()
    )
}

private fun String.upFirstChar() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

private fun LayoutBlockDsl.toMarkdown(foodData: List<FoodData>, dayName: String, date: LocalDate) {
    header {
        text("${dayName.upFirstChar()} (${date.format(dateFormat)}, ${date.toDayOfWeekL10n()})")
    }
    when {
        foodData.isEmpty() -> noFoodOrderedFor(dayName)
        else -> {
            foodData.map {
                section {
                    foodInfo(it)
                }
                divider()
            }
        }
    }
}

private val dateFormat = DateTimeFormatter.ofPattern("dd.MM")

private fun LocalDate.toDayOfWeekL10n() = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "понедельник"
    DayOfWeek.TUESDAY -> "вторник"
    DayOfWeek.WEDNESDAY -> "среда"
    DayOfWeek.THURSDAY -> "четверг"
    DayOfWeek.FRIDAY -> "пятница"
    DayOfWeek.SATURDAY -> "суббота"
    DayOfWeek.SUNDAY -> "воскресенье"
    null -> "null"
}