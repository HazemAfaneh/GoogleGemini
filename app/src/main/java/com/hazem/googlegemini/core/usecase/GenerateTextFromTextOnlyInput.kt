package com.hazem.googlegemini.core.usecase

import com.hazem.googlegemini.ResultData

interface GenerateTextFromTextOnlyInput {
    suspend operator fun invoke(inputText:String): ResultData<String>
}