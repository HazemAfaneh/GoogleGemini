package com.hazem.googlegemini.di

import com.hazem.googlegemini.core.usecase.textAndImageInput.GenerateTextFromTextAndImageInput
import com.hazem.googlegemini.core.usecase.textAndImageInput.GenerateTextFromTextAndImageInputImp
import com.hazem.googlegemini.core.usecase.textInput.GenerateTextFromTextOnlyInput
import com.hazem.googlegemini.core.usecase.textInput.GenerateTextFromTextOnlyInputImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UseCaseModule {
    @Singleton
    @Provides
    fun provideGenerateTextFromTextOnlyInput(): GenerateTextFromTextOnlyInput =
        GenerateTextFromTextOnlyInputImp()
    @Singleton
    @Provides
    fun provideGenerateTextFromTextAndImageInput(): GenerateTextFromTextAndImageInput =
        GenerateTextFromTextAndImageInputImp()
}