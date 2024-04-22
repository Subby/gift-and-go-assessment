package com.giftandgo.assessment.com.giftandgo.assessment

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class IntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `valid input entry file should return correct data file`() {
        val validEntryFile = MockMultipartFile(
            "file", "EntryFile.txt", "text/plain",
            this::class.java.getResourceAsStream("/input/validEntryFile.txt")!!.bufferedReader().readText()
                .toByteArray()
        )

        val expectedDataFile =
            this::class.java.getResourceAsStream("/output/validDataFile.json")!!.bufferedReader().readText()
                .toByteArray()

        val outputDataFile = mockMvc.perform(
            multipart("/processFile").file(validEntryFile).contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk)
            .andExpect { it.response.getHeader(HttpHeaders.CONTENT_DISPOSITION) shouldBe "attachment; filename=OutcomeFile.json" }
            .andReturn().response.contentAsByteArray

        outputDataFile shouldBe expectedDataFile
    }
}