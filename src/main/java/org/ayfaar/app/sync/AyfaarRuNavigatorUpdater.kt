package org.ayfaar.app.sync

import com.google.api.services.sheets.v4.model.Sheet
import com.google.gson.Gson
import mu.KotlinLogging
import org.ayfaar.app.utils.GoogleService
import org.ayfaar.app.utils.Transliterator

class AyfaarRuNavigatorUpdater {
    private val log = KotlinLogging.logger {  }
    val map = listOf(
            Pair("Тест", "1s1kYDmSrL2fShaPrMUO2oWrP-3tyySkp440Z29AuMFg"),
            Pair("Устройство мироздания", "1KBA_9B9bySStHtA_7B1uR83uOT-EnKz2-pH3bREVd1Y"),
            Pair("Сознание и биология человека", "1LYaTF2Tu0qfboAXtbserFK40C1JFr3tfikJHD078Aq0"),
            Pair("Взаимоотношения людей", "1pbLXSJ82xdvhU4UuEE4juivEUpS14dp-PE1tMjEgFVU"),
            Pair("Связь человека с мирозданием", "1D_r68VGcIucDFstNvhTDJp_Ir8Aruo2Cj7BRT31s9no"),
            Pair("Будущее человечества", "18IETRXsA-lWxAuNxzuuWItlE5vEnBUBS56_SYfyj2N0")
    )

    fun sync() {
        log.info("Synchronization started")

        val jsonMap = HashMap<String, Any>()

        map.map { Pair(it.first, loadGoogleData(it.second)) }.forEach {
            jsonMap[it.first] = it.second.map { mapOf(
                    Pair("name", it),
                    Pair("alias", Transliterator.forUrl(it.first))
            ) }
        }
        log.info { Gson().toJson(jsonMap) }

        log.info("Synchronization finished")
    }

    private fun loadGoogleData(id: String) = GoogleService.getSheetsService()
                .spreadsheets()
                .get(id)
                .execute()
                .sheets
                .map { Pair(it.properties.title, it.loadData(id) ) }


}

private fun Sheet.loadData(id: String) = GoogleService.getSheetsService()
        .spreadsheets()
        .values()
        .get(id, "A:Z")
        .execute()
        .getValues()
        .parseData()

fun List<List<Any>>.parseData(): Pair<Block?, Block?> {
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
    return Pair(newbieBlock, advancedBlock)
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
                videos = `разбор блока названий и ссылок`(iterator, startIndex, startIndex + 2)
                iterator.stepBack()
            }
            next.someIs(startIndex, "АУДИО (белый блок)") -> audios = `разбор блока названий и ссылок`(iterator, startIndex, startIndex + 2)
        }
    }
    return Block(articles, videos, audios)
}


fun `разбор статей`(iterator: CoolIterator, startIndex: Int): Articles {
    val articles = Articles()
    while (iterator.hasNext()) {
        val next = iterator.next()
        when {
            next.firstIs("Ориса") -> {
                articles.oris = `разбор блока названий и ссылок`(iterator.clone(), startIndex, startIndex + 1)
                articles.others = `разбор блока названий и ссылок`(iterator.clone(), startIndex + 2, startIndex + 3)
            }
        }
        if (next.firstIs("подборка продвинутой сложности (ссылка на другую подборку)"))
            break
    }
    return articles
}

data class Articles(var oris: List<TitleUrlPair>? = null, var others: List<TitleUrlPair>? = null)

fun `разбор блока названий и ссылок`(iterator: CoolIterator, titleIndex: Int, urlIndex: Int): ArrayList<TitleUrlPair> {
    val articles = ArrayList<TitleUrlPair>()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next.firstNot("подборка продвинутой сложности (ссылка на другую подборку)") && next.size > urlIndex && next[titleIndex].isNotBlank()) {
            articles.add(TitleUrlPair(next[titleIndex], next[urlIndex]))
        } else {
            break
        }
    }
    return articles
}


private fun List<String>.someIs(index: Int, str: String) = if (this.size > index) this[index].trim() == str else false
private fun List<String>.firstIs(str: String) = this.firstOrNull()?.trim() == str
private fun List<String>.firstNot(str: String) = this.firstOrNull()?.trim() != str

data class TitleUrlPair(val title: String, val url: String)


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

data class Block(val articles: Articles?, val videos: List<TitleUrlPair>?, val audios: List<TitleUrlPair>?)
