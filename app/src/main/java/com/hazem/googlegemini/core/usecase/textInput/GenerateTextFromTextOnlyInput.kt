package com.hazem.googlegemini.core.usecase.textInput

import com.hazem.googlegemini.ResultData

interface GenerateTextFromTextOnlyInput {
    suspend operator fun invoke(inputText:String): ResultData<String>
}