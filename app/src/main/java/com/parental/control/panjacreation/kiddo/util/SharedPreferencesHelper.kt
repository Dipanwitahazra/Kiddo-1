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
}