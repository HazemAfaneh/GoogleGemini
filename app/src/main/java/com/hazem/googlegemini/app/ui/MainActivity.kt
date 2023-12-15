package com.hazem.googlegemini.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hazem.googlegemini.ui.theme.GoogleGeminiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state = viewModel.uiState.collectAsState()
            GoogleGeminiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeminiComposable(
                        state.value.loading,
                        state.value.generateTextFromTextOnlyInputText
                            ?: ""
                    ) {
                        viewModel.actionTrigger(
                            MainViewModel.UIAction.GenerateTextFromTextOnlyInput(
                                it
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GeminiComposable(
    isLoading: Boolean,
    geminiResult: String,
    changeInputText: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ProgressBar(visibility = isLoading)
        SendToGeminiComposable(geminiResult, changeInputText = changeInputText)
    }
}

@Composable
fun ProgressBar(
    visibility: Boolean
) {
    if (visibility) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendToGeminiComposable(
    geminiResult: String,
    changeInputText: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("What is SOLID principles") }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = {
                inputText = it
            },
            label = { Text("Input value") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                changeInputText(inputText)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Send to Google Gemini")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = ScrollState(0))
        ) {
            Text(text = geminiResult)

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GeminiComposablePreview() {
    GoogleGeminiTheme {
        GeminiComposable(false, "Android") {

        }
    }
}