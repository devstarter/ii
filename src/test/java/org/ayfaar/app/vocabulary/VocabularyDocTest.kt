package org.ayfaar.app.vocabulary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import java.io.File


class VocabularyDocTest {

    @Test
    fun testRemote() {
        val service = VocabularyService()
        service.getDoc()
    }

    @Test
    fun test() {
        val type = object : TypeToken<List<VocabularyTerm>>() {}.type
        val termsJson = File("src/test/resources/org/ayfaar/app/vocabulary/terms.json").readText()
        val data = Gson().fromJson<List<VocabularyTerm>>(termsJson, type)
//                .filter { it.name == "Поля Сознания" || it.name == "гуманация" }//.subList(0, 10)

        val service = VocabularyService()
        service.getDoc(data/*, File("src/main/resources/template.docx")*/)
    }

}
