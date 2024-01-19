package org.meatball.lunch.service

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class LunchSheetServiceTest {

    private val dateFormat = DateTimeFormatterBuilder()
        .parseDefaulting(ChronoField.YEAR, 2024L)
        .appendPattern("dd.MM")
        .toFormatter()

    @Test
    fun a() {
        val lunchSheetName = "23.01-31.12"
        val dates = lunchSheetName.split('-')
        if (dates.size != 2) {
            throw IllegalStateException("Lunch sheet name = $lunchSheetName is illegal. Should be named in format dd.MM-dd.MM")
        }
        val from = LocalDate.parse(dates.first(), dateFormat)
        val to = LocalDate.parse(dates.last(), dateFormat)
        println(from)
        println(to)
    }
}