package com.zebra.nilac.igplayground.models.session

import org.json.JSONObject

data class UserSessionFactor(
    val factor: String,
    val factorType: String,
    val status: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionFactor(
            factor = obj.optString("factor", ""),
            factorType = obj.optString("factorType", ""),
            status = obj.optString("status", "")
        )
    }
}