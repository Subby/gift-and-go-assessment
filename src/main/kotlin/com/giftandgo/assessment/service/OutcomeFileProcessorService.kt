package com.giftandgo.assessment.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.giftandgo.assessment.model.EntryFile
import com.giftandgo.assessment.model.FileProcessResult
import com.giftandgo.assessment.model.FileProcessSuccess
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service
class OutcomeFileProcessorService(
    @Qualifier("csvMapper") val csvObjectMapper: ObjectMapper,
    @Qualifier("jsonMapper") val jsonObjectMapper: ObjectMapper
) : IOutcomeFileProcessorService {
    override fun processFile(fileInputStream: InputStream): FileProcessResult {
        //TODO: Do we need .java??
        val mappedDataFile =
            csvObjectMapper.readValue(fileInputStream, EntryFile::class.java).also { it.toDataFile() }

        return FileProcessSuccess(
            inputStream = InputStreamResource(
                ByteArrayInputStream(
                    jsonObjectMapper.writeValueAsBytes(
                        mappedDataFile
                    )
                )
            )
        )
    }
}
