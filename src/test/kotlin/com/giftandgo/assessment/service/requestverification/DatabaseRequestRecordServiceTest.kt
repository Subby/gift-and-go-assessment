package com.giftandgo.assessment.service.requestverification

import com.giftandgo.assessment.model.requestverification.RequestRecord
import com.giftandgo.assessment.repository.RequestRecordRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DatabaseRequestRecordServiceTest {
    private val requestRecordRepository = mockk<RequestRecordRepository>()

    @Test
    fun `recordRequest should call repository`() {
        // every { requestRecordRepository.save(any()) }
        val requestRecord =
            RequestRecord(
                id = null,
                uri = "/h",
                timeStamp = LocalDateTime.now(),
                responseCode = 200,
                requestIP = "127.0.0.1",
                requestISP = "Sky",
                countryCode = "GB",
                timeLapsed = 2,
            )

        every { requestRecordRepository.save(requestRecord) } returns requestRecord

        DatabaseRequestRecordService(requestRecordRepository = requestRecordRepository).recordRequest(
            requestRecord,
        )

        verify { requestRecordRepository.save(requestRecord) }
    }
}
