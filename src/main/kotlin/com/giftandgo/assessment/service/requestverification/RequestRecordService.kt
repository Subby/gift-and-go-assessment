package com.giftandgo.assessment.service.requestverification

import com.giftandgo.assessment.model.requestverification.RequestRecord

interface RequestRecordService {
    fun recordRequest(requestRecord: RequestRecord)
}
