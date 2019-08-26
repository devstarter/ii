package org.ayfaar.app.vocabulary

import com.mscharhag.oleaster.matcher.Matchers.expect
import org.junit.Test

class VocabularyService_TextProceedTest {

    @Test
    fun кавычки() {
        "привет \"малыш\", ты прекрасно \"выглядишь\"".proceed().let {
            expect(it).toEqual("привет «малыш», ты прекрасно «выглядишь»")
        }

        " привет . ".proceed().apply {
            expect(this).toEqual("привет")
        }

        "“объекта”".proceed().apply {
            expect(this).toEqual("«объекта»")
        }

        "нами как “деградация «личности”".proceed().apply {
            expect(this).toEqual("нами как «деградация «личности»")
        }
    }

    @Test
    fun тире() {
        "привет - пока".proceed().let {
            expect(it).toEqual("привет — пока")
        }
        "привет – пока".proceed().let {
            expect(it).toEqual("привет — пока")
        }
        "слю-да".proceed().let {
            expect(it).toEqual("слю-да")
        }
    }

    @Test
    fun йё() {
        "й".proceed().let {
            expect(it).toEqual("й")
        }
        "ё".proceed().let {
            expect(it).toEqual("ё")
        }
    }

    @Test
    fun invalid_letters() {
        "наименьшей".proceed().apply {
            expect(this).toEqual("наименьшей")
        }
    }
    //
}
