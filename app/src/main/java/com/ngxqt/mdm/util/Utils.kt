package com.ngxqt.mdm.util

internal fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

internal fun isUrlValid(url: String): Boolean {
    val regex = Regex("^(http|https)://[\\w.-]+\\.com$")
    return regex.matches(url)
}