package org.ayfaar.app.controllers

import org.ayfaar.app.services.AyfaarRuVocabulary
import org.ayfaar.app.services.TermTokens
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("api/integration")
class IntegrationController {
    @Autowired lateinit var ayfaarRuVocabulary: AyfaarRuVocabulary

    @RequestMapping(consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    @ResponseBody
    fun getTerms(@RequestBody params: MultiValueMap<String, String>, @RequestHeader("Referer") referer: String): List<TermTokens> {
        val text = params.toSingleValueMap().entries.find { it.value.isEmpty() }?.key
        val id = params.toSingleValueMap()["id"] ?: throw Exception("No id provided")
        if (text == null || text.isEmpty()) return emptyList()
        return ayfaarRuVocabulary.findTerms(text, referer, id)
    }

    @RequestMapping("clear-cache")
    fun clearCache() {
        ayfaarRuVocabulary.clearCache()
    }
}
