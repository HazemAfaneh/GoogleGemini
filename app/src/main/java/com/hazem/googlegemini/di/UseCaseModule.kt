package com.hazem.googlegemini.di

import com.hazem.googlegemini.core.usecase.GenerateTextFromTextOnlyInput
import com.hazem.googlegemini.core.usecase.GenerateTextFromTextOnlyInputImp
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
}