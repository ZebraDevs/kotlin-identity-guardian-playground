package com.zebra.nilac.igplayground.models.session

import org.json.JSONObject

data class UserSessionLogOutInformation(
    val logoutReason: String,
    val userLogoutTime: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionLogOutInformation(
            logoutReason = obj.optString("logoutReason", ""),
            userLogoutTime = obj.optString("userLogoutTime", "")
        )
    }
}