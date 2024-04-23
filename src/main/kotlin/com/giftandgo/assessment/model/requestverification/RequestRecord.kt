package com.giftandgo.assessment.model.requestverification

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("record_request")
data class RequestRecord(
    @Id
    val id: Long,
    val uuid: UUID = UUID.randomUUID(),
    val uri: String,
    val timeStamp: LocalDateTime,
    val responseCode: Short,
    val requestIP: String,
    val countryCode: String,
    val timeLapsed: Int
)
