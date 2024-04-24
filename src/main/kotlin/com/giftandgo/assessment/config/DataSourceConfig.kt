package com.giftandgo.assessment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.TransactionManager
import javax.sql.DataSource

@EnableJdbcRepositories("com.giftandgo.assessment.repository")
@Configuration
class DataSourceConfig : AbstractJdbcConfiguration() {
    @Bean
    fun dataSource(env: Environment): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name")!!)
        dataSource.url = env.getProperty("spring.datasource.url")
        dataSource.username = env.getProperty("spring.datasource.username")
        dataSource.password = env.getProperty("spring.datasource.password")
        return dataSource
    }

    @Bean
    fun namedParameterJdbcOperations(dataSource: DataSource): NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    @Bean
    fun transactionManager(dataSource: DataSource): TransactionManager = DataSourceTransactionManager(dataSource)
}
