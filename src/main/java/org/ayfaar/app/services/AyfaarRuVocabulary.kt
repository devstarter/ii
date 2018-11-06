package org.ayfaar.app.services

import mu.KotlinLogging
import org.ayfaar.app.event.NewLinkEvent
import org.ayfaar.app.event.TermAddedeEvent
import org.ayfaar.app.event.TermMorphAddedEvent
import org.ayfaar.app.utils.TermService
import org.ayfaar.app.utils.Transliterator
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@Service
class AyfaarRuVocabulary() {
    constructor(termService: TermService) : this() {
        this.termService = termService
    }

    @Inject private lateinit var environment: Environment
    @Inject private lateinit var termService: TermService
    private val ignoreTerms = arrayOf("интеллект", "чувство", "мысль", "воля", "время", "мир", "личность", "жизнь", "ген", "молекула", "атом", "элементарный", "форма", "окружающая действительность", "днк", "трансформация", "сознание", "мироздание", "закон", "реальность", "энергия", "истина", "масса", "вселенная", "идеи", "разум", "масса", "инерция", "земля", "аспект", "орис", "осознанность", "любовь", "пространство", "мудрость", "cознание", "высокочувственный интеллект", "вселенная", "сон", "душа", "окружающую действительность", "смерть", "высокоинтеллектуальный альтруизм", "интуиция", "голографический принцип", "информация", "айфаар", "сущность", "биополе", "голографичность", "эмоции", "чувства", "мысли", "квантовый", "эволюционирование", "сценарий развития")

    private val log = KotlinLogging.logger {  }
    private val cache = ConcurrentHashMap<String, List<TermTokens>>()

    @Suppress("NAME_SHADOWING")
    fun findTerms(text: String, referer: String, id: String, useCache: Boolean = true): List<TermTokens> {
        log.debug { "Find terms for $referer" }
        val globalCacheEnabled = environment.getProperty("ayfaar_ru.vocabulary.cache.enabled")?.let { it == "true" } ?: true
        val cacheKey = computeKey(referer, id)
        return if (useCache && globalCacheEnabled && cache.containsKey(cacheKey)) {
            cache[cacheKey]!!.also { log.debug { "Cache used" } }
        } else {
            findTerms(text).also { cache[cacheKey] = it }
        }
    }

    internal fun findTerms(text: String): List<TermTokens> {
        val (terms, unrecognizedWords) = termService.findTerms(text)

        findTermsUsingRoots(unrecognizedWords).let {
            log.debug { "Found by root ${it.size} terms" }
            terms.putAll(it)
        }

        return terms
                .filterNot { (_, term) -> ignoreTerms.contains(term.name.toLowerCase()) }
                .map { (word, term) -> Pair(word, term.mainOrThis) }
                .filterNot { (_, term) -> ignoreTerms.contains(term.name.toLowerCase()) }
                .map { (word, term) ->
                    Pair(word, TermTokens(
                            word = word,
                            text = term.shortDescription.orElseGet {
                                var description = term.term?.description ?: ""
                                if (description.length > 300) {
                                    description = description.substring(0, 300).plus("...")
                                }
                                description
                            },
                            url = Transliterator.forUrl(term.name),
                            term = term.name))
                }
                .distinctBy { (word, _) -> word }
                .map { (_, term) -> term }
    }

    private fun computeKey(referer: String, id: String?) = "$referer:$id"

    @Suppress("NAME_SHADOWING")
    fun findTermsUsingRoots(text: String): List<Pair<String, TermService.TermProvider>> {
        val roots = termService.termRoots
        return text.splitToWords().mapNotNull { word ->
            roots
                    .find { (root, _) -> word.startsWith(root) }
                    ?.also { log.debug { "слово: $word, корень: ${it.first}, термин: ${it.second.name}" } }
                    ?.second
                    ?.let { Pair(word, it) }
        }
    }

    @EventListener
    fun onNewTerm(event: TermAddedeEvent) = clearCache()
    @EventListener
    fun onNewLink(event: NewLinkEvent) = clearCache()
    @EventListener
    fun onNewTermMorph(event: TermMorphAddedEvent) = clearCache()

    fun clearCache() = cache.clear().also { log.debug { "Cache cleared" } }
}

data class TermTokens(val word: String, val term: String, val text: String, val url: String)

fun String.splitToWords() = this
        .split(Regex("[^а-яА-Я-–]"))
        .filter { it.isNotBlank() }
        .codesWithDashes()

//fun splitToWords(text: String) = text.splitToWords()

fun List<String>.codesWithDashes(): List<String> {
    val result = this.toMutableList()
    this.forEach { current ->
        if (current.contains(Regex("[-–]"))) {
            result.remove(current)
            val code = current.split(Regex("[-–]")).takeWhile { part -> part.all { char -> char.isUpperCase() } }.joinToString("-")
            val tail = current.replace(code, "").trim('-', '–')
            result.add(code)
            result.add(tail)
        }
    }
    return result
}

inline fun <A, B, C> Iterable<Pair<A, B>>.mapSecond(transform: (B) -> C) = this.map { (a, b) -> Pair(a, transform(b)) }