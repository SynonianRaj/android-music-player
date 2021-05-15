package com.synonian.raj.sanampurisong

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.media.AudioManagerCompat
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.synonian.raj.sanampurisong.GlobalVariable.ACTION_CLOSE
import com.synonian.raj.sanampurisong.GlobalVariable.ACTION_PLAY_PAUSE
import com.synonian.raj.sanampurisong.GlobalVariable.audioManager
import com.synonian.raj.sanampurisong.GlobalVariable.builder
import com.synonian.raj.sanampurisong.GlobalVariable.mainPlayPause
import com.synonian.raj.sanampurisong.GlobalVariable.manager
import com.synonian.raj.sanampurisong.GlobalVariable.mp
import com.synonian.raj.sanampurisong.GlobalVariable.notificationView
import com.synonian.raj.sanampurisong.GlobalVariable.slidingLayout
import com.synonian.raj.sanampurisong.GlobalVariable.slidingPlayPauseBtn

const val channelName = "Media Control"
const val channelId = "com.synonian.raj.sanampurisong"



fun showNotification(context: Context, title: String?) {

    val intent = Intent(context, MainActivity::class.java).apply {
        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    val pauseOrClose = Intent(context, NotificationReceiver::class.java)
    notificationView = RemoteViews(context.packageName, R.layout.notification_layout)
    notificationView.setImageViewResource(R.id.notification_close, R.drawable.ic_close)
    notificationView.setTextViewText(R.id.title_notify, title)
    notificationView.setImageViewResource(
        R.id.notification_play_pause,
        R.drawable.ic_pause_main
    )

    pauseOrClose.apply {
        action = ACTION_CLOSE
    }
    val close: PendingIntent = PendingIntent.getBroadcast(context, 0, pauseOrClose, 0)

    notificationView.setOnClickPendingIntent(R.id.notification_close, close)

    pauseOrClose.apply {
        action = ACTION_PLAY_PAUSE
    }

    val pause = PendingIntent.getBroadcast(context, 0, pauseOrClose, 0)
    notificationView.setOnClickPendingIntent(R.id.notification_play_pause, pause)


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val imp = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, imp)

        manager.createNotificationChannel(channel)

    }

    builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.music_noti)
        .setCustomContentView(notificationView)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setNotificationSilent()
            .setAutoCancel(true)



    manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(0, builder.build())


}


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val action = intent.action
        if (action == ACTION_PLAY_PAUSE) {

            if (mp.isPlaying) {
                AudioManagerCompat.abandonAudioFocusRequest(audioManager, MainActivity().request)
                mp.pause()
                notificationView.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_play_main)
                manager.notify(0, builder.build())
                slidingPlayPauseBtn.setImageResource(R.drawable.ic_play_sliding)
                mainPlayPause.setImageResource(R.drawable.ic_play_main)
            } else {
                if (AudioManagerCompat.requestAudioFocus(
                        audioManager,
                        MainActivity().request
                    ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    mp.start()
                    notificationView.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_pause_main)
                    manager.notify(0, builder.build())
                    slidingPlayPauseBtn.setImageResource(R.drawable.ic_pasue_sliding)
                    mainPlayPause.setImageResource(R.drawable.ic_pause_main)

                }
            }

        } else if (action == ACTION_CLOSE) {
            manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancelAll()
            AudioManagerCompat.abandonAudioFocusRequest(audioManager, MainActivity().request)
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            mp.pause()
            mp.seekTo(0)
            slidingPlayPauseBtn.setImageResource(R.drawable.ic_play_sliding)
            mainPlayPause.setImageResource(R.drawable.ic_play_main)

        }


    }
}
