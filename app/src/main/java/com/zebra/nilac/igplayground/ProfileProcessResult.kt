package com.zebra.nilac.igplayground

data class ProfileProcessResult(

    var status: Status,

    var result: Boolean
) {
    enum class Status {
        SUCCESS, FAILED
    }
}