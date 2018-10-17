package org.ayfaar.app.utils

import org.junit.Test
import kotlin.test.assertEquals

class TransliteratorTest {

    @Test
    fun forUrl() {
        assertEquals("muzhchina-zhenshina", Transliterator.forUrl("Мужчина + женщина"))
        assertEquals("subektivizm-vospriyatiya-mira", Transliterator.forUrl("Субъективизм восприятия мира"))
        assertEquals("mozg-dnk-zdorove-beremennost", Transliterator.forUrl("Мозг, ДНК, здоровье, беременность"))
    }
}