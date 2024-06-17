package org.meatball.lunch.sheet

import org.meatball.lunch.food.FoodData
import java.time.DayOfWeek
import java.time.LocalDate

typealias LunchTable = List<List<Any>>

private const val CATEGORY_NAME_COL = 0
private const val FOOD_NAME_COL = 1
private const val INGREDIENTS_COL = 2
private const val WEIGHT_COL = 3
private const val CALORIES_COL = 4
private const val PROTEIN_COL = 5
private const val FAT_COL = 6
private const val CARBS_COL = 7
private const val FOOD_NAME_ROW_MIN = 4
private const val FOOD_NAME_ROW_MAX = 325

private const val PEOPLE_NAME_ROW = 0
private const val PEOPLE_NAME_COL_MIN = 14

private const val PEOPLE_FOOD_ROW_MIN = FOOD_NAME_ROW_MIN

private const val WORKING_DAYS_N = 5

class LunchSheet(table: LunchTable) {

    private val rowToFoodName: Map<Int, FoodData> = table.getFoodColumn().asSequence().withIndex()
        .drop(FOOD_NAME_ROW_MIN)
        .take(FOOD_NAME_ROW_MAX)
        .filterNot { (row, foodName) -> foodName.isBlank() && table.getCategory(row).isBlank() }
        .associate {(row, foodName) ->
            row to FoodData(
                category = table.getCategory(row),
                name = foodName,
                ingredients = table.getIngredients(row),
                weight = table.getWeight(row),
                calories = table.getCalories(row),
                protein = table.getProtein(row),
                fat = table.getFat(row),
                carbs = table.getCarbs(row)
            )
        }

    private val personToStartCol = table.getPeopleRow().asSequence().withIndex()
        .drop(PEOPLE_NAME_COL_MIN)
        .filterNot { it.value.isBlank() }
        .associate { it.value to it.index }

    private val personToFoodList: Map<String, Map<DayOfWeek, List<FoodData>>> = personToStartCol.entries.associate { (personName, startColN) ->
        val map = mutableMapOf<DayOfWeek, List<FoodData>>()
        for (dayOfWeek in 1..WORKING_DAYS_N) {
            val foodRows = table.getPersonFoodRows(startColN, dayOfWeek - 1)
            val currentDayOfWeek = DayOfWeek.of(dayOfWeek)
            map[currentDayOfWeek] = foodRows.map { rowToFoodName[it] ?: emptyFood(it) }
        }
        personName to map.toMap()
    }

    fun getFoodList(userLunchName: String, date: LocalDate): List<FoodData>? {
        val dayOfWeek = date.dayOfWeek
        return getFoodList(userLunchName, dayOfWeek)
    }

    fun getFoodList(userLunchName: String, dayOfWeek: DayOfWeek): List<FoodData>? {
        val personFoodTable = personToFoodList[userLunchName] ?: return null
        return personFoodTable[dayOfWeek]
    }

    fun getAllUserLunchNames(): Set<String> {
        return personToFoodList.keys
    }

    private fun LunchTable.getPersonFoodRows(startColN: Int, offset: Int): List<Int> {
        val actualColN = startColN + offset
        val resultIndices = mutableListOf<Int>()
        getColumn(actualColN).asSequence().withIndex()
            .drop(PEOPLE_FOOD_ROW_MIN)
            .filterNot { it.value.isBlank() }
            .forEach { food ->
                val quantity = food.value.trim().toInt()
                repeat(quantity) { resultIndices += food.index }
            }
        return resultIndices
    }

    private companion object {
        private fun LunchTable.getFoodColumn(): List<String> = this.getColumn(FOOD_NAME_COL)

        private fun LunchTable.getPeopleRow(): List<String> = this.getRow(PEOPLE_NAME_ROW)

        private fun LunchTable.getCategory(rowN: Int): String = this.getCell(CATEGORY_NAME_COL, rowN)
        private fun LunchTable.getIngredients(rowN: Int): String = this.getCell(INGREDIENTS_COL, rowN)
        private fun LunchTable.getWeight(rowN: Int): String = this.getCell(WEIGHT_COL, rowN)
        private fun LunchTable.getCalories(rowN: Int): String = this.getCell(CALORIES_COL, rowN)
        private fun LunchTable.getProtein(rowN: Int): String = this.getCell(PROTEIN_COL, rowN)
        private fun LunchTable.getFat(rowN: Int): String = this.getCell(FAT_COL, rowN)
        private fun LunchTable.getCarbs(rowN: Int): String = this.getCell(CARBS_COL, rowN)

        private fun LunchTable.getColumn(colN: Int): List<String> = this.map {
            if (it.size <= colN) {
                return@map ""
            }
            it[colN] as String
        }

        private fun LunchTable.getRow(rowN: Int): List<String> = this[rowN].map {
            it as String
        }

        private fun LunchTable.getCell(colN: Int, rowN: Int): String = this[rowN][colN] as String

        private fun emptyFood(rowN: Int) = FoodData("", "Пустая строка $rowN", "", "", "", "", "", "")
    }
}

