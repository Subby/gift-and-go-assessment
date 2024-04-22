package com.giftandgo.assessment.model


sealed interface RequestVerificationResult

data class RequestVerificationSuccess(val requestCountryCode: String, val requestIsp: String): RequestVerificationResult

data class RequestVerificationFailure(val reason: String): RequestVerificationResult