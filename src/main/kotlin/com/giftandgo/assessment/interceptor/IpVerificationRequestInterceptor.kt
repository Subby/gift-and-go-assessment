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
import org.springframework.web.servlet.ModelAndView
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private const val requestStartTimeAttributeName = "requestStartTime"
private const val requestCountryCodeAttributeName = "requestCountryCode"


class IpVerificationRequestInterceptor(
    private val requestVerificationService: RequestVerificationService,
    private val requestRecordService: RequestRecordService
) :
    HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setAttribute(requestStartTimeAttributeName, LocalDateTime.now())
        requestVerificationService.verifyRequestForIp(request.remoteAddr).let {
            when (it) {
                is RequestVerificationSuccess -> {
                    request.setAttribute(requestCountryCodeAttributeName, it.requestCountryCode)
                    return super.preHandle(request, response, handler)
                }

                is RequestVerificationFailure -> {
                    val requestStartTime = request.getAttribute(requestStartTimeAttributeName) as LocalDateTime
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
                            timeLapsed = ChronoUnit.MILLIS.between(requestStartTime, LocalDateTime.now()).toInt()
                        )
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
        @Nullable ex: Exception?
    ) {
        val currentDateTime = LocalDateTime.now()
        val requestStartTime = request.getAttribute(requestStartTimeAttributeName) as LocalDateTime
        val requestCountryCode = request.getAttribute(requestCountryCodeAttributeName) as String
        requestRecordService.recordRequest(
            RequestRecord(
                id = null,
                uri = request.requestURI,
                timeStamp = requestStartTime,
                responseCode = response.status.toShort(),
                requestIP = request.remoteAddr,
                countryCode = requestCountryCode,
                timeLapsed = ChronoUnit.MILLIS.between(requestStartTime, currentDateTime).toInt()
            )
        )
    }


}
