package com.sdercolin.vlabeler.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sdercolin.vlabeler.audio.PlayerState
import com.sdercolin.vlabeler.env.Log
import com.sdercolin.vlabeler.ui.AppSnackbarState
import com.sdercolin.vlabeler.ui.string.Strings
import com.sdercolin.vlabeler.ui.string.stringStatic
import com.sdercolin.vlabeler.util.lastPathSection
import com.sdercolin.vlabeler.util.toMillisecond
import java.io.FileNotFoundException

/**
 * State object for [Video].
 */
class VideoState(
    private val playerState: PlayerState,
    private val snackbarState: AppSnackbarState,
    val exit: () -> Unit,
) {
    var width: Dp by mutableStateOf(DefaultWidth)
    var height: Dp = width * AspectRatio

    val videoPlayer: VideoPlayer = VideoPlayer()
    var videoPath: String? by mutableStateOf(null)
        private set
    var currentSampleRate: Float? = null
    var mode: Mode? by mutableStateOf(null)
        private set

    var syncOperations = mutableStateListOf<SyncOperation>()
    var lastSavedTime: Long? = null
        private set

    /**
     * @return false if initialization failed
     */
    suspend fun init(): Boolean {
        videoPath = null
        if (videoPlayer.mediaPlayerComponent == null) {
            videoPlayer.init()
                .onFailure {
                    exit()
                    snackbarState.showSnackbar(stringStatic(Strings.VideoComponentInitializationException))
                    return false
                }
        }
        return true
    }

    suspend fun locatePath(audioPath: String): Result<String> {
        return FindVideoStrategy.SamePlaceOfReferenceAudio.find(
            audioPath,
            SupportedExtensionList,
        )
            .onSuccess { videoPath = it }
            .onFailure {
                videoPath = null
                exit()
                if (it is FileNotFoundException) {
                    snackbarState.showSnackbar(
                        stringStatic(
                            Strings.VideoFileNotFoundExceptionTemplate,
                            audioPath.lastPathSection,
                            SupportedExtensionList.joinToString("/"),
                        ),
                    )
                } else {
                    Log.error(it)
                }
            }
    }

    private fun Float.toTime(): Long? {
        return currentSampleRate?.let { toMillisecond(this, it).toLong() }
    }

    val isEmbeddedMode: Boolean
        get() = mode == Mode.Embedded

    val isNewWindowMode: Boolean
        get() = mode == Mode.NewWindow

    fun setEmbeddedMode() {
        mode = Mode.Embedded
    }

    fun setNewWindowMode() {
        mode = Mode.NewWindow
    }

    fun audioPlayerCurrentTime(): Long? {
        return playerState.framePosition?.toTime()
    }

    fun lastStartedTime(): Long? {
        return playerState.lastStartedFrame?.toFloat()?.toTime()
    }

    fun saveTime(reset: Boolean = false) {
        lastSavedTime = if (reset) null else videoPlayer.currentTime
    }

    companion object {
        val MinWidth = 200.dp
        val MaxWidth = 600.dp
        val DefaultWidth = 360.dp
        const val AspectRatio = 3f / 4f
        val SupportedExtensionList = listOf(".mp4", ".webm")
    }

    enum class Mode {
        Embedded,
        NewWindow,
    }
}
