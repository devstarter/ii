package org.ayfaar.app.services

import mu.KotlinLogging
import org.ayfaar.app.event.NewLinkEvent
import org.ayfaar.app.event.TermAddEvent
import org.ayfaar.app.event.TermMorphAddedEvent
import org.ayfaar.app.utils.TermService
import org.ayfaar.app.utils.Transliterator
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@Service
class AyfaarRuVocabulary() {
    constructor(termService: TermService) : this() {
        this.termService = termService
    }
    @Inject private lateinit var termService: TermService

    private val log = KotlinLogging.logger {  }
    private val cache = ConcurrentHashMap<String, List<TermTokens>>()

    @Suppress("NAME_SHADOWING")
    fun findTerms(text: String, referer: String = "", id: String = ""): List<TermTokens> {
        log.debug { "Find terms for $referer $id" }
        return cache[computeKey(referer, id)]
                ?.let { it }
                ?.also { log.debug { "Used cache" } }
                ?: termService.findTerms(text).map { (word, term) -> term.mainOrThis.let { term -> TermTokens(
                            word = word,
                            text = term.shortDescription.orElseGet { term.term?.description?.substring(0, 300)?.plus("...") ?: "" },
                            url = Transliterator.forUrl(term.name),
                            term = term.name)}
                }
                        .distinctBy { it.word }
                        .also { cache[computeKey(referer, id)] = it }
    }

    private fun computeKey(referer: String, id: String?) = "$referer:$id"

    @Suppress("NAME_SHADOWING")
    fun findTermsByWords(text: String, referer: String = "", id: String? = ""): List<TermTokens> {
        val roots = termService.termRoots
        return text.splitToWords().mapNotNull { word ->
            val term: TermService.TermProvider? = termService.get(word).orElseGet {
                roots.find { (root, _) -> word.contains(root) }?.second
            }
            term?.mainOrThis?.let { term -> TermTokens(
                    word = word,
                    text = term.shortDescription.orElseGet { term.term?.description?.substring(0, 300)?.plus("...") ?: "" },
                    url = Transliterator.forUrl(term.name),
                    term = term.name)
            }
        }.distinctBy { it.word }
    }

    @EventListener
    fun onNewTerm(event: TermAddEvent) = clearCache()
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
