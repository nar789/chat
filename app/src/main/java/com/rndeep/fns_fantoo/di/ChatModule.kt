package com.rndeep.fns_fantoo.di

import android.app.Application
import android.content.ContentResolver
import com.rndeep.fns_fantoo.data.remote.api.ChatService
import com.rndeep.fns_fantoo.data.remote.api.TranslateService
import com.rndeep.fns_fantoo.data.remote.socket.ChatSocketManager
import com.rndeep.fns_fantoo.repositories.ChatInfoRepository
import com.rndeep.fns_fantoo.repositories.ChatRepository
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
    fun provideChatRepository(
        application: Application,
        chatSocketManager: ChatSocketManager,
        contentResolver: ContentResolver,
        @NetworkModule.TranslateServer translateService: TranslateService
    ): ChatRepository =
        ChatRepository(application, chatSocketManager, contentResolver, translateService)

    @Singleton
    @Provides
    fun provideUserRepository(
        @NetworkModule.ApiServer chatService: ChatService
    ): ChatInfoRepository = ChatInfoRepository(chatService)
}