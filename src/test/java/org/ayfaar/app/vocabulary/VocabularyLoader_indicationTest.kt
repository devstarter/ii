package org.ayfaar.app.vocabulary

import com.mscharhag.oleaster.matcher.Matchers.expect
import org.junit.Assert.*
import org.junit.Test
import org.ayfaar.app.vocabulary.VocabularyIndicationType.*

class VocabularyLoader_indicationTest {
    @Test
    fun testIndications() {
        parseIndications("""курсив#в ииссиидиологическом понимании
жирным#по ииссиидиологии
подчёркивание#яллссгульды, ллимпсуссы, аккусписы, фикксиарды, ссумтассы
тест""").apply {
            expect(this).toHaveSize(4)

            expect(this!![0].type).toEqual(ITALIC)
            expect(this[0].text).toEqual("в ииссиидиологическом понимании")

            expect(this!![1].type).toEqual(BOLD)
            expect(this[1].text).toEqual("по ииссиидиологии")

            expect(this!![2].type).toEqual(UNDERSCORE)
            expect(this[2].text).toEqual("яллссгульды, ллимпсуссы, аккусписы, фикксиарды, ссумтассы")

            expect(this!![3].type).toEqual(ITALIC)
            expect(this[3].text).toEqual("тест")
        }
    }
}
