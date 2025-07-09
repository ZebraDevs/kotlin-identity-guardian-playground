package com.zebra.nilac.igplayground.models.session.current

import com.zebra.nilac.igplayground.models.session.SessionAuthenticationFactors
import com.zebra.nilac.igplayground.models.session.SessionFactor
import org.json.JSONObject

data class UserSessionLoginResponse(
    val authenticationFactors: SessionAuthenticationFactors,
    val enrollmentInformation: UserSessionEnrollmentInformation,
    val logOutInformation: UserSessionLogOutInformation,
    val loginInformation: UserSessionLoginInformation,
    val ssoInformation: UserSessionSSOInformation,
    val userInformation: UserSessionInformation,
    val schemaVersion: String,
    val errorCode: Int,
    val eventType: String,
    val lockScreenEventType: String,
    val isSSOInformationAvailable: Int,
    val status: String,
    val userLoggedInState: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionLoginResponse(
            authenticationFactors = SessionAuthenticationFactors.fromJson(
                obj.optJSONObject("authenticationFactors") ?: JSONObject()
            ),
            enrollmentInformation = UserSessionEnrollmentInformation.fromJson(
                obj.optJSONObject("enrollmentInformation") ?: JSONObject()
            ),
            logOutInformation = UserSessionLogOutInformation.fromJson(
                obj.optJSONObject("logOutInformation") ?: JSONObject()
            ),
            loginInformation = UserSessionLoginInformation.fromJson(
                obj.optJSONObject("loginInformation") ?: JSONObject()
            ),
            ssoInformation = UserSessionSSOInformation.fromJson(
                obj.optJSONObject("ssoInformation") ?: JSONObject()
            ),
            userInformation = UserSessionInformation.fromJson(
                obj.optJSONObject("userInformation") ?: JSONObject()
            ),
            schemaVersion = obj.optString("schemaVersion", ""),
            errorCode = obj.optInt("errorCode", -1),
            eventType = obj.optString("eventType", ""),
            lockScreenEventType = obj.optString("lockScreenEventType", ""),
            isSSOInformationAvailable = obj.optInt("isSSOInformationAvailable", 0),
            status = obj.optString("status", ""),
            userLoggedInState = obj.optString("userLoggedInState", "")
        )
    }

    override fun toString(): String {
        return buildString {
            appendLine("Current User Session: \n")

            fun field(label: String, value: String) {
                appendLine("  $label:")
                appendLine("    $value")
                appendLine()
            }

            // General
            field("Status", status)
            field("Event Type", eventType)
            field("Error Code", errorCode.toString())
            field("Logged In State", userLoggedInState)

            // User Info
            field("User ID", userInformation.userId)
            field("Role", userInformation.userRole.ifEmpty { "(not assigned)" })

            // Login Info
            field("Login Time", loginInformation.userLoginTime)

            // Logout Info
            field("Logout Reason", logOutInformation.logoutReason.ifEmpty { "--" })
            field("User Logout Time", logOutInformation.userLogoutTime.ifEmpty { "--" })

            // Enrollment Info
            field("Enrollment ID", enrollmentInformation.enrollmentId)
            field("Enrollment Type", enrollmentInformation.enrollmentType)
            field("Enrollment Created On", enrollmentInformation.enrollmentCreatedOn)
            field("Enrollment Expiry", enrollmentInformation.enrollmentExpiryDate)
            field("Enrollment Device Model", enrollmentInformation.enrollmentCreatedOnDeviceModel)
            field("Enrollment Device Serial No", enrollmentInformation.enrollmentCreatedOnDeviceSerialNo)

            field("Security Types", enrollmentInformation.securityTypes)
            field("Storage Type", enrollmentInformation.storageType)

            // SSO Info
            if (isSSOInformationAvailable == 1) {
                field("SSO Provider", ssoInformation.ssoProvider)
                field("SSO Access Token", ssoInformation.ssoAccessToken)
                field("SSO ID Token", ssoInformation.ssoIDToken)
                field("SSO Data from IDP", ssoInformation.ssoDataReceivedFromIDP)
            }

            // Authentication Factors
            fun List<SessionFactor>.formatted(name: String) {
                if (isNotEmpty()) {
                    appendLine("  $name:")
                    forEach { f ->
                        appendLine("    - Factor     : ${f.factor}")
                        appendLine("      Type       : ${f.factorType}")
                        appendLine("      Status     : ${f.status}")
                        appendLine()
                    }
                }
            }

            authenticationFactors.factors.formatted("Primary Factors")
            authenticationFactors.alternateFactors.formatted("Alternate Factors")
            authenticationFactors.adminByPassFactors.formatted("Admin Bypass Factors")
        }
    }
}