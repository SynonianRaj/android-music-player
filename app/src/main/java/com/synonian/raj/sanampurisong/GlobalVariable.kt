package com.synonian.raj.sanampurisong

import android.app.NotificationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import com.sothree.slidinguppanel.SlidingUpPanelLayout

object GlobalVariable {
    //MediaPlayer
    val mp = MediaPlayer()

    //Native ads layout
    lateinit var ad_frame: FrameLayout

    lateinit var handler: Handler


    lateinit var audioManager: AudioManager

    lateinit var manager: NotificationManager
    lateinit var notificationView: RemoteViews
    lateinit var builder : NotificationCompat.Builder



    const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
    const val ACTION_CLOSE = "ACTION_CLOSE"


    lateinit var slidingLayout: SlidingUpPanelLayout


    //panel Layout wala jo sliding pannel h

    lateinit var slidingPlayPauseBtn : ImageButton
    lateinit var slidingProgressBar : ProgressBar
    lateinit var hide: ConstraintLayout //panel_layout
    lateinit var slidingSongTitle: TextView
    lateinit var slidingSongArtist: TextView


    //main wla

    lateinit var skipNext : ImageButton
    lateinit var skipPrev: ImageButton
    lateinit var mainPlayPause : ImageButton
    lateinit var seekBar: SeekBar
    lateinit var curr_dur : TextView
    lateinit var total_dur: TextView
    lateinit var titleSongMain : TextView
    lateinit var mainProgressBar: ProgressBar
    lateinit var shuffleSong : ImageButton
    lateinit var repeatSong : ImageButton


    //boolean
    var shuffleOn : Boolean = false




}