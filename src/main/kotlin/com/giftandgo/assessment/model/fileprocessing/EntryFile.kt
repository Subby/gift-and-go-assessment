package com.giftandgo.assessment.model.fileprocessing

import com.fasterxml.jackson.dataformat.csv.CsvSchema
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
        EntryFile::id required { pattern("\\d*(X)\\d*(D)\\d*") hint "Format must follow [digit]X[digit]D" }
        EntryFile::name required { pattern("\\w+\\s*\\w*") hint "Format must follow [First Name] [Last Name]"}
        EntryFile::likes required { pattern("Likes\\s+(.+)") hint "Format must follow [Likes] [Something]"}
        EntryFile::transport required { pattern("((Rides|Drives)\\s+.*)") hint "Format must follow [Rides/Drives] [Something]" }
        EntryFile::averageSpeed required { minimum(0.00) }
        EntryFile::topSpeed required { minimum(0.00) }
    }.validate(this)


    fun toDataFile() = DataFile(this.name, this.transport, this.topSpeed)
}

fun entryFileCsvSchema(): CsvSchema {
    return CsvSchema.builder()
        .setColumnSeparator('|')
        .setUseHeader(false)
        .addColumn("uuid", CsvSchema.ColumnType.STRING)
        .addColumn("id", CsvSchema.ColumnType.STRING)
        .addColumn("name", CsvSchema.ColumnType.STRING)
        .addColumn("likes", CsvSchema.ColumnType.STRING)
        .addColumn("transport", CsvSchema.ColumnType.STRING)
        .addColumn("averageSpeed", CsvSchema.ColumnType.STRING)
        .addColumn("topSpeed", CsvSchema.ColumnType.NUMBER).build()
}
