package com.giftandgo.assessment.controller

import com.giftandgo.assessment.model.fileprocessing.FileProcessError
import com.giftandgo.assessment.model.fileprocessing.FileProcessSuccess
import com.giftandgo.assessment.service.fileprocessing.FileProcessorService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatusCode
import org.springframework.mock.web.MockMultipartFile

class FileProcessorControllerTest {
    private val fileProcessorService = mockk<FileProcessorService>()
    private val mockMultipartFile = MockMultipartFile("Some name", "yo!".toByteArray())
    private val fileProcessorController = FileProcessorController(outcomeFileProcessorService = fileProcessorService)

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `processFile returns unprocessable entity when processing error occurs`() {
        every { fileProcessorService.processFile(mockMultipartFile) } returns FileProcessError(errors = listOf("Invalid file name provided"))

        fileProcessorController.processFile(mockMultipartFile).let {
            it.statusCode shouldBe HttpStatusCode.valueOf(422)
            it.body shouldBe "Invalid file name provided"
        }
    }

    @Test
    fun `processFile returns ok entity when processing fails`() {
        every { fileProcessorService.processFile(mockMultipartFile) } returns (
            FileProcessSuccess(
                inputStream =
                    InputStreamResource(
                        "something".byteInputStream(),
                    ),
            )
        )

        fileProcessorController.processFile(mockMultipartFile).let {
            it.statusCode shouldBe HttpStatusCode.valueOf(200)
            it.headers["Content-Disposition"] shouldBe listOf("attachment; filename=OutcomeFile.json")
            it.body shouldNotBe null
        }
    }
}
