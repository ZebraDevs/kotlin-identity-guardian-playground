package com.zebra.nilac.igplayground.models.session

import org.json.JSONObject

data class UserSessionInformation(
    val userId: String,
    val userRole: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionInformation(
            userId = obj.optString("userId", ""),
            userRole = obj.optString("userRole", "")
        )
    }
}