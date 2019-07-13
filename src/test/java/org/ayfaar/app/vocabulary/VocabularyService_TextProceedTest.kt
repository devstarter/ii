package org.ayfaar.app.vocabulary

import com.mscharhag.oleaster.matcher.Matchers.expect
import org.junit.Test

import org.junit.Assert.*

class VocabularyService_TextProceedTest {

    @Test
    fun кавычки() {
        "привет \"малыш\", ты прекрасно \"выглядишь\"".proceed().let {
            expect(it).toEqual("привет «малыш», ты прекрасно «выглядишь»")
        }
        " привет . ".proceed().apply {
            expect(this).toEqual("привет")
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
}
