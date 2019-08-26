package org.ayfaar.app.vocabulary

import com.google.gson.Gson
import com.mscharhag.oleaster.matcher.Matchers.expect
import org.junit.Test
import java.io.File

class VocabularyLoaderTest {

    @Test
    fun test() {
        val service = VocabularyLoader()

        val terms = service.getData(getData())

        with(terms.find { it.name == "ааииааическое информационное состояние"}!!) {
            expect(source).toBeNull()
            expect(description).toEqual("состояние Фокусной Динамики (ФД) Высших Формо-Творцов Вселенского Коллективного Космического Разума (ВККР) Мироздания с наименьшей для них степенью субъективизма; свойственно глубинным Уровням консумматизации в ллууввумической бирвуляртности")
            expect(reductions).toBeEmpty()
            expect(zkk).toBeNull()
            expect(pleadsTerm).toBeFalse()
            expect(inII).toBeFalse()
            expect(conventional).toBeFalse()
        }

        with(terms.find { it.name == "«РЕЗОСКОНЦЕОННАЯ» Инволюционная Ветвь" }!!) {
            expect(source).toEqual("(по смыслу образовано от слов «резонанс» и «концентрация»)")
            expect(description).toEqual("одна из 24-х Ветвей одновременного Процесса Самопознания Высшего Разума")
            expect(zkk).toEqual("ВКРЦЫЫЫЙЙ-ККР")
            expect(reductions).toBeEmpty()
            expect(pleadsTerm).toBeFalse()
            expect(inII).toBeFalse()
            expect(conventional).toBeFalse()
        }

        with(terms.find { it.name == "айфааровский"}!!) {
            expect(source).toBeNull()
            expect(description).toEqual("относящийся к парадигме «Айфаар». Применимо для различных сфер жизни айфааровских сообществ.")
            expect(zkk).toBeNull()
            expect(reductions).toBeEmpty()
            expect(pleadsTerm).toBeFalse()
            expect(inII).toBeFalse()
            expect(conventional).toBeFalse()
            expect(inPhrases).toEqual("айфааровские принципы; айфааровские отношения; айфааровские песни; айфааровский образ жизни; айфааровская модель отношений; айфааровский самоанализ; айфааровские субботники".split("; ").map { VocabularySubTerm(it, null, true) })
        }

        /*with(terms.find { it.name == "Примордиум" }!!) {
            expect(derivatives).toHaveSize(1)
            expect(derivatives.first().name).toEqual("примордиумация")
            expect(derivatives.first().description).toMatch("^перенос.+меркавгнации$")
        }*/

        with(terms.find { it.name == "деплиативность" }!!) {
            expect(derivatives).toHaveSize(1)
            expect(derivatives.first().name).toEqual("УБРАТЬ: деплиативный")
            expect(derivatives.first().description).toMatch("УБРАТЬ: некачественный")
        }

        with(terms.find { it.name == "диффузгентный" }!!) {
            expect(inPhrases).toHaveSize(2)
            expect(inPhrases.toList()[1].name).toEqual("диффузгентная Форма")
            expect(inPhrases.toList()[0].name).toEqual("диффузгентные Вселенные")
        }

        with(terms.find { it.name == "амплификационный"}!!) {
            expect(source).toEqual("(от лат. amplification – усиление, расширение, улучшение)")
            expect(description).toEqual("эволюционный, эволюционирующий")
            expect(zkk).toBeNull()
            expect(reductions).toBeEmpty()
            expect(pleadsTerm).toBeFalse()
            expect(inII).toBeFalse()
            expect(conventional).toBeFalse()
            expect(inPhrases).toHaveSize(3)
            expect(inPhrases).toContain(VocabularySubTerm("амплификационная функция", "эволюционная Задача", true))
            expect(inPhrases.find { it.name == "амплификационный организационно-направляющий Импульс" }).toBeNotNull()
            expect(inPhrases.find { it.name == "амплификационный организационно-направляющий Импульс" }?.description).toMatch("^эгллеролифтивный.+в целом$")
            expect(inPhrases.find { it.name == "Амплификационные/Квалитационные Векторы и Ветви" }).toBeNotNull()
        }
        print(Gson().toJson(terms))
    }



    private fun getData() = Gson().fromJson(File("src/test/resources/org/ayfaar/app/vocabulary/raw-data.json").readText(),
        MutableList::class.java) as MutableList<MutableList<String>>

}


