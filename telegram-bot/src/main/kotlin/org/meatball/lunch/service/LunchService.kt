package org.meatball.lunch.service

import org.meatball.lunch.food.FoodData
import java.time.LocalDate

class LunchService {

    fun getLunchData(userLunchName: String, date: LocalDate): String? {
        val lunchSheet = lunchSheetService.getLunchSheet(date)
        val foodList = lunchSheet.getFoodList(userLunchName, date) ?: return null
        return toMarkdownText(foodList)
    }

    fun getAllUserLunchNames(date: LocalDate): Set<String> {
        val lunchSheet = lunchSheetService.getLunchSheet(date)
        return lunchSheet.getAllUserLunchNames()
    }

    private fun toMarkdownText(foodList: List<FoodData>): String {
        if (foodList.isEmpty()) {
            return "Вы на сегодня ничего не заказывали"
        }
        return foodList.joinToString("\n\n————————————————————\n\n") { it.toTextBlock() }
            .replace(REGEX_MARKDOWN_V2_ESCAPE, "\\\\$1")
    }

    private fun FoodData.toTextBlock(): String {
        return """
            **$name**
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