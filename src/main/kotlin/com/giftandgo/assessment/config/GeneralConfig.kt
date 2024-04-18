package com.giftandgo.assessment.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.giftandgo.assessment.model.EntryFile
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeneralConfig {

    @Bean("csvMapper")
    fun csvOjectMapperConfig(): ObjectMapper {
        val mapper = CsvMapper()
        mapper.registerModule(KotlinModule())
        mapper.schemaFor(EntryFile::class.java)
        return mapper
    }

    @Bean("jsonMapper")
    fun jsonObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        return mapper
    }

}