package com.giftandgo.assessment.controller

import com.giftandgo.assessment.model.FileProcessError
import com.giftandgo.assessment.model.FileProcessSuccess
import com.giftandgo.assessment.service.FileProcessorService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller("/v1/")
class FileProcessorController(
    private val outcomeFileProcessorService: FileProcessorService
) {

    @PostMapping(name = "/processFile", consumes = ["multipart/form-data"])
    fun processFile(@RequestParam("File") file: MultipartFile): ResponseEntity<Any> {
        outcomeFileProcessorService.processFile(file.inputStream).let {
            when (it) {
                is FileProcessError -> {
                    return ResponseEntity.badRequest().body(it.errors.joinToString(separator = "\n"))
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