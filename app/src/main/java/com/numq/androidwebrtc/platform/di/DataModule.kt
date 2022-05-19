package com.numq.androidwebrtc.platform.di

import com.numq.androidwebrtc.data.session.SessionData
import com.numq.androidwebrtc.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideSessionRepository(repository: SessionData): SessionRepository = repository

}