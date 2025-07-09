package com.zebra.nilac.igplayground.models.session

import org.json.JSONArray
import org.json.JSONObject

data class UserSessionAuthenticationFactors(
    val adminByPassFactors: List<UserSessionFactor>,
    val alternateFactors: List<UserSessionFactor>,
    val factors: List<UserSessionFactor>,
    val schemaVersion: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = UserSessionAuthenticationFactors(
            adminByPassFactors = obj.optJSONArray("adminByPassFactors")?.toFactorList() ?: emptyList(),
            alternateFactors = obj.optJSONArray("alternateFactors")?.toFactorList() ?: emptyList(),
            factors = obj.optJSONArray("factors")?.toFactorList() ?: emptyList(),
            schemaVersion = obj.optString("schemaVersion", "")
        )
    }
}

private fun JSONArray.toFactorList(): List<UserSessionFactor> {
    return List(length()) { i -> UserSessionFactor.fromJson(getJSONObject(i)) }
}