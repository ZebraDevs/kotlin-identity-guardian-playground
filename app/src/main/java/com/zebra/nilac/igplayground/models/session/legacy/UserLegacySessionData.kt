package com.zebra.nilac.igplayground.models.session.legacy

import com.zebra.nilac.igplayground.models.session.SessionAuthenticationFactors
import org.json.JSONObject

data class UserLegacySessionData(
    val userId: String,
    val userRole: String,
    val userLoggedInState: String,
    val userLoginTime: String,
    val userLogoutTime: String,
    val logoutReason: String,
    val deviceModel: String,
    val storageType: String,
    val securityTypes: String,
    val barcodeId: String,
    val barcodeCreatedOn: String,
    val barcodeExpiryDate: String,
    val barcodeCreatedOnDeviceModel: String,
    val barcodeCreatedOnDeviceSerialNo: String,
    val barcodeAuthenticatedOnDeviceSerialNo: String,
    val ssoAccessToken: String,
    val ssoProvider: String,
    val authenticationFactors: SessionAuthenticationFactors,
    val ssoData: String
) {
    companion object {
        fun fromJson(obj: JSONObject): UserLegacySessionData {
            val authFactorsJson = obj.optString("authenticationFactors", "{}")

            return UserLegacySessionData(
                userId = obj.optString("userId", ""),
                userRole = obj.optString("userRole", ""),
                userLoggedInState = obj.optString("userLoggedInState", ""),
                userLoginTime = obj.optString("userLoginTime", ""),
                userLogoutTime = obj.optString("userLogoutTime", ""),
                logoutReason = obj.optString("logoutReason", ""),
                deviceModel = obj.optString("deviceModel", ""),
                storageType = obj.optString("storageType", ""),
                securityTypes = obj.optString("securityTypes", ""),
                barcodeId = obj.optString("barcodeId", ""),
                barcodeCreatedOn = obj.optString("barcodeCreatedOn", ""),
                barcodeExpiryDate = obj.optString("barcodeExpiryDate", ""),
                barcodeCreatedOnDeviceModel = obj.optString("barcodeCreatedOnDeviceModel", ""),
                barcodeCreatedOnDeviceSerialNo = obj.optString("barcodeCreatedOnDeviceSerialNo", ""),
                barcodeAuthenticatedOnDeviceSerialNo = obj.optString("barcodeAuthenticatedOnDeviceSerialNo", ""),
                ssoAccessToken = obj.optString("ssoAccessToken", ""),
                ssoProvider = obj.optString("ssoProvider", ""),
                authenticationFactors = SessionAuthenticationFactors.fromJson(JSONObject(authFactorsJson)),
                ssoData = obj.optString("ssoData", "")
            )
        }
    }
}