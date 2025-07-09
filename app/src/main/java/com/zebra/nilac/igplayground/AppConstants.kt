package com.zebra.nilac.igplayground

object AppConstants {

    const val BASE_URI =
        "content://com.zebra.mdna.els.provider/"

    const val CURRENT_SESSION_URI_LEGACY =
        "content://com.zebra.mdna.els.provider/currentsession"

    const val CURRENT_SESSION_URI =
        "content://com.zebra.mdna.els.provider/v2/currentsession"

    const val PREVIOUS_SESSION_URI =
        "content://com.zebra.mdna.els.provider/previoussession"
    const val STATUS_AUTHENTICATION_URI =
        "content://com.zebra.mdna.els.provider/lockscreenaction/authenticationstatus"
    const val STATUS_URI =
        "content://com.zebra.mdna.els.provider/lockscreenstatus/state"

    const val LOCKSCREEN_ACTION =
        "lockscreenaction"
    const val LOCKSCREEN_STATUS_ACTION =
        "lockscreenstatus"

    const val START_AUTHENTICATION_METHOD =
        "startauthentication"
    const val LOGOUT_METHOD =
        "logout"
    const val LOCKSCREEN_STATUS_STATE_METHOD =
        "state"
    const val LOCKSCREEN_SET_MESSAGE_METHOD =
        "showmessage"
}