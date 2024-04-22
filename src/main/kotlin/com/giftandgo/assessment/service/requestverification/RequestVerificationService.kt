package com.giftandgo.assessment.service.requestverification

import com.giftandgo.assessment.model.RequestVerificationResult

interface RequestVerificationService {

    fun verifyRequestForIp(ip: String): RequestVerificationResult

}