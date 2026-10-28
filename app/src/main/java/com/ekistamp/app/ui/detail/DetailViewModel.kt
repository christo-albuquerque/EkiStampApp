package com.ekistamp.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekistamp.app.data.repository.EkiStampRepository
import com.ekistamp.app.domain.model.EkiStamp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DetailViewModel(repository: EkiStampRepository, id: String) : ViewModel() {

    val stamp: StateFlow<EkiStamp?> = repository.observeStamp(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
