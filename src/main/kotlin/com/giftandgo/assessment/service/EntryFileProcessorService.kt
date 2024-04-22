package com.giftandgo.assessment.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.giftandgo.assessment.model.*
import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream


private const val VALID_FILE_NAME = "EntryFile.txt"

@Service
class EntryFileProcessorService(
    @Qualifier("csvObjectMapper") val csvObjectMapper: ObjectMapper,
    @Qualifier("jsonObjectMapper") val jsonObjectMapper: ObjectMapper
) : FileProcessorService {
    override fun processFile(file: MultipartFile): FileProcessResult {
        if (!file.originalFilename.equals(VALID_FILE_NAME)) {
            return FileProcessError(errors = listOf("Invalid file name provided"))
        }
        try {
            val validatedEntryFileValues = csvObjectMapper.readerFor(EntryFile::class.java).with(entryFileCsvSchema())
                .readValues<EntryFile>(file.bytes).readAll().map { it.validateEntryFile() }

            val (validEntryFies, invalidEntryFiles) = validatedEntryFileValues.partition { it is Valid }

            if (invalidEntryFiles.isNotEmpty()) {
                val invalidEntryFileValidationMessages =
                    validatedEntryFileValues.asSequence().filter { it is Invalid }.map { it.errors }.flatten()
                        .map { it.message }.toList()
                return FileProcessError(errors = invalidEntryFileValidationMessages)
            }

            val mappedDataFiles = validEntryFies.map { it as Valid }.map { it.value.toDataFile() }

            return FileProcessSuccess(
                inputStream = InputStreamResource(
                    ByteArrayInputStream(
                        jsonObjectMapper.writeValueAsBytes(
                            mappedDataFiles
                        )
                    )
                )
            )
        } catch (exception: Exception) {
            return FileProcessError(errors = listOf("Incorrect format found"))
        }
    }
}
