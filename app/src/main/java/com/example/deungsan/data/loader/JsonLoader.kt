package com.example.deungsan.data.loader

import androidx.compose.foundation.layout.*        // Row, Column, Spacer, Modifier.padding 등
import androidx.compose.material3.*              // Text, MaterialTheme, Button 등
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.deungsan.data.model.Mountain
import com.example.deungsan.data.model.Review


object JsonLoader {
    fun loadMountainsFromAssets(context: Context): List<Mountain> {
        val json = context.assets.open("mountain.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Mountain>>() {}.type
        return gson.fromJson(json, type)
    }
    fun loadReviewsFromAssets(context: Context): List<Review> {
        val json = context.assets.open("reviews.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Review>>() {}.type
        return gson.fromJson(json, type)
    }
}
