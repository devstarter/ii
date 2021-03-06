package org.ayfaar.app.controllers

import com.google.common.net.UrlEscapers
import org.ayfaar.app.vocabulary.VocabularyService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("api/vocabulary")
class VocabularyController {
    @Inject lateinit var service: VocabularyService

    private val name = UrlEscapers.urlFragmentEscaper().escape("терминологический словарь ииссиидиологических неологизмов")

    @RequestMapping("doc")
    fun getDoc(response: HttpServletResponse) = response.sendRedirect("/api/vocabulary/doc/$name.${getCurrentDate()}.docx")

    private fun getCurrentDate() = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

    @RequestMapping("doc/{name:.+}")
    fun getDocNamed(): ResponseEntity<ByteArray> {
        val file = service.getDoc()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        // Here you have to set the actual filename of your pdf
//        val filename = "словарь.${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}.docx"
//        headers.setContentDispositionFormData(filename, filename)
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;")
        return ResponseEntity(file.readBytes(), headers, HttpStatus.OK)
    }
}
