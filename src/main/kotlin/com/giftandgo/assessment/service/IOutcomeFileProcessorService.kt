package com.giftandgo.assessment.service

import com.giftandgo.assessment.model.FileProcessResult
import java.io.InputStream

//TODO: Ugh can we have a better name here?
interface IOutcomeFileProcessorService {
    fun processFile(fileInputStream: InputStream): FileProcessResult
}
