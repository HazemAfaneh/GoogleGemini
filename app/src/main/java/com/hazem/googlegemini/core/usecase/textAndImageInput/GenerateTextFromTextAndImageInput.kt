package com.hazem.googlegemini.core.usecase.textAndImageInput

import android.graphics.Bitmap
import com.hazem.googlegemini.app.ResultData

interface GenerateTextFromTextAndImageInput {
    suspend operator fun invoke(inputText:String, images:List<Bitmap?>): ResultData<String>
}