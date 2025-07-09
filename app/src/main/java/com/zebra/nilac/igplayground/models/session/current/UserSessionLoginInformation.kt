package com.zebra.nilac.igplayground.models.session.current

import org.json.JSONObject

data class UserSessionLoginInformation(
    val userLoginTime: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionLoginInformation(
            userLoginTime = obj.optString("userLoginTime", "")
        )
    }
}