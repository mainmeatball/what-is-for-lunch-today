package org.meatball.lunch.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpExecuteInterceptor
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import org.meatball.lunch.config.getGoogleApiKey
import org.meatball.lunch.sheet.LunchSheet

private const val GOOGLE_API_APP_NAME = "What Is For Lunch Today"
private val GOOGLE_API_KEY = getGoogleApiKey()

private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
private val httpRequestInitializer = HttpRequestInitializer { request ->
    request.interceptor = HttpExecuteInterceptor { intercepted ->
        intercepted.url.set("key", GOOGLE_API_KEY)
    }
}
private const val MAX_ROW = 100

fun loadLunchSheet(spreadsheetId: String, sheetName: String): LunchSheet {
    val sheets = Sheets.Builder(httpTransport, JSON_FACTORY, httpRequestInitializer)
        .setApplicationName(GOOGLE_API_APP_NAME)
        .build()

    val response = sheets.spreadsheets().values()
        .get(spreadsheetId, "$sheetName!1:$MAX_ROW")
        .execute()
    val rows = response.getValues()

    if (rows.isNullOrEmpty()) {
        error("No data found for sheet $spreadsheetId")
    }

    return LunchSheet(rows)
}

fun loadLunchSheetNames(spreadsheetId: String): List<String> {
    val sheets = Sheets.Builder(httpTransport, JSON_FACTORY, httpRequestInitializer)
        .setApplicationName(GOOGLE_API_APP_NAME)
        .build()

    val response = sheets.spreadsheets()
        .get(spreadsheetId)
        .execute()

    return response.sheets.map { it.properties.title }
}
