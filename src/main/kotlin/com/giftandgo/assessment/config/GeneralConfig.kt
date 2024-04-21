package com.giftandgo.assessment.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.giftandgo.assessment.model.EntryFile
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class GeneralConfig {

    @Primary
    @Bean
    fun csvObjectMapper(): ObjectMapper {
        val mapper = CsvMapper()
        mapper.registerModule(KotlinModule())
        mapper.schemaFor(EntryFile::class.java).withColumnSeparator('|')
        return mapper
    }

    @Bean
    fun jsonObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        return mapper
    }

}