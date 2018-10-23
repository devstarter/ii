package org.ayfaar.app.services

import com.mscharhag.oleaster.matcher.Matchers.expect
import org.junit.Test

class WordSplitterTest {

    @Test
    fun textWordSplitter() {
        "Итак, супервакансивное состояние, обусловленное".splitToWords().let {
            expect(it).toHaveSize(4)
            expect(it[0]).toEqual("Итак")
            expect(it[1]).toEqual("супервакансивное")
            expect(it[2]).toEqual("состояние")
            expect(it[3]).toEqual("обусловленное")
        }
    }

    @Test
    fun `через тире`() {
        listOf("НУУ-ВВУ-Формо-Типами").codesWithDashes().let {
            expect(it).toHaveSize(2)
            expect(it.first()).toEqual("НУУ-ВВУ")
            expect(it.last()).toEqual("Формо-Типами")
        }

    }

    @Test
    fun test() {
        "бла НУУ-ВВУ-Формо-Типами бла".splitToWords().let {
            expect(it).toHaveSize(4)
            expect(it.contains("НУУ-ВВУ")).toBeTrue()
            expect(it.contains("Формо-Типами")).toBeTrue()
        }
    }
}