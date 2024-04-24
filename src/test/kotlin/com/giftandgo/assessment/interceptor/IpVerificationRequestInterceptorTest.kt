package com.giftandgo.assessment.interceptor

import com.giftandgo.assessment.model.requestverification.RequestRecord
import com.giftandgo.assessment.model.requestverification.RequestVerificationFailure
import com.giftandgo.assessment.model.requestverification.RequestVerificationSuccess
import com.giftandgo.assessment.service.requestverification.RequestRecordService
import com.giftandgo.assessment.service.requestverification.RequestVerificationService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.time.LocalDateTime

class IpVerificationRequestInterceptorTest {
    private val requestVerificationService = mockk<RequestVerificationService>(relaxed = true)
    private val requestRecordService = mockk<RequestRecordService>(relaxed = true)

    private val ipVerificationRequestInterceptor =
        IpVerificationRequestInterceptor(
            requestVerificationService = requestVerificationService,
            requestRecordService = requestRecordService,
        )

    @Test
    fun `preHandle should forward request and record when ip verification succeeds`() {
        every { requestVerificationService.verifyRequestForIp("127.0.0.1") } returns
            RequestVerificationSuccess(
                requestCountryCode = "GB",
                requestIsp = "GCP",
            )

        ipVerificationRequestInterceptor.preHandle(
            request = MockHttpServletRequest(),
            response = MockHttpServletResponse(),
            handler = String(),
        ) shouldBe true

        verify(exactly = 0) { requestRecordService.recordRequest(any(RequestRecord::class)) }
    }

    @Test
    fun `preHandle should stop request and record when ip verification fails`() {
        every { requestVerificationService.verifyRequestForIp("127.0.0.1") } returns
            RequestVerificationFailure(
                requestCountryCode = "GB",
                reason = "The country is blocked!",
                requestIsp = "Sky",
            )
        ipVerificationRequestInterceptor.preHandle(
            request = MockHttpServletRequest(),
            response = MockHttpServletResponse(),
            handler = String(),
        ) shouldBe false

        verify { requestRecordService.recordRequest(any(RequestRecord::class)) }
    }

    @Test
    fun `afterCompletion should record request`() {
        val mockHttpServletRequest = MockHttpServletRequest()
        mockHttpServletRequest.setAttribute("requestStartTime", LocalDateTime.now())
        mockHttpServletRequest.setAttribute("requestCountryCode", "GB")
        mockHttpServletRequest.setAttribute("requestISP", "Sky")

        ipVerificationRequestInterceptor.afterCompletion(
            request = mockHttpServletRequest,
            response = MockHttpServletResponse(),
            handler = String(),
            ex = null,
        )

        verify { requestRecordService.recordRequest(any(RequestRecord::class)) }
    }
}
