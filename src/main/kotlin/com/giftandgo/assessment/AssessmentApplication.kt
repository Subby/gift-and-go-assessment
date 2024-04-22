package com.giftandgo.assessment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


@SpringBootApplication
class AssessmentApplication

fun main(args: Array<String>) {
	runApplication<AssessmentApplication>(*args)
}

@Component
class EndpointsListener : ApplicationListener<ContextRefreshedEvent> {
	override fun onApplicationEvent(event: ContextRefreshedEvent) {
		val applicationContext: ApplicationContext = event.applicationContext
		applicationContext.getBean(RequestMappingHandlerMapping::class.java).handlerMethods
			.forEach { println(it) }
	}
}