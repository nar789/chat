package com.rndeep.fns_fantoo.di

import com.rndeep.fns_fantoo.data.remote.api.ChatService
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.repositories.ChatUserRepository
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
    fun provideChatRepository(chatSocketManager: ChatSocketManager): ChatRepository =
        ChatRepository(chatSocketManager)

    @Singleton
    @Provides
    fun provideUserRepository(
        @NetworkModule.ChatUserServer chatService: ChatService
    ): ChatUserRepository = ChatUserRepository(chatService)
}