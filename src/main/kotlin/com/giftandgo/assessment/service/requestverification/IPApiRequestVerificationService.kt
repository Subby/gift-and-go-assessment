package com.giftandgo.assessment.service.requestverification

import com.giftandgo.assessment.client.IPApiClient
import com.giftandgo.assessment.config.ApplicationConfigProps
import com.giftandgo.assessment.model.requestverification.RequestVerificationFailure
import com.giftandgo.assessment.model.requestverification.RequestVerificationResult
import com.giftandgo.assessment.model.requestverification.RequestVerificationSuccess
import org.springframework.stereotype.Service

@Service
//tODO: Make this and other vals private?
class IPApiRequestVerificationService(val ipApiClient: IPApiClient, val applicationConfigProps: ApplicationConfigProps): RequestVerificationService {

    override fun verifyRequestForIp(ip: String): RequestVerificationResult = //TODO: How do we handle errors here?
        ipApiClient.testIp(ip).let { (country, isp, countryCode) ->
            if(applicationConfigProps.ipBlockedIsps.contains(isp)) {
                return RequestVerificationFailure(reason = "Request ISP is blocked")
            } else if(applicationConfigProps.ipBlockedCountries.contains(country)) {
                return RequestVerificationFailure(reason = "Request Country is blocked")
            } else {
                return RequestVerificationSuccess(requestCountryCode = countryCode, requestIsp = isp)
            }
        }
}
