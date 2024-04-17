package com.giftandgo.assessment.model

import org.springframework.core.io.InputStreamResource

private const val DEFAULT_PROCESSED_FILE_NAME = "OutcomeFile.json"

//TODO: Different package
data class FileProcessSuccess(val fileName: String = DEFAULT_PROCESSED_FILE_NAME, val inputStream: InputStreamResource): FileProcessResult

data class FileProcessError(val errors: Array<String>): FileProcessResult {
    //TODO: Do we need this?
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileProcessError

        if (!errors.contentEquals(other.errors)) return false

        return true
    }

    override fun hashCode(): Int {
        return errors.contentHashCode()
    }
}

sealed interface FileProcessResult
