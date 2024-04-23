package com.giftandgo.assessment.repository

import com.giftandgo.assessment.model.requestverification.RequestRecord
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface RequestRecordRepository : CrudRepository<RequestRecord, Long>
