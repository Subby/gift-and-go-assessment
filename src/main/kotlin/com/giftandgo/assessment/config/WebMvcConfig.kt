package com.giftandgo.assessment.config

import com.giftandgo.assessment.interceptor.IpVerificationRequestInterceptor
import com.giftandgo.assessment.service.requestverification.IPApiRequestVerificationService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(val requestVerificationService: IPApiRequestVerificationService) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(IpVerificationRequestInterceptor(requestVerificationService)).addPathPatterns("/processFile")
        super.addInterceptors(registry)
    }

}
