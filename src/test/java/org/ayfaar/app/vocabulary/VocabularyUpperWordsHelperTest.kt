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
}
