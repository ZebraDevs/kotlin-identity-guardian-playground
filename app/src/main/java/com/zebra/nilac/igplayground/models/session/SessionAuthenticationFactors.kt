package com.zebra.nilac.igplayground.models.session

import org.json.JSONArray
import org.json.JSONObject

data class SessionAuthenticationFactors(
    val adminByPassFactors: List<SessionFactor>,
    val alternateFactors: List<SessionFactor>,
    val factors: List<SessionFactor>,
    val schemaVersion: String
) {
    companion object {
        fun fromJson(obj: JSONObject) = SessionAuthenticationFactors(
            adminByPassFactors = obj.optJSONArray("adminByPassFactors")?.toFactorList()
                ?: emptyList(),
            alternateFactors = obj.optJSONArray("alternateFactors")?.toFactorList() ?: emptyList(),
            factors = obj.optJSONArray("factors")?.toFactorList() ?: emptyList(),
            schemaVersion = obj.optString("schemaVersion", "")
        )
    }
}
private fun JSONArray.toFactorList(): List<SessionFactor> {
    return List(length()) { i -> SessionFactor.fromJson(getJSONObject(i)) }
}