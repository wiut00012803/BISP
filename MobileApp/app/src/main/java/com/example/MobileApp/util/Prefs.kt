package com.example.MobileApp.util

import android.content.Context

object Prefs {
    private const val NAME = "prefs"
    private const val KEY_TOKEN = "key_token"
    fun saveToken(ctx: Context, token: String) {
        ctx.getSharedPreferences(NAME, 0).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(ctx: Context): String? {
        return ctx.getSharedPreferences(NAME, 0).getString(KEY_TOKEN, "")
    }

    fun clear(ctx: Context) {
        ctx.getSharedPreferences(NAME, 0).edit().clear().apply()
    }
}