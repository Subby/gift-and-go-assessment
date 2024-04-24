package com.giftandgo.assessment.service.fileprocessing

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.giftandgo.assessment.config.ApplicationConfigProps
import com.giftandgo.assessment.model.fileprocessing.EntryFile
import com.giftandgo.assessment.model.fileprocessing.FileProcessError
import com.giftandgo.assessment.model.fileprocessing.FileProcessSuccess
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import java.util.UUID

class EntryFileProcessorServiceTest {
    private val csvObjectMapper = mockk<ObjectMapper>()
    private val jsonObjectMapper = mockk<ObjectMapper>()
    private val applicationConfigProps = mockk<ApplicationConfigProps>()

    private val entryFileProcessorService =
        EntryFileProcessorService(
            csvObjectMapper = csvObjectMapper,
            jsonObjectMapper = jsonObjectMapper,
            applicationConfigProps = applicationConfigProps,
        )

    @Test
    fun `processFile returns error when validation is enabled and file name is invalid`() {
        every { applicationConfigProps.enableEntryFileValidation } returns true

        entryFileProcessorService.processFile(
            MockMultipartFile(
                "somefile",
                "somefile.txt",
                null,
                "some-contents".toByteArray(),
            ),
        ) shouldBe FileProcessError(errors = listOf("Invalid file name provided"))

        verify { csvObjectMapper wasNot called }
        verify { jsonObjectMapper wasNot called }
    }

    @Test
    fun `processFile returns error when validation is enabled and file data is invalid`() {
        val mockObjectReader = mockk<ObjectReader>()
        val mappingIterator = mockk<MappingIterator<EntryFile>>()
        val entryFileValues =
            listOf(
                EntryFile(
                    uuid = UUID.randomUUID(),
                    id = "",
                    name = "",
                    likes = "",
                    transport = "",
                    averageSpeed = -0.01,
                    topSpeed = -0.01,
                ),
            )

        every { applicationConfigProps.enableEntryFileValidation } returns true
        every { csvObjectMapper.readerFor(EntryFile::class.java) } returns mockObjectReader
        every { mockObjectReader.with(any(CsvSchema::class)) } returns mockObjectReader
        every { mockObjectReader.readValues<EntryFile>(any(ByteArray::class)) } returns mappingIterator
        every { mappingIterator.readAll() } returns entryFileValues

        entryFileProcessorService.processFile(
            MockMultipartFile(
                "somefile",
                "EntryFile.txt",
                null,
                "some-contents".toByteArray(),
            ),
        ).let {
            it as FileProcessError
            it.errors shouldContainAll
                listOf(
                    "Format must follow [digit]X[digit]D",
                    "Format must follow [First Name] [Last Name]",
                    "Format must follow [Likes] [Something]",
                    "must be at least '0.0'",
                    "must be at least '0.0'",
                )
        }

        verify { jsonObjectMapper wasNot called }
    }

    @Test
    fun `processFile returns success when validation is enabled and file data is valid`() {
        val mockObjectReader = mockk<ObjectReader>()
        val mappingIterator = mockk<MappingIterator<EntryFile>>()
        val entryFileValues =
            listOf(
                EntryFile(
                    uuid = UUID.randomUUID(),
                    id = "1X1D",
                    name = "Kurt Angle",
                    likes = "Likes Pineapples",
                    transport = "Rides Bicycles",
                    averageSpeed = 0.01,
                    topSpeed = 0.01,
                ),
            )

        every { applicationConfigProps.enableEntryFileValidation } returns true
        every { csvObjectMapper.readerFor(EntryFile::class.java) } returns mockObjectReader
        every { mockObjectReader.with(any(CsvSchema::class)) } returns mockObjectReader
        every { mockObjectReader.readValues<EntryFile>(any(ByteArray::class)) } returns mappingIterator
        every { mappingIterator.readAll() } returns entryFileValues
        every { jsonObjectMapper.writeValueAsBytes(any()) } returns "yo".toByteArray()

        entryFileProcessorService.processFile(
            MockMultipartFile(
                "somefile",
                "EntryFile.txt",
                null,
                "some-contents".toByteArray(),
            ),
        ).let {
            it as FileProcessSuccess
            it.inputStream.inputStream.readAllBytes() shouldBe "yo".toByteArray()
        }
    }

    @Test
    fun `processFile returns success when validation is disable and file data is invalid`() {
        val mockObjectReader = mockk<ObjectReader>()
        val mappingIterator = mockk<MappingIterator<EntryFile>>()
        val entryFileValues =
            listOf(
                EntryFile(
                    uuid = UUID.randomUUID(),
                    id = "",
                    name = "",
                    likes = "",
                    transport = "",
                    averageSpeed = -0.01,
                    topSpeed = -0.01,
                ),
            )

        every { applicationConfigProps.enableEntryFileValidation } returns false
        every { csvObjectMapper.readerFor(EntryFile::class.java) } returns mockObjectReader
        every { mockObjectReader.with(any(CsvSchema::class)) } returns mockObjectReader
        every { mockObjectReader.readValues<EntryFile>(any(ByteArray::class)) } returns mappingIterator
        every { mappingIterator.readAll() } returns entryFileValues
        every { jsonObjectMapper.writeValueAsBytes(any()) } returns "yo".toByteArray()

        entryFileProcessorService.processFile(
            MockMultipartFile(
                "somefile",
                "EntryFile.txt",
                null,
                "some-contents".toByteArray(),
            ),
        ).let {
            it as FileProcessSuccess
            it.inputStream.inputStream.readAllBytes() shouldBe "yo".toByteArray()
        }
    }
}
