package com.synonian.raj.sanampurisong

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds

class GameView : AppCompatActivity() {

lateinit var vInterAd : InterstitialAd

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_view)
        supportActionBar!!.hide()
        val v = findViewById<WebView>(R.id.webview)
        val gameUrl = intent.getStringExtra("url")
        val orientation = intent.getStringExtra("orientation")

        MobileAds.initialize(this) {}

        vInterAd = InterstitialAd(this)
        vInterAd.adUnitId = getString(R.string.interAd3)
        vInterAd.loadAd(AdRequest.Builder().build())

        v.loadUrl(gameUrl!!)
        v.settings.javaScriptEnabled = true
        v.settings.setSupportZoom(true)
        v.settings.builtInZoomControls = true
        v.settings.displayZoomControls = false
        v.settings.useWideViewPort = true
        v.settings.loadWithOverviewMode = true

        if (orientation == "landscape") {
            Toast.makeText(this, "orientation changed to Landscape", Toast.LENGTH_LONG).show()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onBackPressed() {
        val builders: AlertDialog.Builder = AlertDialog.Builder(this)
        builders.setCancelable(false)
        builders.setMessage("Do you want to Exit?")
        builders.setPositiveButton("Yes") { _, _ ->
            if (vInterAd.isLoaded){
                vInterAd.show()
            }

            finish()
        }
        builders.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }
        val alert: AlertDialog = builders.create()
        alert.show()
    }

    override fun onDestroy() {
        GlobalVariable.manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        GlobalVariable.manager.cancelAll()
        super.onDestroy()
    }

}