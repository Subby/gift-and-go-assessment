package com.giftandgo.assessment.service.requestverification

import com.giftandgo.assessment.model.requestverification.RequestRecord
import com.giftandgo.assessment.repository.RequestRecordRepository
import org.springframework.stereotype.Service

@Service
class DatabaseRequestRecordService(val requestRecordRepository: RequestRecordRepository): RequestRecordService {
    override fun recordRequest(requestRecord: RequestRecord) {
        requestRecordRepository.save(requestRecord)
    }
}
