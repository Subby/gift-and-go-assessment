package com.giftandgo.assessment.model

import java.util.UUID

//TODO: Double or BigDecimal?
data class EntryFile(
    val uuid: UUID,
    val id: String,
    val name: String,
    val transport: String,
    val likes: String,
    val averageSpeed: Double,
    val topSpeed: Double
) {
}