package com.hazem.googlegemini.core.usecase

import com.google.ai.client.generativeai.GenerativeModel
import com.hazem.googlegemini.BuildConfig
import com.hazem.googlegemini.ResultData

class GenerateTextFromTextOnlyInputImp : GenerateTextFromTextOnlyInput {
    override suspend fun invoke(inputText: String): ResultData<String> {
        return try {
            val generativeModel = GenerativeModel(
                // For text-only input, use the gemini-pro model
                modelName = "gemini-pro",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.apiKey
            )
            ResultData.Success(
                generativeModel.generateContent(inputText).text ?: ""
            )

        } catch (e: Exception) {
            ResultData.Error("Exception message: ${e.message ?: "Gemini Error"}")
        }

    }
}