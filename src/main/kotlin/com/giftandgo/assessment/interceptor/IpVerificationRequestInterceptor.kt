package com.giftandgo.assessment.interceptor

import com.giftandgo.assessment.model.requestverification.RequestRecord
import com.giftandgo.assessment.model.requestverification.RequestVerificationFailure
import com.giftandgo.assessment.model.requestverification.RequestVerificationSuccess
import com.giftandgo.assessment.service.requestverification.RequestRecordService
import com.giftandgo.assessment.service.requestverification.RequestVerificationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.Nullable
import org.springframework.web.servlet.HandlerInterceptor
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private const val REQUEST_START_TIME_ATTRIBUTE_NAME = "requestStartTime"
private const val REQUEST_COUNTRY_ATTRIBUTE_NAME = "requestCountryCode"
private const val REQUEST_ISP_ATTRIBUTE_NAME = "requestISP"

class IpVerificationRequestInterceptor(
    private val requestVerificationService: RequestVerificationService,
    private val requestRecordService: RequestRecordService,
) :
    HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE_NAME, LocalDateTime.now())
        requestVerificationService.verifyRequestForIp(request.remoteAddr).let {
            when (it) {
                is RequestVerificationSuccess -> {
                    request.setAttribute(REQUEST_COUNTRY_ATTRIBUTE_NAME, it.requestCountryCode)
                    request.setAttribute(REQUEST_ISP_ATTRIBUTE_NAME, it.requestIsp)
                    return super.preHandle(request, response, handler)
                }

                is RequestVerificationFailure -> {
                    val requestStartTime = request.getAttribute(REQUEST_START_TIME_ATTRIBUTE_NAME) as LocalDateTime
                    response.status = 403
                    response.writer.write(it.reason)
                    response.writer.flush()
                    requestRecordService.recordRequest(
                        RequestRecord(
                            id = null,
                            uri = request.requestURI,
                            timeStamp = requestStartTime,
                            responseCode = response.status.toShort(),
                            requestIP = request.remoteAddr,
                            countryCode = it.requestCountryCode,
                            timeLapsed = ChronoUnit.MILLIS.between(requestStartTime, LocalDateTime.now()).toInt(),
                            requestISP = it.requestIsp,
                        ),
                    )
                    return false
                }
            }
        }
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        @Nullable ex: Exception?,
    ) {
        val currentDateTime = LocalDateTime.now()
        val requestStartTime = request.getAttribute(REQUEST_START_TIME_ATTRIBUTE_NAME) as LocalDateTime
        val requestCountryCode = request.getAttribute(REQUEST_COUNTRY_ATTRIBUTE_NAME) as String
        val requestIsp = request.getAttribute(REQUEST_ISP_ATTRIBUTE_NAME) as String
        requestRecordService.recordRequest(
            RequestRecord(
                id = null,
                uri = request.requestURI,
                timeStamp = requestStartTime,
                responseCode = response.status.toShort(),
                requestIP = request.remoteAddr,
                countryCode = requestCountryCode,
                timeLapsed = ChronoUnit.MILLIS.between(requestStartTime, currentDateTime).toInt(),
                requestISP = requestIsp,
            ),
        )
    }
}
