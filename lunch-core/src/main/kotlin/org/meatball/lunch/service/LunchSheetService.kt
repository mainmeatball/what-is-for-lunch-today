package org.meatball.lunch.service

import org.meatball.lunch.config.getLunchSpreadsheetId
import org.meatball.lunch.sheet.LunchSheet
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.Volatile

private val lunchSpreadsheetId = getLunchSpreadsheetId()

class LunchSheetService {

    @Volatile
    private var lunchSheet: LunchSheet? = null

    fun getLunchSheet(date: LocalDate): LunchSheet {
        if (lunchSheet != null) {
            return lunchSheet!!
        }
        val sheetName = calculateSheetName(date)
        return loadAndSetLunchSheet(sheetName)
    }

    @Synchronized
    private fun loadAndSetLunchSheet(sheetName: String): LunchSheet {
        if (lunchSheet != null) {
            return lunchSheet!!
        }
        lunchSheet = loadLunchSheet(lunchSpreadsheetId, sheetName)
        return lunchSheet!!
    }

    private fun calculateSheetName(date: LocalDate): String {
        val mondayDiff = date.dayOfWeek.value.toLong() - 1L
        val monday = date.minusDays(mondayDiff)
        val friday = monday.plusDays(DayOfWeek.FRIDAY.value.toLong() - DayOfWeek.MONDAY.value.toLong())
        val dateFormat = DateTimeFormatter.ofPattern("dd.MM")
        return "${monday.format(dateFormat)}-${friday.format(dateFormat)}"
    }
}