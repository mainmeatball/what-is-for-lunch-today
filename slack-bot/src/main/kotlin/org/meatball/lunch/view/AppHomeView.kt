package org.meatball.lunch.view

import com.slack.api.model.kotlin_extension.view.blocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views.view
import org.meatball.lunch.food.FoodData
import org.meatball.lunch.singletone.lunchService
import java.time.LocalDate

fun homeTabView(userLunchName: String?, today: LocalDate, foodList: List<FoodData>? = null, msg: String? = null): View {
    val todayLunch = msg?.let { emptyList() } ?: foodList ?: lunchService.getFoodList(userLunchName!!, today) ?: emptyList()

    val view = view {
        it
            .type("home")
            .blocks {
                if (msg != null) {
                    section {
                        markdownText(msg)
                    }
                } else {
                    when {
                        todayLunch.isEmpty() -> {
                            header {
                                text("Сегодня")
                            }
                            section {
                                markdownText("*Вы на сегодня ничего не заказывали*")
                            }
                        }
                        else -> {
                            header {
                                text("Сегодня")
                            }
                            todayLunch.map {
                                section {
                                    markdownText("""
                                    *${it.name}*
                                    
                                    *   К   |  Б  |  Ж  |  У*
                                    ${it.calories} | ${it.protein} | ${it.fat} | ${it.carbs}
                                    
                                    *Вес:* ${it.weight} г.
                                """.trimIndent())
                                }
                                divider()
                            }
                        }
                    }
                }
            }
    }

    return view
}