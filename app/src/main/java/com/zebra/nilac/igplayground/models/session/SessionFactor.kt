package com.zebra.nilac.igplayground.models.session

import org.json.JSONObject

data class SessionFactor(
    val factor: String,
    val factorType: String,
    val status: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = SessionFactor(
            factor = obj.optString("factor", ""),
            factorType = obj.optString("factorType", ""),
            status = obj.optString("status", "")
        )
    }
}