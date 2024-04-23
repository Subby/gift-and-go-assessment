package com.giftandgo.assessment.interceptor

import com.giftandgo.assessment.model.requestverification.RequestVerificationFailure
import com.giftandgo.assessment.model.requestverification.RequestVerificationSuccess
import com.giftandgo.assessment.service.requestverification.IPApiRequestVerificationService
import com.giftandgo.assessment.service.requestverification.RequestVerificationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.Nullable
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.lang.Exception
import java.time.LocalDateTime

private const val requestStartTimeAttributeName = "requestStartTime"

class IpVerificationRequestInterceptor(private val requestVerificationService: RequestVerificationService) :
    HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setAttribute(requestStartTimeAttributeName, LocalDateTime.now())
        requestVerificationService.verifyRequestForIp(request.remoteAddr).let {
            when (it) {
                is RequestVerificationSuccess -> {
                    return super.preHandle(request, response, handler)
                }
                is RequestVerificationFailure -> {
                    response.status = 403
                    response.writer.write(it.reason)
                    response.writer.flush()
                    return false
                }
            }
        }

    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        println("YOxsdasdasd!sdsdasd!!!")
        super.postHandle(request, response, handler, modelAndView)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        @Nullable ex: Exception?
    ) {
        val uri = request.requestURI
        val responseStatus = response.status
        println("YOxsdasdasd!!!!")
    }

    fun handleRequestLogging() {

    }


}
