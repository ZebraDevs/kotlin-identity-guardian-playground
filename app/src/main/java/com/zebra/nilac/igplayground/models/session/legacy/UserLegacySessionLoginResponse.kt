package com.zebra.nilac.igplayground.models.session.legacy

import com.zebra.nilac.igplayground.models.session.SessionFactor
import org.json.JSONObject

data class UserLegacySessionLoginResponse(
    val code: Int,
    val status: String,
    val userData: UserLegacySessionData
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserLegacySessionLoginResponse(
            code = obj.optInt("code", -1),
            status = obj.optString("status", ""),
            userData = UserLegacySessionData.fromJson(obj.optJSONObject("userData") ?: JSONObject())
        )
    }

    override fun toString(): String {
        return buildString {
            appendLine("Response: \n")

            fun field(label: String, value: String) {
                appendLine("  $label:")
                appendLine("    $value")
                appendLine()
            }

            field("Code", code.toString())
            field("Status", status)

            with(userData) {
                field("User ID", userId)
                field("Role", userRole.ifEmpty { "(not assigned)" })
                field("Logged In State", userLoggedInState)
                field("Login Time", userLoginTime)
                field("Logout Reason", logoutReason.ifEmpty { "--" })
                field("Logout Time", userLogoutTime.ifEmpty { "--" })
                field("Device Model", deviceModel)
                field("Storage Type", storageType)
                field("Security Types", securityTypes)

                field("Barcode ID", barcodeId)
                field("Barcode Created On", barcodeCreatedOn)
                field("Barcode Expiry Date", barcodeExpiryDate)
                field("Barcode Creation Device Model", barcodeCreatedOnDeviceModel)
                field("Barcode Serial No", barcodeCreatedOnDeviceSerialNo)
                field("Authenticated On Serial", barcodeAuthenticatedOnDeviceSerialNo)

                field("SSO Provider", ssoProvider)
                field("SSO Access Token", ssoAccessToken)
                field("SSO Data (raw)", ssoData)
            }

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

            userData.authenticationFactors.factors.formatted("Authentication Factors")
        }
    }
}
