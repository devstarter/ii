package org.ayfaar.app.vocabulary

import com.mscharhag.oleaster.matcher.Matchers.expect
import org.ayfaar.app.services.splitToWords
import org.docx4j.wml.BooleanDefaultTrue
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.docx4j.wml.Text
import org.junit.Test

import org.junit.Assert.*

class VocabularyService_identationTest {

    @Test
    fun withContent() {
        P().withContent("один два три", "один два три".splitToWords().map {  VocabularyIndication(it) }).apply {
            expect(this.content).toHaveSize(5)
            (this.content[0] as R).apply {
                expect(rPr.i).toBeInstanceOf(BooleanDefaultTrue::class.java)
                expect((content[0] as Text).value).toEqual("один")
            }
            (this.content[1] as R).apply {
                expect(rPr).toBeNull()
                expect((content[0] as Text).value).toEqual(" ")
            }
            (this.content[2] as R).apply {
                expect(rPr.i).toBeInstanceOf(BooleanDefaultTrue::class.java)
                expect((content[0] as Text).value).toEqual("два")
            }
        }

        P().withContent("один два три", "два".splitToWords().map {  VocabularyIndication(it) }).apply {
            expect(this.content).toHaveSize(3)
            (this.content[0] as R).apply {
                expect(rPr).toBeNull()
                expect((content[0] as Text).value).toEqual("один ")
            }
            (this.content[1] as R).apply {
                expect(rPr.i).toBeInstanceOf(BooleanDefaultTrue::class.java)
                expect((content[0] as Text).value).toEqual("два")
            }
            (this.content[2] as R).apply {
                expect(rPr).toBeNull()
                expect((content[0] as Text).value).toEqual(" три")
            }
        }
    }
}
