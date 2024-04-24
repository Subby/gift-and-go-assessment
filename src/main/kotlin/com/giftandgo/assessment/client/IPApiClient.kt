package com.giftandgo.assessment.client

import com.giftandgo.assessment.model.requestverification.IPApiResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

interface IPApiClient {
    @GetExchange("/json/{ip}")
    fun testIp(
        @PathVariable ip: String,
    ): IPApiResponse
}
