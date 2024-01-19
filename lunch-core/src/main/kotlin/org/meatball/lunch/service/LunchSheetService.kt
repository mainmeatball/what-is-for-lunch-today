package org.meatball.lunch.service

import org.meatball.lunch.config.getLunchSpreadsheetId
import org.meatball.lunch.sheet.LunchSheet
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import kotlin.concurrent.Volatile

private val lunchSpreadsheetId = getLunchSpreadsheetId()

class LunchSheetService {

    @Volatile
    private var lunchSheet: LunchSheet? = null

    fun getLunchSheet(date: LocalDate): LunchSheet {
        if (lunchSheet != null) {
            return lunchSheet!!
        }
        val writtenSheetName = getLunchSheetName()
        val (from, to) = parseSheetName(writtenSheetName)
        if (date in from..to) {
            return loadAndSetLunchSheet(writtenSheetName)
        }

        val sheetName = calculateSheetName(date)
        writeLunchSheetName(sheetName)
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

    private fun parseSheetName(lunchSheetName: String): Pair<LocalDate, LocalDate> {
        val dates = lunchSheetName.split('-')
        if (dates.size != 2) {
            throw IllegalStateException("Lunch sheet name = $lunchSheetName is illegal. Should be named in format dd.MM-dd.MM")
        }
        val from = LocalDate.parse(dates.first(), dateFormat)
        val to = LocalDate.parse(dates.last(), dateFormat)
        return from to to
    }

    private companion object {
        private val currentYear = LocalDate.now().year.toLong()
        private val dateFormat = DateTimeFormatterBuilder()
            .parseDefaulting(ChronoField.YEAR, currentYear)
            .appendPattern("dd.MM")
            .toFormatter()
    }
}