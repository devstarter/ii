package org.ayfaar.app.services

import org.ayfaar.app.utils.TermService
import org.ayfaar.app.utils.Transliterator
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/integration")
class AyfaarRuVocabulary(private val termService: TermService) {
    @Suppress("NAME_SHADOWING")
    @ResponseBody
    @RequestMapping
    fun findTerms(@RequestBody text: String, @RequestHeader("Referer") referer: String, @RequestParam id: String): List<TermTokens> {
        val roots = termService.termRoots
        return text.splitToWords().mapNotNull { word ->
            val term: TermService.TermProvider? = termService.get(word).orElseGet {
                roots.find { (root, _) -> word.contains(root) }?.second
            }
            term?.mainOrThis?.let { term -> TermTokens(
                    word = word,
                    text = term.shortDescription.orElseGet { term.term.description.substring(0, 300).plus("...") },
                    url = Transliterator.forUrl(term.name),
                    term = term.name)
            }
        }
    }
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
