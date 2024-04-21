package com.giftandgo.assessment.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.giftandgo.assessment.model.EntryFile
import com.giftandgo.assessment.model.FileProcessError
import com.giftandgo.assessment.model.FileProcessResult
import com.giftandgo.assessment.model.FileProcessSuccess
import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service
class OutcomeFileProcessorService(
    @Qualifier("csvObjectMapper") val csvObjectMapper: ObjectMapper,
    @Qualifier("jsonObjectMapper") val jsonObjectMapper: ObjectMapper
) : FileProcessorService {
    override fun processFile(fileInputStream: InputStream): FileProcessResult {
        try {
            //TODO: Do we need .java??
            val validatedEntryFile =
                csvObjectMapper.readValue(fileInputStream, EntryFile::class.java).validateEntryFile()

            validatedEntryFile.let {
                when (it) {
                    is Valid -> return FileProcessSuccess(
                        inputStream = InputStreamResource(
                            ByteArrayInputStream(
                                jsonObjectMapper.writeValueAsBytes(
                                    it.value.toDataFile()
                                )
                            )
                        )
                    )
                    is Invalid -> return FileProcessError(errors = it.errors.map { error -> error.message })
                }
            }
        } catch (exception: Exception) {
            return FileProcessError(errors = listOf("Incorrect format found"))
        }
    }
}
