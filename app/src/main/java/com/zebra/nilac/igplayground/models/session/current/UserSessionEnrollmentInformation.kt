package com.zebra.nilac.igplayground.models.session.current

import org.json.JSONObject

data class UserSessionEnrollmentInformation(
    val enrollmentCreatedOn: String,
    val enrollmentCreatedOnDeviceModel: String,
    val enrollmentCreatedOnDeviceSerialNo: String,
    val enrollmentExpiryDate: String,
    val enrollmentId: String,
    val enrollmentType: String,
    val securityTypes: String,
    val storageType: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionEnrollmentInformation(
            enrollmentCreatedOn = obj.optString("enrollmentCreatedOn", ""),
            enrollmentCreatedOnDeviceModel = obj.optString("enrollmentCreatedOnDeviceModel", ""),
            enrollmentCreatedOnDeviceSerialNo = obj.optString("enrollmentCreatedOnDeviceSerialNo", ""),
            enrollmentExpiryDate = obj.optString("enrollmentExpiryDate", ""),
            enrollmentId = obj.optString("enrollmentId", ""),
            enrollmentType = obj.optString("enrollmentType", ""),
            securityTypes = obj.optString("securityTypes", ""),
            storageType = obj.optString("storageType", "")
        )
    }
}