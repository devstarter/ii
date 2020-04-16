package org.ayfaar.app.vocabulary

import com.mscharhag.oleaster.matcher.Matchers.expect
import org.ayfaar.app.utils.TermService
import org.ayfaar.app.utils.TermServiceImpl
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class VocabularyUpperWordsHelperTest {

    @Test
    fun test_makeCodesUpper() {
        val upperWords = listOf(
                "ААИИГЛА-МАА",
                "АИГЛЛИЛЛИАА"
        )

        makeCodesUpper("Ааиигла-МАА-Сущностей Аигллиллиаа-Ииссииди", upperWords).apply {
            expect(this).toEqual("ААИИГЛА-МАА-Сущностей АИГЛЛИЛЛИАА-Ииссииди")
        }
    }

    @Test
    fun test_loadUpperWords() {
        loadUpperWords(listOf("СЛООР-ССС-ЛЛААС", "ССМАЙК-АЙКК-Поле", "ССС", "квант")).also {
            expect(it).toHaveSize(3)
            expect(it[0]).toEqual("СЛООР-ССС-ЛЛААС")
            expect(it[1]).toEqual("ССМАЙК-АЙКК")
            expect(it[2]).toEqual("ССС")
        }
    }
}
