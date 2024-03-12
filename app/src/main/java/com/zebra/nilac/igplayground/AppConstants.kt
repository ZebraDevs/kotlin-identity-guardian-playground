package com.zebra.nilac.igplayground

object AppConstants {

    const val BASE_URI =
        "content://com.zebra.mdna.els.provider/"

    const val CURRENT_SESSION_URI =
        "content://com.zebra.mdna.els.provider/currentsession"
    const val PREVIOUS_SESSION_URI =
        "content://com.zebra.mdna.els.provider/previoussession"
    const val STATUS_AUTHENTICATION_URI =
        "content://com.zebra.mdna.els.provider/lockscreenaction/authenticationstatus"

    const val LOCKSCREEN_ACTION =
        "lockscreenaction"
    const val START_AUTHENTICATION_METHOD =
        "startauthentication"
    const val LOGOUT_METHOD =
        "logout"
}