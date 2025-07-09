package com.zebra.nilac.igplayground.models.session

import org.json.JSONObject

data class UserSessionSSOInformation(
    val ssoAccessToken: String,
    val ssoDataReceivedFromIDP: String,
    val ssoIDToken: String,
    val ssoProvider: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionSSOInformation(
            ssoAccessToken = obj.optString("ssoAccessToken", ""),
            ssoDataReceivedFromIDP = obj.optString("ssoDataReceivedFromIDP", ""),
            ssoIDToken = obj.optString("ssoIDToken", ""),
            ssoProvider = obj.optString("ssoProvider", "")
        )
    }
}