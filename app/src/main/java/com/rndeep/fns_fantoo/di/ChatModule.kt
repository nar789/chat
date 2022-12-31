package com.rndeep.fns_fantoo.di

import android.content.ContentResolver
import com.rndeep.fns_fantoo.data.remote.api.ChatService
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.ChatInfoRepository
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

    @Singleton
    @Provides
    fun provideChatRepository(chatSocketManager: ChatSocketManager, contentResolver: ContentResolver): ChatRepository =
        ChatRepository(chatSocketManager, contentResolver)

    @Singleton
    @Provides
    fun provideUserRepository(
        @NetworkModule.ApiServer chatService: ChatService
    ): ChatInfoRepository = ChatInfoRepository(chatService)
}