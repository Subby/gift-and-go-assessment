package com.giftandgo.assessment.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application")
data class ApplicationConfigProperties(val enableEntryFileValidation: Boolean)

