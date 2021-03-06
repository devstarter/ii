package org.ayfaar.app.vocabulary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fromJson
import org.ayfaar.app.utils.TermService
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File


class VocabularyDocTest {

    @Test
    fun release() {
        val termService = mock(TermService::class.java)
        val helper = spy(VocabularyUpperWordsHelper())
        helper.termService = termService
        doReturn(upperTermsJson.fromJson()).`when`(termService).allNames
        val service = VocabularyService()
        service.helper = helper
        service.getDoc("словарь.docx", File("src/main/resources/vocabulary-template.docx"))
    }

    @Test
    fun test() {
        val type = object : TypeToken<List<VocabularyTerm>>() {}.type
        val termsJson = File("src/test/resources/org/ayfaar/app/vocabulary/terms.json").readText()
        val data = Gson().fromJson<List<VocabularyTerm>>(termsJson, type)
//                .filter { it.name == "Поля Сознания" || it.name == "гуманация" || it.name == "психонация" || it.name == "эгрегор" }//.subList(0, 10)
//                .filter { it.name == "димидиомиттенсный" }//.subList(0, 10)

        val termService = mock(TermService::class.java)
        val helper = spy(VocabularyUpperWordsHelper())
        helper.termService = termService
        doReturn(upperTermsJson.fromJson()).`when`(termService).allNames

        val service = VocabularyService()
        service.helper = helper
        service.getDoc(data, "test.docx", File("src/main/resources/vocabulary-template.docx").inputStream())
    }

    /*fun getHelperMock() = mock(VocabularyUpperWordsHelper::class.java).also {
        `when`(it.check(any())).thenAnswer { it.arguments.first() }
    }*/
}



internal const val upperTermsJson = """["ААИИГЛА-МАА","ААИИ-СС-М","ААЙЛЛИИ","ААЛЛГГЛЛААММАА","ААЛЛММААЛЛАА","ААССФЛАСС","АВВААЛМИРА","АГЛАОРОТ","АИГЛЛИЛЛИАА","АИИЙВВФФ","АИИЛЛИИСС","АИЙ-ЙЯ","АИЙКВООФ","АИЙС-ССС","АИЙФРЫ","АЛЛ-УТТ-АРТ","АЛММА-ФФЛААТ","АММБО-ММ-БАЕ","АПП-РИВ-ЕРРА","АРГЛААМ","АРГЛЛААМУНИ","АРФФ-ОРСТ-МАА","АСТТМАЙ-РАА-А","БЛУ-У","ВВУ","ВИЛЛСИ-ЛЛАРИС","ВЛОООМООТ","ВЛУУСТОР","ВСЕ-Воля-ВСЕ-Разума","ВСЕ-","ВСЕ-Знание-ВСЕ-Информированность","ВСЕ-Любовь-ВСЕ-Мудрость","ВСЕ-Мобильность-ВСЕ-Присутственность","ВСЕ-Пустотность-ВСЕ-Вакуумность","ВСЕ-Сущность-ВСЕ-Проницаемость","ВСЕ-Устойчивость-ВСЕ-Стабильность","ВСЕ-","ВСЕ-","ВУОЛДТМ","ГАМАЛГОРРАА-А-","ГДОУККЛОФТ","ГЛЛАА-","ГЛЛАА-ГЛЛИИ","ГЛИИЛСС-ЛЛИ","ГЛООА","ГЛЯИЙГМИ-И","ГООЛГАМАА-А","ГООРР-ВВУ","ГРООМПФ ","ГРУУ-ЛЛФ","ГРЭИЙСЛИИСС","ДДААТТООНН","ДООРР-В-УУ-О","ДРУОТММ-","ДУУ-ЛЛИ","ЕСИП","ЗКК","ИЙЮ-УУ-ЙЮ","ИЛЛГРИИ-ТО-О","ИНГЛИМИЛИССА","ИНСТРА-ЗУ-РРИС","ЙЕИЙ","ЙЙЮ-ЛЛУ-АЙЙ","ЙЙЮУЛЛУЙГ","КААЙСИИ","КЛААЛЛАКСТМА","КЛООГМИИ","КЛООРТМ","КЛЛУАА","КОАРДДИИРФФ","КУЛЛКЛЛИ-РАА-А","ЛАА-","ЛААГГСС-ССНААЛ","ЛАНГМИИ","ЛГУУ-ВВУ","ЛЙЮЙЮ-ВВУ","ЛЛАА-ГРУАА","ЛЛААЙММА","ЛЛАА-СПАССМ-УСС","ЛЛААСС-Формы ","ЛЛААСС-ЛЛУУСС","ЛЛАИССММА-А","ЛЛИИЛЛ-ГГЛЛАА","ЛЛИИФФТ-ГГЛЛООССТ","ЛЛУОЛЛССМ","ЛУУД-ВВУ","ЛЛУУ-ВВУ","ЛООГЛИИ","МОУРСС-ФУЛЛГ","НАА-А","ННААССММ","НУУ-ВВУ","НУУ-ЛЛ-ВВУ","ОЛЛАКТ","ООЛЛМ-МАА","ООДДМИИ","ОРИС","ООДММ-ДДМОО","ОО-УУ","ОРЛААКТОР","ОСТРОККОЛФ","ПЛИИССМА","ППУУРПУ-ВВУ","ПРААЛЛУ-ЛАА","ПРИИ-ЛЛ-ОО-ЛККГ","ПРООЛФФ","ПРООФФ-РРУ","РАА-А","РАСС-АТ-СИСС","РРААЛЛСМ","РР-ВВУ","РРГЛУУ-ВВУ","РУЙЙЮУР-ТУУССТ","СБОАЛЛГСС","СБОАЛЛГСС-","СВОО-УУ","СЕЛЛКС-СКААН-ССТ","СКУУЛ-ЛСПЕР-РССК","СКУУЛЛМ","СЛОО-ГГОЛЛ","СЛУИ-СЛУУ","ССААЛМ-МАА","ССАЛЛАССТ-УУССТ","ССМИИЙГМИИ","ССМИИЙСМАА-А","ССНУУЙЛЛ-","ССОЛЛАС-МАА","ССУУЙЙ-НУУЛЛСС","ССС","СТООЛЛ-ВВУ","СТУУГМИИ","СУУ-ЛУУ","СФУУРММ-","СЦЫЫГЛ-ВВУ","ТАО-ВВУ","ТАССИЛЛУ-УРС-МАА","ТЛААССМА-А","ТООРГМИИ","ТОО-УУ","ТРООФТ-ЛЛИ","ТРУУРРГУРРДТ-НУУ","ТРУУФФОРРГ-ВУУ","ТУУРР-МООРР","УЛГРУУ","УУ-ВВУ","УУЙГ-УУЙЮ","УУЛЛ-ВВУ","УУ-О","УУСС-ИИСС","ФАТТМА-НАА-А","ФЛООВ-Р-УУ-О","Флооллгсс-РАА-А","ФЛУУ-ВВУ","ФЛУУ-ЛУУ","ФЛУУЛФ","ФФАОЛЛ-ФС-МАА","ФФЛААЙЙ-ТТААРР","ФФЛАТТМА","ФФЛУУФФ-ЛЛИИРР","ФФУ-И","ХВУО-ВВУ"]"""


