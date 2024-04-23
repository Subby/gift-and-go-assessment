package com.giftandgo.assessment.model.fileprocessing

import org.springframework.core.io.InputStreamResource

private const val DEFAULT_PROCESSED_FILE_NAME = "OutcomeFile.json"

sealed interface FileProcessResult

data class FileProcessSuccess(val fileName: String = DEFAULT_PROCESSED_FILE_NAME, val inputStream: InputStreamResource):
    FileProcessResult

data class FileProcessError(val errors: List<String>): FileProcessResult
