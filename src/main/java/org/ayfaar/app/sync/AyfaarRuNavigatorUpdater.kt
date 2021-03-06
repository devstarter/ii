package org.ayfaar.app.sync

import com.google.gson.Gson
import mu.KotlinLogging
import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.FTPClient
import org.ayfaar.app.utils.AyfaarRuFileTransfer
import org.ayfaar.app.utils.GoogleService
import org.ayfaar.app.utils.Transliterator
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.util.regex.Pattern
import javax.inject.Inject


@Service
@EnableScheduling
class AyfaarRuNavigatorUpdater {
    private val log = KotlinLogging.logger {  }

    constructor()
    constructor(fileTransfer: AyfaarRuFileTransfer) {
        this.fileTransfer = fileTransfer
    }

    @Inject lateinit var fileTransfer: AyfaarRuFileTransfer

    val map = listOf(
//            Pair("Тест", "11YKrbqZdAEZ-oRiZ9OPJZrMKhYypRqph0hbY6Gqeq-I")
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

        fileTransfer.upload("tpl/static/izuchenie_ii/navigator-datasource.json", Gson().toJson(dsJsonMap)!!)
        log.info("navigator-datasource.json uploaded")

        fileTransfer.upload("tpl/static/izuchenie_ii/navigator-topics.json", Gson().toJson(topicsJsonMap)!!)
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

val markers = listOf("подборка продвинутой сложности (ссылка на другую подборку)", "подборка начальной сложности (ссылка на другую подборку)")



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
                newbieBlock = `разбор материала`(iterator.clone())
                advancedBlock = `разбор материала`(iterator.clone(), 5)
            }

        }
    }
    return Blocks(newbieBlock, advancedBlock)
}

fun `разбор материала`(iterator: CoolIterator, startIndex: Int = 0): Block {
    var articles: Articles? = null
    var videos: List<TitleUrlPair>? = null
    var audios: List<TitleUrlPair>? = null
    while (iterator.hasNext()) {
        val next = iterator.next()
        when {
            next.someIs(startIndex, "СТАТЬИ") -> articles = `разбор статей`(iterator, startIndex)
            next.someIs(startIndex, "ВИДЕО (цветной блок)") -> {
                videos = `разбор блока названий и ссылок`(iterator, startIndex, startIndex + 2).map { titleUrlPair ->
                    titleUrlPair.copy(id = extractVideoIdFromUrl(titleUrlPair.url).also {
                    if (it == null)
                        KotlinLogging.logger {  }.warn { "Не правильная ссылка для видео ${titleUrlPair.url}" }
                }) }
                iterator.stepBack()
            }
            next.someIs(startIndex, "АУДИО (белый блок)") -> audios = `разбор блока названий и ссылок`(iterator, startIndex, startIndex + 2).mapAudioUrls()
        }
    }
    return Block(articles, videos, audios)
}
private fun Collection<TitleUrlPair>.mapAudioUrls() = this
        .map { it.copy( url = it.url.replace("ii.ayfaar.org", "ii.ayfaar.ru")) }
        .map {
            if (it.url.contains("ii.ayfaar.ru/r/", true)) {
                val code = it.url.split("ii.ayfaar.ru/r/")[1]
                it.copy(url = "https://ii.ayfaar.ru/api/record/$code/download/$code")
            } else {
                KotlinLogging.logger {  }.warn { "Не правильная ссылка для аудио ${it.url}" }
                it
            }
        }



fun `разбор статей`(iterator: CoolIterator, startIndex: Int): Articles {
    val articles = Articles()
    while (iterator.hasNext()) {
        val next = iterator.next()
        when {
            next.firstIs("Ориса") -> {
                articles.oris = `разбор блока названий и ссылок с темами`(iterator.clone(), startIndex, startIndex, startIndex + 1)
                articles.others = `разбор блока названий и ссылок с темами`(iterator, startIndex, startIndex + 2, startIndex + 3)
            }
        }
        if (iterator.current!!.size > startIndex && markers.contains(iterator.current!![startIndex]))
            break
    }
    return articles
}


fun `разбор блока названий и ссылок с темами`(iterator: CoolIterator, topicIndex: Int, titleIndex: Int, urlIndex: Int): List<TopicArticles> {
    val topicArticles = ArrayList<TopicArticles>()
    val articles = ArrayList<TitleUrlPair>()
    var currentTopic = ""

    fun saveArticlesForTopic() {
        if (articles.isNotEmpty()) {
            topicArticles.add(TopicArticles(ArrayList(articles), currentTopic))
            articles.clear()
        }
    }

    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next.size > titleIndex && !markers.contains(next[titleIndex])) {
            if (next.size > urlIndex) {
                if (next[urlIndex].isBlank()) {
                    saveArticlesForTopic()
                    currentTopic = next[topicIndex]
                    if (markers.contains(currentTopic)) {
                        break
                    }
                } else {
                    articles.add(TitleUrlPair(next[titleIndex], next[urlIndex]))
                }
            } else {
                saveArticlesForTopic()
                currentTopic = next[titleIndex]
            }
        } else {
            saveArticlesForTopic()
            if (next.size == 1) {
                currentTopic = next[0]
            } else if (next.size == titleIndex - 1) {
                currentTopic = next[titleIndex - 2]
            }

            if (markers.contains(currentTopic)) {
                break
            }
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
        if (next.size > titleIndex && !markers.contains(next[titleIndex])
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
