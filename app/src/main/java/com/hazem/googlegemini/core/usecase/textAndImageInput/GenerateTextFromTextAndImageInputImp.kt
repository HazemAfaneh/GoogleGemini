package com.hazem.googlegemini.core.usecase.textAndImageInput

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.hazem.googlegemini.BuildConfig
import com.hazem.googlegemini.ResultData

class GenerateTextFromTextAndImageInputImp:GenerateTextFromTextAndImageInput {
    override suspend fun invoke(inputText: String, images: List<Bitmap?>) =
        try {
            val generativeModel = GenerativeModel(
                // For text-and-images input (multimodal), use the gemini-pro-vision model
                modelName = "gemini-pro-vision",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.apiKey
            )
            val inputContent = content {
                images.forEach {
                    it?.let {
                        image(it)
                    }
                }
                text(inputText)
            }
            val response = generativeModel.generateContent(inputContent)
            ResultData.Success(
                response.text ?: ""
            )

        } catch (e: Exception) {
            ResultData.Error("Exception message: ${e.message ?: "Gemini Error"}")
        }
}