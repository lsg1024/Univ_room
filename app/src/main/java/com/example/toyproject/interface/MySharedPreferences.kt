package com.example.toyproject.`interface`

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {

    private val MY_ACCOUNT : String = "account"

    fun setUserKey(context: Context, input : Int) {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("user_key", input.toString())
        editor.commit()
    }

    fun getUserKey(context: Context) : String {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("user_key", "").toString()
    }

    fun removeKey(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear().commit()
    }
}

