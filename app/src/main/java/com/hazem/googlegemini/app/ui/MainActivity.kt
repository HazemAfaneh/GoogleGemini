package com.hazem.googlegemini.app.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hazem.googlegemini.toBitmap
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
                            ?: "",
                        {
                            viewModel.actionTrigger(
                                MainViewModel.UIAction.GenerateTextFromTextOnlyInput(
                                    it
                                )
                            )
                        }, { text, list ->
                            viewModel.actionTrigger(
                                MainViewModel.UIAction.GenerateTextFromTextAndImageInput(
                                    text,
                                    list,
                                    this
                                )
                            )
                        })
                }
            }
        }
    }
}

@Composable
fun GeminiComposable(
    isLoading: Boolean,
    geminiResult: String,
    changeInputText: (String) -> Unit,
    changeInputTextWithImages: (String, List<Uri?>) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ProgressBar(visibility = isLoading)
        SendToGeminiComposable(
            geminiResult,
            changeInputText = changeInputText,
            changeInputTextWithImages
        )
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
    changeInputText: (String) -> Unit,
    changeInputTextWithImage: (String, List<Uri?>) -> Unit
) {
    var inputText by remember { mutableStateOf("What is SOLID principles") }
    var selectedImages by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
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
            modifier = Modifier
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        PhotoSelectorView(selectedImages, maxSelectionCount = 2) {
            selectedImages = it
        }
        Button(
            onClick = {
                if (selectedImages.isEmpty())
                    changeInputText(inputText)
                else
                    changeInputTextWithImage(inputText, selectedImages)
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(6.dp)
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


@Composable
fun PhotoSelectorView(
    selectedImages: List<Uri?>,
    maxSelectionCount: Int = 1,
    onImageSelected: (List<Uri?>) -> Unit
) {


    val buttonText = "Select Images"
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImageSelected(listOf(uri)) }
    )

    // I will start this off by saying that I am still learning Android development:
    // We are tricking the multiple photos picker here which is probably not the best way,
    // if you know of a better way to implement this feature drop a comment and let me know
    // how to improve this design
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = if (maxSelectionCount > 1) {
                maxSelectionCount
            } else {
                2
            }
        ),
        onResult = { uris -> onImageSelected(uris) }
    )

    fun launchPhotoPicker() {
        if (maxSelectionCount > 1) {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }


    Button(
        onClick = {
            launchPhotoPicker()
        },
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(buttonText)
    }

    ImageLayoutView(selectedImages = selectedImages)

}

@Composable
fun ImageLayoutView(selectedImages: List<Uri?>) {
    LazyRow {
        items(selectedImages.size) { index ->
            AsyncImage(
                model = selectedImages[index],
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .padding(start = 4.dp, end = 4.dp),
                contentScale = ContentScale.Crop
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeminiComposablePreview() {
    GoogleGeminiTheme {
        GeminiComposable(false, "Android", {

        }, { _, _ ->

        })
    }
}