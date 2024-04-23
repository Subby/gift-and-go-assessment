package com.giftandgo.assessment.interceptor

import com.giftandgo.assessment.model.requestverification.RequestVerificationFailure
import com.giftandgo.assessment.model.requestverification.RequestVerificationSuccess
import com.giftandgo.assessment.service.requestverification.IPApiRequestVerificationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.Nullable
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.lang.Exception
import java.time.LocalDateTime

//TODO: More specific name
class IpVerificationRequestInterceptor(private val requestVerificationService: IPApiRequestVerificationService) :
    HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setAttribute("requestStartTime", LocalDateTime.now())
        requestVerificationService.verifyRequestForIp(request.remoteAddr).let {
            when (it) {
                is RequestVerificationSuccess -> {
                    return super.preHandle(request, response, handler)
                }
                is RequestVerificationFailure -> {
                    TODO()
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
    }


}
