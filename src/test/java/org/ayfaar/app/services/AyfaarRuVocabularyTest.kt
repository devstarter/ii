package org.ayfaar.app.services

import com.mscharhag.oleaster.matcher.Matchers.expect
import org.ayfaar.app.utils.TermService
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*

class AyfaarRuVocabularyTest {

    fun getTermServiceMock() = mock(TermService::class.java)
    fun getTermProviderMock() = mock(TermService.TermProvider::class.java)

//    @Test
    fun findTermsTest() {
        val termService = getTermServiceMock()
        val termProvider = getTermProviderMock()

//        `when`(termService.termRoots).thenReturn(listOf(Pair("супервакансивн", termProvider)))
        `when`(termProvider.shortDescription).thenReturn(Optional.of("test"))

        AyfaarRuVocabulary(termService).findTerms("Итак, супервакансивное состояние, обусловленное").let {
            expect(it).toHaveSize(1)
            with(it.first()) {
                expect(word).toEqual("супервакансивное")
                expect(text).toEqual("test")
                expect(url).toBeNotNull()
            }
        }
    }

    @Test
    fun `абривиатуры`() {
        val termService = getTermServiceMock()
        val termProvider = getTermProviderMock()
        val mainTermProvider = getTermProviderMock()

        `when`(termService.get(Matchers.anyString())).thenAnswer {
            when(it.arguments.first()) {
                "ФПВ" -> Optional.of(termProvider)
                else -> Optional.empty()
            }
        }

        `when`(termProvider.mainOrThis).thenReturn(mainTermProvider)

        `when`(mainTermProvider.name).thenReturn("Фокус Пристального Внимания")
        `when`(mainTermProvider.shortDescription).thenReturn(Optional.of(""))

        AyfaarRuVocabulary(termService).findTerms("бла бла бла ФПВ, и т.п.").let {
            expect(it).toHaveSize(1)
            with(it.first()) {
                expect(word).toEqual("ФПВ")
                expect(term).toEqual("Фокус Пристального Внимания")
            }
        }
    }

    @Test
    fun `коды`() {
        val termService = getTermServiceMock()
        val termProvider = getTermProviderMock()

        `when`(termService.get(Matchers.anyString())).thenAnswer {
            when(it.arguments.first()) {
                "НУУ-ВВУ" -> Optional.of(termProvider)
                else -> Optional.empty()
            }
        }

        `when`(termProvider.mainOrThis).thenReturn(termProvider)
        `when`(termProvider.name).thenReturn("НУУ-ВВУ")
        `when`(termProvider.shortDescription).thenReturn(Optional.of(""))

        AyfaarRuVocabulary(termService).findTerms("фокусируемыми нами, НУУ-ВВУ-Формо-Типами – то есть вся").let {
            expect(it).toHaveSize(1)
            with(it.first()) {
                expect(word).toEqual("НУУ-ВВУ")
                expect(term).toEqual("НУУ-ВВУ")
            }
        }
    }
}