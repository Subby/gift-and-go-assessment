package com.giftandgo.assessment.service

import com.giftandgo.assessment.model.FileProcessResult
import java.io.InputStream

interface FileProcessorService {
    fun processFile(fileInputStream: InputStream): FileProcessResult
}
