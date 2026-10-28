package com.ekistamp.app.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekistamp.app.data.repository.EkiStampRepository
import com.ekistamp.app.domain.model.EkiStamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUiState(
    val stamps: List<EkiStamp> = emptyList(),
    val prefectures: List<String> = emptyList(),
    val query: String = "",
    val selectedPrefecture: String? = null,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModel(private val repository: EkiStampRepository) : ViewModel() {

    private val query = MutableStateFlow("")
    private val prefecture = MutableStateFlow<String?>(null)
    private val status = MutableStateFlow(Pair(false, null as String?))

    private val allStamps = repository.observeStamps()
    private val filtered = query.flatMapLatest { repository.observeStamps(it) }

    val uiState: StateFlow<CatalogUiState> = combine(
        filtered, allStamps, query, prefecture, status
    ) { stamps, all, q, pref, (refreshing, error) ->
        CatalogUiState(
            stamps = if (pref == null) stamps else stamps.filter { it.prefecture == pref },
            prefectures = all.map { it.prefecture }.filter { it.isNotBlank() }.distinct().sorted(),
            query = q,
            selectedPrefecture = pref,
            isRefreshing = refreshing,
            errorMessage = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState())

    init {
        refresh()
    }

    fun onQueryChange(value: String) = query.update { value }

    fun onPrefectureSelected(value: String?) = prefecture.update { if (it == value) null else value }

    fun refresh() {
        viewModelScope.launch {
            status.value = true to null
            val result = repository.refresh()
            status.value = false to result.exceptionOrNull()?.let { "Couldn't update stamps. Check your connection." }
        }
    }
}
