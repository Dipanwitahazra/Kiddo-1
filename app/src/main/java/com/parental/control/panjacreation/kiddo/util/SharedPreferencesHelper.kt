package com.parental.control.panjacreation.kiddo.util

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {

    private const val PREFS_NAME = Constants.PREFERENCE_FILE_KEY
    private const val HASHSET_KEY = "restrictedPackageSet"

    fun saveHashSet(context: Context, set: HashSet<String>?) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putStringSet(HASHSET_KEY, set)
        editor.apply()
    }

    fun getHashSet(context: Context): HashSet<String> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(HASHSET_KEY, HashSet<String>()) as HashSet<String>
    }

    fun saveString(context: Context, key: String, value: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String, default: String? = null): String?{
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, default)
    }

    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String, default: Boolean = true): Boolean{
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, default)
    }
}