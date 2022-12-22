package com.rndeep.fns_fantoo.di

import com.rndeep.fns_fantoo.utils.ChatSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ChatModule {

    @Singleton
    @Provides
    fun provideSocketManager(): ChatSocketManager = ChatSocketManager()
}