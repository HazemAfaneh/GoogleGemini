package com.hazem.googlegemini.app.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hazem.googlegemini.core.usecase.textAndImageInput.GenerateTextFromTextAndImageInput
import com.hazem.googlegemini.core.usecase.textInput.GenerateTextFromTextOnlyInput
import com.hazem.googlegemini.app.doIfFailure
import com.hazem.googlegemini.app.doIfSuccess
import com.hazem.googlegemini.app.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val generateTextFromTextOnlyInput: GenerateTextFromTextOnlyInput,
    val generateTextFromTextAndImageInput: GenerateTextFromTextAndImageInput
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> get() = _uiState

    data class UiState(
        val loading: Boolean = false,
        val generateTextFromTextOnlyInputText: String? = null
    )

    sealed class UIAction {
        data class GenerateTextFromTextOnlyInput(val inputText: String) : UIAction()
        data class GenerateTextFromTextAndImageInput(
            val inputText: String,
            val images: List<Uri?>,
            val context: Context
        ) : UIAction()
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

                is UIAction.GenerateTextFromTextAndImageInput -> {
                    launch(Dispatchers.IO) {
                        generateTextFromTextAndImageInput(
                            action.inputText,
                            action.images.map { uri ->
                                uri?.toBitmap(action.context)
                            }).doIfSuccess {
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
}