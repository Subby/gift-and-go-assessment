package com.giftandgo.assessment.com.giftandgo.assessment

import com.giftandgo.assessment.repository.RequestRecordRepository
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.get
import com.marcinziolo.kotlin.wiremock.returns
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.MySQLContainer
import java.time.Duration
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest
class IntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var requestRecordRepository: RequestRecordRepository

    @AfterEach
    fun cleardown() {
        wiremock.resetAll()
        requestRecordRepository.deleteAll()
    }

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

        queueSuccessfulIPApiResponse()

        val outputDataFile = mockMvc.perform(
            multipart("/processFile").file(validEntryFile).contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk)
            .andExpect { it.response.getHeader(HttpHeaders.CONTENT_DISPOSITION) shouldBe "attachment; filename=OutcomeFile.json" }
            .andReturn().response.contentAsByteArray

        outputDataFile shouldBe expectedDataFile

        await atMost Duration.ofSeconds(5) untilAsserted {
            requestRecordRepository.findAll().first().let {
                it.id shouldNotBe null
                it.uuid shouldNotBe null
                it.uri shouldBe "/processFile"
                it.timeStamp shouldBeBefore LocalDateTime.now()
                it.responseCode shouldBe 200
                it.requestIP shouldBe "127.0.0.1"
                it.countryCode shouldBe "GB"
                it.timeLapsed shouldBeLessThan 8000
            }
        }

    }

    @Test
    fun `broken input entry file should return incorrect format 422 error`() {
        val validEntryFile = MockMultipartFile(
            "file", "EntryFile.txt", "text/plain",
            this::class.java.getResourceAsStream("/input/brokenEntryFile.txt")!!.bufferedReader().readText()
                .toByteArray()
        )

        queueSuccessfulIPApiResponse()

        mockMvc.perform(
            multipart("/processFile").file(validEntryFile).contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isUnprocessableEntity)
            .andExpect(content().string("Incorrect format found"))

        await atMost Duration.ofSeconds(5) untilAsserted {
            requestRecordRepository.findAll().first().let {
                it.id shouldNotBe null
                it.uuid shouldNotBe null
                it.uri shouldBe "/processFile"
                it.timeStamp shouldBeBefore LocalDateTime.now()
                it.responseCode shouldBe 422
                it.requestIP shouldBe "127.0.0.1"
                it.countryCode shouldBe "GB"
                it.timeLapsed shouldBeLessThan 8000
            }
        }

    }

    @Test
    fun `misnamed input entry file should return incorrect format 422 error`() {
        val validEntryFile = MockMultipartFile(
            "file", "Yo.txt", "text/plain",
            this::class.java.getResourceAsStream("/input/validEntryFile.txt")!!.bufferedReader().readText()
                .toByteArray()
        )

        queueSuccessfulIPApiResponse()

        mockMvc.perform(
            multipart("/processFile").file(validEntryFile).contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isUnprocessableEntity)
            .andExpect(content().string("Invalid file name provided"))

        await atMost Duration.ofSeconds(5) untilAsserted {
            requestRecordRepository.findAll().first().let {
                it.id shouldNotBe null
                it.uuid shouldNotBe null
                it.uri shouldBe "/processFile"
                it.timeStamp shouldBeBefore LocalDateTime.now()
                it.responseCode shouldBe 422
                it.requestIP shouldBe "127.0.0.1"
                it.countryCode shouldBe "GB"
                it.timeLapsed shouldBeLessThan 8000
            }
        }
    }

    @Test
    fun `invalid input entry file should return incorrect format 422 error`() {
        val validEntryFile = MockMultipartFile(
            "file", "EntryFile.txt", "text/plain",
            this::class.java.getResourceAsStream("/input/invalidEntryFile.txt")!!.bufferedReader().readText()
                .toByteArray()
        )

        queueSuccessfulIPApiResponse()

        mockMvc.perform(
            multipart("/processFile").file(validEntryFile).contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isUnprocessableEntity)
            .andExpect(content().string("Format must follow [Likes] [Something]"))

        await atMost Duration.ofSeconds(5) untilAsserted {
            requestRecordRepository.findAll().first().let {
                it.id shouldNotBe null
                it.uuid shouldNotBe null
                it.uri shouldBe "/processFile"
                it.timeStamp shouldBeBefore LocalDateTime.now()
                it.responseCode shouldBe 422
                it.requestIP shouldBe "127.0.0.1"
                it.countryCode shouldBe "GB"
                it.timeLapsed shouldBeLessThan 8000
            }
        }
    }

    @Test
    fun `blocked ISP should return 403 error`() {
        val validEntryFile = MockMultipartFile(
            "file", "EntryFile.txt", "text/plain",
            this::class.java.getResourceAsStream("/input/validEntryFile.txt")!!.bufferedReader().readText()
                .toByteArray()
        )

        queueBlockedISPApiResponse()

        mockMvc.perform(
            multipart("/processFile").file(validEntryFile).contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isForbidden)
            .andExpect(content().string("Request ISP is blocked"))

        await atMost Duration.ofSeconds(5) untilAsserted {
            requestRecordRepository.findAll().first().let {
                it.id shouldNotBe null
                it.uuid shouldNotBe null
                it.uri shouldBe "/processFile"
                it.timeStamp shouldBeBefore LocalDateTime.now()
                it.responseCode shouldBe 403
                it.requestIP shouldBe "127.0.0.1"
                it.countryCode shouldBe "GB"
                it.timeLapsed shouldBeLessThan 8000
            }
        }
    }

    @Test
    fun `blocked country should return 403 error`() {
        val validEntryFile = MockMultipartFile(
            "file", "EntryFile.txt", "text/plain",
            this::class.java.getResourceAsStream("/input/validEntryFile.txt")!!.bufferedReader().readText()
                .toByteArray()
        )

        queueBlockedCountryApiResponse()

        mockMvc.perform(
            multipart("/processFile").file(validEntryFile).contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isForbidden)
            .andExpect(content().string("Request Country is blocked"))

        await atMost Duration.ofSeconds(5) untilAsserted {
            requestRecordRepository.findAll().first().let {
                it.id shouldNotBe null
                it.uuid shouldNotBe null
                it.uri shouldBe "/processFile"
                it.timeStamp shouldBeBefore LocalDateTime.now()
                it.responseCode shouldBe 403
                it.requestIP shouldBe "127.0.0.1"
                it.countryCode shouldBe "GB"
                it.timeLapsed shouldBeLessThan 8000
            }
        }
    }

    private fun queueSuccessfulIPApiResponse() {
        val successfulResponseContent =
            this::class.java.getResourceAsStream("/responses/successfulResponse.json")!!.bufferedReader().readText()
        wiremock.get {
            url equalTo "/json/127.0.0.1"
        } returns {
            header = "Content-Type" to "application/json"
            statusCode = 200
            body = successfulResponseContent
        }
    }

    private fun queueBlockedISPApiResponse() {
        val blockedISPResponse =
            this::class.java.getResourceAsStream("/responses/blockedISPResponse.json")!!.bufferedReader().readText()
        wiremock.get {
            url equalTo "/json/127.0.0.1"
        } returns {
            header = "Content-Type" to "application/json"
            statusCode = 200
            body = blockedISPResponse
        }
    }

    private fun queueBlockedCountryApiResponse() {
        val blockedCountryResponse =
            this::class.java.getResourceAsStream("/responses/blockedCountryResponse.json")!!.bufferedReader().readText()
        wiremock.get {
            url equalTo "/json/127.0.0.1"
        } returns {
            header = "Content-Type" to "application/json"
            statusCode = 200
            body = blockedCountryResponse
        }
    }



    companion object {
        private val wiremock = WireMockServer(options().dynamicPort())
        private val databaseContainer = MySQLContainer("mysql:8.3.0").withDatabaseName("assessment")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            wiremock.start()
            databaseContainer.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wiremock.stop()
            databaseContainer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun setupConfigProps(registry: DynamicPropertyRegistry) {
            registry.add("application.ip-api-url", wiremock::baseUrl)
            registry.add("spring.datasource.url", databaseContainer::getJdbcUrl)
            registry.add("spring.datasource.username", databaseContainer::getUsername)
            registry.add("spring.datasource.password", databaseContainer::getPassword)
        }
    }
}


