package com.fanap.exoplayertestapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util


class PlayerActivity : AppCompatActivity() ,TransferListener {
    val TAG: String = "EXOPLAYERTEST"
    var player: ExoPlayer? = null
    private var playWhenReady = true
    private var isProgressive = false
    private var currentWindow = 0
    private var playbackPosition = 0L
    lateinit var playerView: PlayerView
    lateinit var filUrl: String
    var movie: Movie? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        movie = intent.getSerializableExtra("movie") as Movie?
        filUrl = movie?.url.toString()
        isProgressive = movie?.streamType.equals("progressive")
    }

    fun initializePlayer() {
        playerView = findViewById(R.id.playerview)
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            playerView.setPlayer(player)
            player!!.addMediaSource(getMediaSource())
            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentWindow, playbackPosition)
            player!!.prepare()
        }
    }

    fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            currentWindow = player!!.currentWindowIndex
            playbackPosition = player!!.currentPosition
            Log.e(
                TAG,
                "releasePlayer: $playWhenReady   --->   $currentWindow   --->      --->   $playbackPosition"
            )
            player!!.release()
            player = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun getMediaSource(): MediaSource {
        if (isProgressive) {
            Log.e(TAG, "create isProgressive media source")
            return buildProgressiveMediaSource()
        }
        Log.e(TAG, "create dash media source")
        return buildDashMediaSource()
    }

    private fun buildDashMediaSource(): MediaSource {

        return DashMediaSource.Factory(DefaultHttpDataSource.Factory())
            .createMediaSource(MediaItem.fromUri(filUrl))
    }

    private fun buildProgressiveMediaSource(): MediaSource {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        val dataSourceFactory = DataSource.Factory {
            val dataSource: HttpDataSource = httpDataSourceFactory.createDataSource()
            // Set a custom authentication request header.
            dataSource.setRequestProperty("Header", "Value")
            dataSource
        }

//        val dataSourceFactory: DataSource.Factory = ResolvingDataSource.Factory(
//            httpDataSourceFactory
//        ) // Provide just-in-time URI resolution logic.
//        { dataSpec: DataSpec ->
//            dataSpec.withUri(
//                resolveUri(
//                    dataSpec.uri
//                )
//            )
//        }

        httpDataSourceFactory.setTransferListener(this)

        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(filUrl))
    }

    private fun resolveUri(uri: Uri): Uri {
        return uri
    }

    override fun onTransferInitializing(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        Log.e(TAG, "onTransferInitializing: " )
    }

    override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
        Log.e(TAG, "onTransferStart: " )
    }

    override fun onBytesTransferred(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean,
        bytesTransferred: Int
    ) {
        Log.e(TAG, "onBytesTransferred: " )
    }

    override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
        Log.e(TAG, "onTransferEnd: " )
    }
}