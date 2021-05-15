package com.synonian.raj.sanampurisong

import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import androidx.media.AudioManagerCompat
import com.synonian.raj.sanampurisong.GlobalVariable.audioManager
import com.synonian.raj.sanampurisong.GlobalVariable.mainPlayPause
import com.synonian.raj.sanampurisong.GlobalVariable.mainProgressBar
import com.synonian.raj.sanampurisong.GlobalVariable.mp
import com.synonian.raj.sanampurisong.GlobalVariable.seekBar
import com.synonian.raj.sanampurisong.GlobalVariable.slidingPlayPauseBtn
import com.synonian.raj.sanampurisong.GlobalVariable.slidingProgressBar
import com.synonian.raj.sanampurisong.GlobalVariable.total_dur
import java.io.IOException
import java.util.concurrent.TimeUnit


fun play(url: String?) {


    try {
        mp.setDataSource(url)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    mp.prepareAsync()
    mp.setOnPreparedListener {
        if (AudioManagerCompat.requestAudioFocus(
                        audioManager,
                        MainActivity().request
                ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        ) {

            mp.start()
        }
        slidingProgressBar.visibility = View.GONE
        slidingPlayPauseBtn.visibility = View.VISIBLE
        mainPlayPause.isEnabled = true
        slidingPlayPauseBtn.isEnabled = true


        mainProgressBar.visibility = View.GONE
        mainPlayPause.visibility = View.VISIBLE

        val duration: Long = mp.duration.toLong()
        val time = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        )

        total_dur.text = time
        seekBar.max = duration.toInt()
    }
}



fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        try {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                Log.i("update_statut", "Network is available : true")
                return true
            }
        } catch (e: Exception) {
            Log.i("update_statut", "" + e.message)
        }
    }
    Log.i("update_statut", "Network is available : FALSE ")
    return false
}