package com.hazem.googlegemini.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.hazem.googlegemini.BuildConfig
import com.hazem.googlegemini.core.usecase.GenerateTextFromTextOnlyInput
import com.hazem.googlegemini.doIfFailure
import com.hazem.googlegemini.doIfSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val generateTextFromTextOnlyInput: GenerateTextFromTextOnlyInput
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> get() = _uiState

    data class UiState(
        val loading: Boolean = false,
        val generateTextFromTextOnlyInputText: String? = null
    )

    sealed class UIAction {
        data class GenerateTextFromTextOnlyInput(val inputText: String) : UIAction()
    }

    fun actionTrigger(action: UIAction) {
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    loading = true
                )
            )
            when (action) {
                is UIAction.GenerateTextFromTextOnlyInput -> {
                    generateTextFromTextOnlyInput(action.inputText).doIfSuccess {
                        _uiState.emit(
                            uiState.value.copy(
                                loading = false,
                                generateTextFromTextOnlyInputText = it
                            )
                        )
                    }.doIfFailure {
                        _uiState.emit(
                            uiState.value.copy(
                                loading = false,
                                generateTextFromTextOnlyInputText = it
                            )
                        )
                    }
                }
            }

        }

    }
}