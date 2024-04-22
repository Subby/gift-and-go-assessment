package com.giftandgo.assessment.service

import com.giftandgo.assessment.model.FileProcessResult
import org.springframework.web.multipart.MultipartFile

interface FileProcessorService {
    fun processFile(file: MultipartFile): FileProcessResult
}
