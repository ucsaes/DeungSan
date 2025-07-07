package com.example.deungsan.data.loader

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. DataStore 인스턴스 생성
private val Context.dataStore by preferencesDataStore(name = "favorites")

object FavoriteManager {
    private val FAVORITE_KEY = stringSetPreferencesKey("favorite_ids")

    // 2. 즐겨찾기 리스트 불러오기
    fun getFavorites(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { prefs ->
            prefs[FAVORITE_KEY] ?: emptySet()
        }
    }

    // 3. 즐겨찾기 토글
    suspend fun toggleFavorite(context: Context, id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITE_KEY] ?: emptySet()
            prefs[FAVORITE_KEY] = if (id in current) current - id else current + id
        }
    }

    // 4. 즐겨찾기 여부 확인
    fun isFavorite(favorites: Set<String>, id: String): Boolean {
        return id in favorites
    }
}

class MountainViewModel : ViewModel() {
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    fun toggleFavorite(context: Context, id: String) {
        viewModelScope.launch {
            FavoriteManager.toggleFavorite(context, id)
        }
    }

    fun loadFavorites(context: Context) {
        viewModelScope.launch {
            FavoriteManager.getFavorites(context).collect {
                _favorites.value = it
            }
        }
    }
}