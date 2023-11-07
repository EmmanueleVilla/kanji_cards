package com.shadowings.kanjitrainerita

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("KanjiPrefs", Context.MODE_PRIVATE)

    fun putInt(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun putKanjiIds(value: List<Int>) {
        val editor = sharedPreferences.edit()
        editor.putString("train_ids", Gson().toJson(value))
        editor.apply()
    }

    fun getKanjiIds(defaultValue: List<Int>): List<Int> {
        val json = sharedPreferences.getString("train_ids", null)
        return if (json == null) {
            defaultValue
        } else {
            Gson().fromJson(json, Array<Int>::class.java).toList()
        }
    }

    fun putString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}