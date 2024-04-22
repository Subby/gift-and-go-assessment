package com.giftandgo.assessment.service.fileprocessing

import com.fasterxml.jackson.databind.ObjectMapper
import com.giftandgo.assessment.config.ApplicationConfigProps
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
    @Qualifier("jsonObjectMapper") val jsonObjectMapper: ObjectMapper,
    val applicationConfigProps: ApplicationConfigProps,
) : FileProcessorService {
    override fun processFile(file: MultipartFile): FileProcessResult {
        if (applicationConfigProps.enableEntryFileValidation && !file.originalFilename.equals(VALID_FILE_NAME)) {
            return FileProcessError(errors = listOf("Invalid file name provided"))
        }
        try {
            val entryFileValues = csvObjectMapper.readerFor(EntryFile::class.java).with(entryFileCsvSchema())
                .readValues<EntryFile>(file.bytes).readAll()

            if (applicationConfigProps.enableEntryFileValidation) {
                val (validEntryFies, invalidEntryFiles) = entryFileValues.map { it.validateEntryFile() }
                    .partition { it is Valid }

                if (invalidEntryFiles.isNotEmpty()) {
                    val invalidEntryFileValidationMessages =
                        invalidEntryFiles.asSequence().filter { it is Invalid }.map { it.errors }.flatten()
                            .map { it.message }.toList()
                    return FileProcessError(errors = invalidEntryFileValidationMessages)
                }

                return FileProcessSuccess(
                    inputStream = InputStreamResource(
                        ByteArrayInputStream(
                            jsonObjectMapper.writeValueAsBytes(
                                validEntryFies.map { it as Valid }.map { it.value.toDataFile() }
                            )
                        )
                    )
                )
            } else {
                return FileProcessSuccess(
                    inputStream = InputStreamResource(
                        ByteArrayInputStream(
                            jsonObjectMapper.writeValueAsBytes(
                                entryFileValues.map { it.toDataFile() }
                            )
                        )
                    )
                )
            }

        } catch (exception: Exception) {
            return FileProcessError(errors = listOf("Incorrect format found"))
        }
    }
}