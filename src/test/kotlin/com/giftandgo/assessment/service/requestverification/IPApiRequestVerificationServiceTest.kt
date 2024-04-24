package com.giftandgo.assessment.service.requestverification

import com.giftandgo.assessment.client.IPApiClient
import com.giftandgo.assessment.config.ApplicationConfigProps
import com.giftandgo.assessment.model.requestverification.IPApiResponse
import com.giftandgo.assessment.model.requestverification.RequestVerificationFailure
import com.giftandgo.assessment.model.requestverification.RequestVerificationSuccess
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class IPApiRequestVerificationServiceTest {
    private val ipApiClient = mockk<IPApiClient>()
    private val applicationConfigProps = mockk<ApplicationConfigProps>()
    private val ipApiRequestVerificationService =
        IPApiRequestVerificationService(ipApiClient = ipApiClient, applicationConfigProps = applicationConfigProps)

    @Test
    fun `verifyRequestForIp returns error when requested isp is blocked`() {
        every { ipApiClient.testIp("127.0.0.1") } returns
            IPApiResponse(
                country = "England", countryCode = "GB", isp = "Sky",
            )
        every { applicationConfigProps.ipBlockedIsps } returns listOf("Sky")

        ipApiRequestVerificationService.verifyRequestForIp("127.0.0.1") shouldBe
            RequestVerificationFailure(
                reason = "Request ISP is blocked", requestCountryCode = "GB", requestIsp = "Sky",
            )
    }

    @Test
    fun `verifyRequestForIp returns error when requested country is blocked`() {
        every { ipApiClient.testIp("127.0.0.1") } returns
            IPApiResponse(
                country = "England", countryCode = "GB", isp = "Sky",
            )
        every { applicationConfigProps.ipBlockedIsps } returns emptyList()
        every { applicationConfigProps.ipBlockedCountries } returns listOf("England")

        ipApiRequestVerificationService.verifyRequestForIp("127.0.0.1") shouldBe
            RequestVerificationFailure(
                reason = "Request Country is blocked", requestCountryCode = "GB", requestIsp = "Sky",
            )
    }

    @Test
    fun `verifyRequestForIp returns success when request is vvalid `() {
        every { ipApiClient.testIp("127.0.0.1") } returns
            IPApiResponse(
                country = "England", countryCode = "GB", isp = "Sky",
            )
        every { applicationConfigProps.ipBlockedIsps } returns emptyList()
        every { applicationConfigProps.ipBlockedCountries } returns emptyList()

        ipApiRequestVerificationService.verifyRequestForIp("127.0.0.1") shouldBe
            RequestVerificationSuccess(
                requestCountryCode = "GB", requestIsp = "Sky",
            )
    }
}
