package com.giftandgo.assessment.service.fileprocessing

import com.giftandgo.assessment.model.fileprocessing.FileProcessResult
import org.springframework.web.multipart.MultipartFile

interface FileProcessorService {
    fun processFile(file: MultipartFile): FileProcessResult
}
