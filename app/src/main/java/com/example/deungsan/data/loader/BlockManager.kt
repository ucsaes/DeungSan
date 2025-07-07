package com.example.deungsan.data.loader

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "reports")

object ReportManager {
    private val REPORT_KEY = stringSetPreferencesKey("reported_ids")

    fun getReports(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { prefs ->
            prefs[REPORT_KEY] ?: emptySet()
        }
    }

    suspend fun addReport(context: Context, id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[REPORT_KEY] ?: emptySet()
            prefs[REPORT_KEY] = current + id
        }
    }

    suspend fun removeReport(context: Context, id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[REPORT_KEY] ?: emptySet()
            prefs[REPORT_KEY] = current - id
        }
    }

    fun isReported(reports: Set<String>, id: String): Boolean {
        return id in reports
    }
}

class ReportViewModel : ViewModel() {
    private val _reported = MutableStateFlow<Set<String>>(emptySet())
    val reported: StateFlow<Set<String>> = _reported

    fun loadReports(context: Context) {
        viewModelScope.launch {
            ReportManager.getReports(context).collect {
                _reported.value = it
            }
        }
    }

    fun addReport(context: Context, id: String) {
        viewModelScope.launch {
            ReportManager.addReport(context, id)
        }
    }

    fun removeReport(context: Context, id: String) {
        viewModelScope.launch {
            ReportManager.removeReport(context, id)
        }
    }
}

