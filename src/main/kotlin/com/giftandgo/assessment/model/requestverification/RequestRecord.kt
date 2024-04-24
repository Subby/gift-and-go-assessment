package com.giftandgo.assessment.model.requestverification

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("record_request")
data class RequestRecord(
    @Id
    @Column("id")
    val id: Long?,
    @Column("uuid")
    val uuid: String = UUID.randomUUID().toString(),
    @Column("uri")
    val uri: String,
    @Column("time_stamp")
    val timeStamp: LocalDateTime,
    @Column("response_code")
    val responseCode: Short,
    @Column("request_ip")
    val requestIP: String,
    @Column("request_isp")
    val requestISP: String,
    @Column("country_code")
    val countryCode: String,
    @Column("time_lapsed")
    val timeLapsed: Int
)
