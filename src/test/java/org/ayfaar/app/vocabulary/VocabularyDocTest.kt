package org.ayfaar.app.vocabulary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import org.mockito.Mockito.mock
import org.springframework.core.io.ResourceLoader
import java.io.File


class VocabularyDocTest {

    @Test
    fun testRemote() {
        val service = VocabularyService(mock(ResourceLoader::class.java))
        service.getDoc()
    }

    @Test
    fun test() {
        val type = object : TypeToken<List<VocabularyTerm>>() {}.type
        val termsJson = File("/Users/yuriylebid/projects/ii/src/test/resources/org/ayfaar/app/vocabulary/terms.json").readText()
        val data = Gson().fromJson<List<VocabularyTerm>>(termsJson, type).subList(0, 10)

        val service = VocabularyService(mock(ResourceLoader::class.java))
        service.getDoc(data, File("/Users/yuriylebid/projects/ii/src/main/resources/template.docx"))
    }

}
