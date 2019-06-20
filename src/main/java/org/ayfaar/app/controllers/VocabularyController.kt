package org.ayfaar.app.controllers

import org.ayfaar.app.vocabulary.VocabularyService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject


@RestController
@RequestMapping("api/vocabulary")
class VocabularyController {
    @Inject lateinit var service: VocabularyService

    @RequestMapping("doc")
    fun getDoc(): ResponseEntity<ByteArray> {

        val file = service.getDoc()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        // Here you have to set the actual filename of your pdf
        val filename = "vocabulary.docx"
        headers.setContentDispositionFormData(filename, filename)
        return ResponseEntity(file.readBytes(), headers, HttpStatus.OK)
    }
}
