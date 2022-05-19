package com.numq.androidwebrtc.service.rtc

import android.content.Context
import com.numq.androidwebrtc.service.peer.PeerApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import org.webrtc.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RtcMediaService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eglContext: EglBase.Context,
    private val peer: PeerApi
) : RtcMediaApi, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    companion object {
        const val THREAD = "media_thread"
    }

    private val localAudioSource by lazy { peer.createLocalAudioSource(peer.constraints) }
    private val localVideoSource by lazy { peer.createLocalVideoSource(false) }
    private val localAudioTrack: AudioTrack by lazy { peer.createLocalAudioTrack(localAudioSource) }
    private val localVideoTrack: VideoTrack by lazy { peer.createLocalVideoTrack(localVideoSource) }
    private var videoCapturer: CameraVideoCapturer? = null

    private fun createCapturer(context: Context) = with(Camera2Enumerator(context)) {
        deviceNames.find {
            isFrontFacing(it)
        }?.let {
            createCapturer(it, null)
        }
    }

    private fun initSurfaceView(eglContext: EglBase.Context, renderer: SurfaceViewRenderer) =
        with(renderer) {
            setMirror(true)
            setEnableHardwareScaler(true)
            init(eglContext, null)
        }

    private fun startLocalVideoCapture(
        eglContext: EglBase.Context,
        localView: SurfaceViewRenderer
    ) {
        val surfaceTextureHelper =
            SurfaceTextureHelper.create(THREAD, eglContext)
        videoCapturer = createCapturer(context)?.apply {
            initialize(
                surfaceTextureHelper,
                localView.context,
                localVideoSource.capturerObserver
            )
            startCapture(1280, 720, 30)
        }
        localVideoTrack.addSink(localView)
        peer.addStream(peer.createLocalStream(localAudioTrack, localVideoTrack))
    }

    override fun initLocalView(view: SurfaceViewRenderer) {
        initSurfaceView(eglContext, view)
        startLocalVideoCapture(eglContext, view)
    }

    override fun initRemoteView(view: SurfaceViewRenderer) {
        initSurfaceView(eglContext, view)
        launch {
            peer.stream.receiveAsFlow().collect {
                it.videoTracks.firstOrNull()?.addSink(view)
            }
        }
    }

    override fun dispose() {
        videoCapturer?.dispose()
        localAudioSource.dispose()
        localVideoSource.dispose()
        coroutineContext.cancel()
        videoCapturer = null
    }

    override fun toggleAudio(state: Boolean) {
        localAudioTrack.setEnabled(state)
    }

    override fun toggleVideo(state: Boolean) {
        localVideoTrack.setEnabled(state)
    }

    override fun switchCamera() {
        videoCapturer?.switchCamera(null)
    }
}