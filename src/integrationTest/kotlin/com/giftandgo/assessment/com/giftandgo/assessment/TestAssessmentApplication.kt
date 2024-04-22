package com.giftandgo.assessment.com.giftandgo.assessment

import com.giftandgo.assessment.AssessmentApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestAssessmentApplication

fun main(args: Array<String>) {
	fromApplication<AssessmentApplication>().with(TestAssessmentApplication::class).run(*args)
}
