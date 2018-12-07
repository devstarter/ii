package org.ayfaar.app.sync

import com.google.gson.Gson
import mu.KotlinLogging
import org.apache.commons.net.ftp.FTPClient
import org.ayfaar.app.utils.GoogleService
import org.ayfaar.app.utils.Transliterator
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.util.regex.Pattern


@Service
@EnableScheduling
class AyfaarRuNavigatorUpdater {
    constructor() {}
    internal constructor(ftpLogin: String, ftpPassword: String) {
        this.ftpLogin = ftpLogin
        this.ftpPassword = ftpPassword
    }

    private val log = KotlinLogging.logger {  }
    @Value("ftp.ayfaar.ru.login") private lateinit var ftpLogin: String
    @Value("ftp.ayfaar.ru.password") private lateinit var ftpPassword: String
    val map = listOf(
//            Pair("Тест", "1mvEvkeYkaJrt_skpY4oKr3CiPPnbH2yJp0Ri1qR0lPk")
            Pair("Устройство мироздания", "1KBA_9B9bySStHtA_7B1uR83uOT-EnKz2-pH3bREVd1Y"),
            Pair("Сознание и биология человека", "1LYaTF2Tu0qfboAXtbserFK40C1JFr3tfikJHD078Aq0"),
            Pair("Взаимоотношения людей", "1pbLXSJ82xdvhU4UuEE4juivEUpS14dp-PE1tMjEgFVU"),
            Pair("Связь человека с мирозданием", "1D_r68VGcIucDFstNvhTDJp_Ir8Aruo2Cj7BRT31s9no"),
            Pair("Будущее человечества", "18IETRXsA-lWxAuNxzuuWItlE5vEnBUBS56_SYfyj2N0")
    )

    @Scheduled(cron = "0 0 * * * *") // every hour
    fun sync() {
        log.info("Synchronization started")

        val dsJsonMap = HashMap<String, Any>()
        val topicsJsonMap = HashMap<String, Any>()

        val ds = map.map { Pair(it.first, loadGoogleData(it.second)) }
        ds.forEach { (topicName, subtopics) ->
            dsJsonMap[topicName] = subtopics.map { (title, blocks) -> mapOf(
                    Pair("name", title),
                    Pair("alias", Transliterator.forUrl(title)),
                    Pair("blocks", blocks)
            ) }
            topicsJsonMap[topicName] = subtopics.map { (title, _) -> mapOf(
                    Pair("name", title),
                    Pair("alias", Transliterator.forUrl(title))
            ) }
        }
        val ftpHost = "ftp.ayfaar.ru"

        uploadData("navigator-datasource.json", Gson().toJson(dsJsonMap)!!, ftpHost, ftpLogin, ftpPassword)
        log.info("navigator-datasource.json uploaded")

        uploadData("navigator-topics.json", Gson().toJson(topicsJsonMap)!!, ftpHost, ftpLogin, ftpPassword)
        log.info("navigator-topics.json uploaded")

        log.info("Synchronization finished")
    }

    private fun loadGoogleData(id: String) = GoogleService.getSheetsService()
                .spreadsheets()
                .get(id)
                .execute()
                .sheets
                .map { Pair(it.properties.title, loadData(id, it.properties.title)) }
}

/////////////////////////////////// UTILS /////////////////////////////////

