package com.giftandgo.assessment.model

import io.konform.validation.Validation
import io.konform.validation.jsonschema.minimum
import io.konform.validation.jsonschema.pattern
import java.util.UUID

data class EntryFile(
    val uuid: UUID,
    val id: String,
    val name: String,
    val likes: String,
    val transport: String,
    val averageSpeed: Double,
    val topSpeed: Double
) {

    fun validateEntryFile() = Validation {
        EntryFile::uuid required {
        }
        EntryFile::id required { pattern("\\d*(X)\\d*(D)\\d*\n") }
        EntryFile::name required { pattern("\\w+\\s*\\w*") }
        EntryFile::likes required { pattern("\\w+\\s*\\w*") }
        EntryFile::transport required { pattern("Likes\\s+(.+)\n") }
        EntryFile::averageSpeed required { minimum(0.00) }
        EntryFile::topSpeed required { minimum(0.00) }
    }.validate(this)


    fun toDataFile() = DataFile(this.name, this.transport, this.topSpeed)
}
