package com.numq.androidwebrtc.platform.di

import android.content.Context
import com.numq.androidwebrtc.service.peer.PeerApi
import com.numq.androidwebrtc.service.peer.PeerService
import com.numq.androidwebrtc.service.rtc.RtcApi
import com.numq.androidwebrtc.service.rtc.RtcMediaApi
import com.numq.androidwebrtc.service.rtc.RtcMediaService
import com.numq.androidwebrtc.service.rtc.RtcService
import com.numq.androidwebrtc.service.signaling.SignalingApi
import com.numq.androidwebrtc.service.signaling.SignalingService
import com.numq.androidwebrtc.service.socket.SocketApi
import com.numq.androidwebrtc.service.socket.SocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.webrtc.EglBase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSocketService(socketService: SocketService): SocketApi = socketService

    @Provides
    @Singleton
    fun providePeerService(
        @ApplicationContext context: Context,
        eglContext: EglBase.Context
    ): PeerApi =
        PeerService(context, eglContext)

    @Provides
    @Singleton
    fun provideSignalingService(socket: SocketApi): SignalingApi =
        SignalingService(socket)

    @Provides
    @Singleton
    fun provideRtcService(
        socket: SocketApi,
        signaling: SignalingApi,
        peer: PeerApi
    ): RtcApi = RtcService(socket, signaling, peer)

    @Provides
    @Singleton
    fun provideRtcMediaService(
        @ApplicationContext context: Context,
        eglContext: EglBase.Context,
        peer: PeerApi
    ): RtcMediaApi = RtcMediaService(context, eglContext, peer)

    @Provides
    @Singleton
    fun provideEglContext(): EglBase.Context = EglBase.create().eglBaseContext

}