fun uploadData(remoteFilename: String, json: String, ftpHost: String, ftpLogin: String, ftpPassword: String) {
    val file = File.createTempFile("navigator", ".json")
    file.writeText(json)

    val client = FTPClient()

    try {
        client.connect(ftpHost)
        client.login(ftpLogin, ftpPassword)
        client.enterLocalPassiveMode()
        client.storeFile("/domains/ayfaar.ru/public_html/tpl/static/izuchenie_ii/$remoteFilename", file.inputStream())
        client.logout()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            client.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

private fun loadData(id: String, sheetId: String): Blocks {
    val log = KotlinLogging.logger {  }
//    log.info { "loading sheet $id#$sheetId" }
    return GoogleService.getSheetsService()
            .spreadsheets()
            .values()
            .get(id, "$sheetId!A:Z")
            .execute()
            .getValues()
            .parseData()
            .also {
//                log.debug { "$id#$sheetId loaded: ${it.summary()}" }
            }
}




fun List<List<Any>>.parseData(): Blocks {
    val iterator = CoolIterator(this)
    var newbieBlock: Block? = null
    var advancedBlock: Block? = null
    while (iterator.hasNext()) {
        val next = iterator.next()
        when {
            next.firstIs("ПОДБОРКА НАЧАЛЬНОЙ СЛОЖНОСТИ") -> {
                newbieBlock = `разбор материала начальной сложности`(iterator.clone())
                advancedBlock = `разбор материала начальной сложности`(iterator.clone(), 5)
            }

        }
    }
    return Blocks(newbieBlock, advancedBlock)
}

fun `разбор материала начальной сложности`(iterator: CoolIterator, startIndex: Int = 0): Block {
    var articles: Articles? = null
    var videos: List<TitleUrlPair>? = null
    var audios: List<TitleUrlPair>? = null
    while (iterator.hasNext()) {
        val next = iterator.next()
        when {
            next.someIs(startIndex, "СТАТЬИ") -> articles = `разбор статей`(iterator, startIndex)
            next.someIs(startIndex, "ВИДЕО (цветной блок)") -> {
                videos = `разбор блока названий и ссылок`(iterator, startIndex, startIndex + 2).map { it.copy(id = extractVideoIdFromUrl(it.url)) }
                iterator.stepBack()
            }
            next.someIs(startIndex, "АУДИО (белый блок)") -> audios = `разбор блока названий и ссылок`(iterator, startIndex, startIndex + 2).mapAudioUrls()
        }
    }
    return Block(articles, videos, audios)
}

private fun Collection<TitleUrlPair>.mapAudioUrls() = this.map {
    if (it.url.contains("ii.ayfaar.org/r/", true)) {
        val code = it.url.split("ii.ayfaar.org/r/")[1]
        it.copy(url = "http://ii.ayfaar.org/api/record/$code/download/$code")
    } else it
}


fun `разбор статей`(iterator: CoolIterator, startIndex: Int): Articles {
    val articles = Articles()
    while (iterator.hasNext()) {
        val next = iterator.next()
        when {
            next.firstIs("Ориса") -> {
                articles.oris = `разбор блока названий и ссылок с темами`(iterator.clone(), startIndex, startIndex, startIndex + 1)
                articles.others = `разбор блока названий и ссылок с темами`(iterator.clone(), startIndex, startIndex + 2, startIndex + 3)
            }
        }
        if (next.firstIs("подборка продвинутой сложности (ссылка на другую подборку)"))
            break
    }
    return articles
}


fun `разбор блока названий и ссылок с темами`(iterator: CoolIterator, topicIndex: Int, titleIndex: Int, urlIndex: Int): List<TopicArticles> {
    val topicArticles = ArrayList<TopicArticles>()
    val articles = ArrayList<TitleUrlPair>()
    var currentTopic = ""

    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next.firstNot("подборка продвинутой сложности (ссылка на другую подборку)")) {
            if (next.size > urlIndex) {
                if (next[urlIndex].isBlank()) {
                    if (articles.isNotEmpty()) {
                        topicArticles.add(TopicArticles(ArrayList(articles), currentTopic))
                        articles.clear()
                    }
                    currentTopic = next[topicIndex]
                } else {
                    articles.add(TitleUrlPair(next[titleIndex], next[urlIndex]))
                }
            }
        } else {
            break
        }
    }
    if (articles.isNotEmpty()) {
        topicArticles.add(TopicArticles(ArrayList(articles), currentTopic))
    }
    return topicArticles
}

fun `разбор блока названий и ссылок`(iterator: CoolIterator, titleIndex: Int, urlIndex: Int): ArrayList<TitleUrlPair> {
    val articles = ArrayList<TitleUrlPair>()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next.firstNot("подборка продвинутой сложности (ссылка на другую подборку)")
                && next.size > urlIndex
                && next[urlIndex].isNotBlank()
                && next[titleIndex].isNotBlank()) {
            articles.add(TitleUrlPair(next[titleIndex], next[urlIndex]))
        } else {
            break
        }
    }
    return articles
}

val youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/"
val videoIdRegex = arrayOf("\\?vi?=([A-Za-z0-9_\\-]*)", "watch\\?.*v=([A-Za-z0-9_\\-]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9_\\-]*)")

fun extractVideoIdFromUrl(url: String): String? {
    val youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url)

    for (regex in videoIdRegex) {
        val compiledPattern = Pattern.compile(regex)
        val matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain)

        if (matcher.find()) {
            return matcher.group(1)
        }
    }

    return null
}

private fun youTubeLinkWithoutProtocolAndDomain(url: String): String {
    val compiledPattern = Pattern.compile(youTubeUrlRegEx)
    val matcher = compiledPattern.matcher(url)

    return if (matcher.find()) {
        url.replace(matcher.group(), "")
    } else url
}

private fun List<String>.someIs(index: Int, str: String) = if (this.size > index) this[index].trim() == str else false
private fun List<String>.firstIs(str: String) = this.firstOrNull()?.trim() == str
private fun List<String>.firstNot(str: String) = this.firstOrNull()?.trim() != str

data class TitleUrlPair(val title: String, val url: String, val id: String? = null)


class CoolIterator(private val list: List<List<Any>>) {
    private var iterator: ListIterator<List<Any>> = list.listIterator()
    private var _current: List<String>? = null

    fun hasNext() = iterator.hasNext()
    fun next(): List<String> {
        return iterator.next()
                .map { it.toString() }
                .also { this._current = it }
    }

    fun clone(): CoolIterator {
        return CoolIterator(list, list.listIterator(currentIndex))
    }

    fun stepBack() {
        this._current = iterator.previous() as List<String>
    }

    constructor(list: List<List<Any>>, iterator: ListIterator<List<Any>>) : this(list) {
        this.iterator = iterator
    }

    val current: List<String>?
        get() = _current

    val currentIndex: Int
        get() = iterator.previousIndex() + 1

}

data class Articles(var oris: List<TopicArticles>? = null, var others: List<TopicArticles>? = null)

data class Block(val articles: Articles?, val videos: List<TitleUrlPair>?, val audios: List<TitleUrlPair>?) {
    fun summary(): String {
        return "Oris articles: ${articles?.oris?.size}, " +
                "others articles ${articles?.others?.size}, " +
                "videos: ${videos?.size}, " +
                "audios: ${audios?.size}"
    }
}

data class TopicArticles(val articles: List<TitleUrlPair>, val topic: String = "")

data class Blocks(val newbie: Block?, val advanced: Block?) {
    fun summary(): String {
        return "newbie: ${newbie?.summary()}, advanced: ${advanced?.summary()}"
    }
}
