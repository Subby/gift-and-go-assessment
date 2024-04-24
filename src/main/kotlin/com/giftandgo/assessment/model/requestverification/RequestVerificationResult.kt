package com.giftandgo.assessment.model.requestverification

sealed interface RequestVerificationResult

data class RequestVerificationSuccess(val requestCountryCode: String, val requestIsp: String) :
    RequestVerificationResult

data class RequestVerificationFailure(val requestCountryCode: String, val requestIsp: String, val reason: String) :
    RequestVerificationResult
