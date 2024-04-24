package com.giftandgo.assessment.controller

import com.giftandgo.assessment.model.fileprocessing.FileProcessError
import com.giftandgo.assessment.model.fileprocessing.FileProcessSuccess
import com.giftandgo.assessment.service.fileprocessing.FileProcessorService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
class FileProcessorController(
    private val outcomeFileProcessorService: FileProcessorService
) {

    @PostMapping("/processFile")
    fun processFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        outcomeFileProcessorService.processFile(file).let {
            when (it) {
                is FileProcessError -> {
                    return ResponseEntity.unprocessableEntity().body(it.errors.joinToString(separator = "\n"))
                }
                is FileProcessSuccess -> {
                    return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=${it.fileName}")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(it.inputStream)
                }
            }
        }
    }
}
