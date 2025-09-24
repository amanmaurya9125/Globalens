package com.example.Globalens

import android.content.Context
import com.google.firebase.auth.FirebaseUser

class SharedPreferences() {
    fun saveUserAuth(context: Context, user: FirebaseUser) {
        val prefs = context.getSharedPreferences("user_auth", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("uid", user.uid)
        editor.putString("email", user.email)
        editor.putString("name", user.displayName)
        editor.putString("photo", user.photoUrl?.toString())
            .apply()
    }

    fun getUserAuth(context: Context): Map<String, String?> {
        val prefs = context.getSharedPreferences("user_auth", Context.MODE_PRIVATE)
        return mapOf(
            "uid" to prefs.getString("uid", null),
            "email" to prefs.getString("email", null),
            "name" to prefs.getString("name", null),
            "photo" to prefs.getString("photo", null)
        )

    }

    fun clearUserAuth(context: Context) {
        val prefs = context.getSharedPreferences("user_auth", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}