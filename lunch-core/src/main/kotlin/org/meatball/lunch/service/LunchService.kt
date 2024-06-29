package org.meatball.lunch.service

import org.meatball.lunch.food.FoodData
import java.time.LocalDate

class LunchService {

    fun getFoodList(userLunchName: String, date: LocalDate): List<FoodData>? {
        val lunchSheet = lunchSheetService.getLunchSheet(date)
        return lunchSheet.getFoodList(userLunchName, date)
    }

    fun getLunchData(userLunchName: String, date: LocalDate): String? {
        val foodList = getFoodList(userLunchName, date) ?: return null
        return toMarkdownText(foodList, date)
    }

    fun getAllUserLunchNames(date: LocalDate): Set<String> {
        val lunchSheet = lunchSheetService.getLunchSheet(date)
        return lunchSheet.getAllUserLunchNames()
    }

    private fun toMarkdownText(foodList: List<FoodData>, date: LocalDate): String {
        if (foodList.isEmpty()) {
            val today = LocalDate.now()
            val isRequestForToday = date == today
            val l10date = if (isRequestForToday) "сегодня" else date.toString().replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1")
            return "Вы ничего не заказывали на $l10date"
        }
        return foodList.joinToString("\n\n————————————————————\n\n") { it.toTextBlock() }
            .replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1")
    }

    private fun FoodData.toTextBlock(): String {
        val foodName = name.ifBlank { category }
        return """
            **$foodName**
            **Вес:** $weight г.
            
            **  К   |  Б  |  Ж  |  У**
            $calories | $protein | $fat | $carbs
        """.trimIndent()
    }

    private fun getCaloriesTable(foodData: FoodData): String {
        val separator = "-----------------"
        val formattingPattern = "|%s|%s|%s|%s|"
        return with(foodData) {
            val firstColLengthDiff = calories.length - 1
            """
            $separator
            ${"|%5s|%s|%s|%s|".format("К", "Б", "Ж", "У")}
            $separator
            ${"|%${5+firstColLengthDiff}s|%s|%s|%s|".format(calories, protein, fat, carbs)}
            $separator
            """
        }
    }

    private companion object {
        private val lunchSheetService = LunchSheetService()

        private val REGEX_MARKDOWN_V2_ESCAPE = Regex("([|{\\[\\]_~}+)(#>!=\\-.])")
    }
}