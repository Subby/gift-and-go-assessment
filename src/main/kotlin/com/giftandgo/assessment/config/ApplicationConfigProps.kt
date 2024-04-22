package com.giftandgo.assessment.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application")
data class ApplicationConfigProps(val enableEntryFileValidation: Boolean, val ipApiUrl: String, val ipBlockedCountries: List<String>, val ipBlockedIsps: List<String>)

