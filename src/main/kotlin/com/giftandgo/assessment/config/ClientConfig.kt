package com.giftandgo.assessment.config

import com.giftandgo.assessment.client.IPApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class ClientConfig(val applicationConfigProps: ApplicationConfigProps) {

    @Bean
    fun ipApiClient(): IPApiClient {
        val webClient = WebClient.builder().baseUrl(applicationConfigProps.ipApiUrl).build()
        return HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(webClient)).build().createClient(IPApiClient::class.java)
    }

}