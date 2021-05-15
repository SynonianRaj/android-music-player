package com.synonian.raj.sanampurisong

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.gson.GsonBuilder
import com.muddzdev.styleabletoast.StyleableToast
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.synonian.raj.sanampurisong.GlobalVariable.ad_frame
import com.synonian.raj.sanampurisong.GlobalVariable.audioManager
import com.synonian.raj.sanampurisong.GlobalVariable.builder
import com.synonian.raj.sanampurisong.GlobalVariable.curr_dur
import com.synonian.raj.sanampurisong.GlobalVariable.handler
import com.synonian.raj.sanampurisong.GlobalVariable.hide
import com.synonian.raj.sanampurisong.GlobalVariable.mainPlayPause
import com.synonian.raj.sanampurisong.GlobalVariable.mainProgressBar
import com.synonian.raj.sanampurisong.GlobalVariable.manager
import com.synonian.raj.sanampurisong.GlobalVariable.mp
import com.synonian.raj.sanampurisong.GlobalVariable.notificationView
import com.synonian.raj.sanampurisong.GlobalVariable.repeatSong
import com.synonian.raj.sanampurisong.GlobalVariable.seekBar
import com.synonian.raj.sanampurisong.GlobalVariable.shuffleSong
import com.synonian.raj.sanampurisong.GlobalVariable.skipNext
import com.synonian.raj.sanampurisong.GlobalVariable.skipPrev
import com.synonian.raj.sanampurisong.GlobalVariable.slidingLayout
import com.synonian.raj.sanampurisong.GlobalVariable.slidingPlayPauseBtn
import com.synonian.raj.sanampurisong.GlobalVariable.slidingProgressBar
import com.synonian.raj.sanampurisong.GlobalVariable.slidingSongArtist
import com.synonian.raj.sanampurisong.GlobalVariable.slidingSongTitle
import com.synonian.raj.sanampurisong.GlobalVariable.titleSongMain
import com.synonian.raj.sanampurisong.GlobalVariable.total_dur
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var interAd: InterstitialAd
    lateinit var dialog: AlertDialog
    private var nullParent: ViewGroup? = null


    private var currentNativeAd: UnifiedNativeAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        MobileAds.initialize(this) {}
        interAd = InterstitialAd(this)
        interAd.adUnitId = getString(R.string.intrestial_ad)
        interAd.loadAd(AdRequest.Builder().build())

       if (!isNetworkAvailable(this)){
           StyleableToast.makeText(this,"Check for Internet Connection !",
                   Toast.LENGTH_LONG, R.style.noNet).show()
       }
        else {
           StyleableToast.makeText(this,
                   "You are Connected !",
                   Toast.LENGTH_LONG, R.style.noNet).show()
       }


        mp.setAudioAttributes(
                AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
        )

        slidingPlayPauseBtn = findViewById(R.id.sliding_play_pause_btn)
        slidingProgressBar = findViewById(R.id.sliding_progress_bar)
        hide = findViewById(R.id.panel_layout)
        slidingLayout = findViewById(R.id.sliding_layout_panel)
        slidingSongArtist = findViewById(R.id.sliding_song_artist)
        slidingSongTitle = findViewById(R.id.sliding_song_title)
        titleSongMain = findViewById(R.id.title_song_main)
        skipNext = findViewById(R.id.skip_next)
        skipPrev = findViewById(R.id.skip_prev)
        seekBar = findViewById(R.id.seekBar)
        mainPlayPause = findViewById(R.id.play_pause_main_btn)
        curr_dur = findViewById(R.id.curr_dur)
        total_dur = findViewById(R.id.total_dur)
        mainProgressBar = findViewById(R.id.main_pbar)
        shuffleSong = findViewById(R.id.shuffle_on_off)
        repeatSong = findViewById(R.id.repeat_song)
        ad_frame = findViewById(R.id.ad_frame)

        val layout = layoutInflater.inflate(R.layout.custom_dialog, nullParent, false)
        val dialogBuilder = AlertDialog.Builder(this)
        dialog = dialogBuilder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setView(layout)
        dialog.window!!.decorView.setBackgroundResource(R.drawable.rounded_layout)
        dialog.window!!.setLayout(700, 600)
        dialog.setCancelable(false)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN

        val body = fetchMusicFromJson(this)
        val gson = GsonBuilder().create()
        val item = gson.fromJson(body, Model::class.java)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MusicAdapter(item)


        slidingSongTitle.isSelected = true
        slidingSongArtist.isSelected = true
        titleSongMain.isSelected = true




        val adView = findViewById<AdView>(R.id.adView_main)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError?) {
               // Toast.makeText(this@MainActivity, "Failed to load Ad", Toast.LENGTH_SHORT).show()
                super.onAdFailedToLoad(p0)
            }
            override fun onAdLoaded() {
               // Toast.makeText(this@MainActivity, "Ads loaded", Toast.LENGTH_SHORT).show()
                super.onAdLoaded()
                adView.visibility = View.VISIBLE
            }
        }


        slidingPlayPauseBtn.setOnClickListener {
            if (mp.isPlaying) {
                AudioManagerCompat.abandonAudioFocusRequest(audioManager, request)
                mp.pause()
                slidingPlayPauseBtn.setImageResource(R.drawable.ic_play_sliding)
                mainPlayPause.setImageResource(R.drawable.ic_play_main)
                notificationView.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_play_main)
                manager.notify(0, builder.build())
            } else {
                if (AudioManagerCompat.requestAudioFocus(
                                audioManager,
                                request
                        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                ) {
                    mp.start()
                    slidingPlayPauseBtn.setImageResource(R.drawable.ic_pasue_sliding)
                    mainPlayPause.setImageResource(R.drawable.ic_pause_main)
                    notificationView.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_pause_main)
                    manager.notify(0, builder.build())
                }
            }
        }

        mainPlayPause.setOnClickListener {
            if (mp.isPlaying) {
                AudioManagerCompat.abandonAudioFocusRequest(audioManager, request)
                mp.pause()
                slidingPlayPauseBtn.setImageResource(R.drawable.ic_play_sliding)
                mainPlayPause.setImageResource(R.drawable.ic_play_main)
                notificationView.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_play_main)
                manager.notify(0, builder.build())
            } else {
                if (AudioManagerCompat.requestAudioFocus(
                                audioManager,
                                request
                        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                ) {
                    mp.start()
                    slidingPlayPauseBtn.setImageResource(R.drawable.ic_pasue_sliding)
                    mainPlayPause.setImageResource(R.drawable.ic_pause_main)
                    notificationView.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_pause_main)
                    manager.notify(0, builder.build())
                }
            }
        }






        seekBar.progress = mp.currentPosition
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                val playTime = mp.currentPosition.toLong()
                curr_dur.text = String.format(
                        "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(playTime),
                        TimeUnit.MILLISECONDS.toSeconds(playTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(playTime))
                )

                seekBar.progress = playTime.toInt()
                Handler(Looper.getMainLooper()).postDelayed(this, 100)

            }
        }, 1000)



        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //   TODO("Not yet implemented")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //  TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mp.seekTo(seekBar!!.progress)
            }


        })



        slidingLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                hide.visibility = View.GONE

            }

            override fun onPanelStateChanged(
                    panel: View?,
                    previousState: SlidingUpPanelLayout.PanelState?,
                    newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED
                ) {
                    supportActionBar!!.hide()
                    refreshAd()
                    ad_frame.visibility = View.VISIBLE
                }

                if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    hide.visibility = View.VISIBLE
                    supportActionBar!!.show()
                    ad_frame.visibility = View.GONE
                }

                if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.HIDDEN) {

                    supportActionBar!!.show()

                }

            }

        })




        interAd.adListener =  object : AdListener(){

            override fun onAdLoaded() {
                //Toast.makeText(this@MainActivity, "Showing Ads", Toast.LENGTH_SHORT).show()
                dialog.show()
                handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    dialog.dismiss()
                    interAd.show()
                }, 2000)
                super.onAdLoaded()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                dialog.dismiss()
            }

            override fun onAdFailedToLoad(p0: LoadAdError?) {
                Toast.makeText(this@MainActivity, "Something went wrong with ad!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                super.onAdFailedToLoad(p0)

            }
        }










    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.music_menu_item, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.watch_ad1 -> {
                interAd.loadAd(AdRequest.Builder().build())
                dialog.show()
                true
            }
            R.id.play_game -> {
                if (mp.isPlaying) {
                    AudioManagerCompat.abandonAudioFocusRequest(audioManager, request)
                    mp.pause()
                    mp.seekTo(0)
                    manager.cancelAll()
                    slidingPlayPauseBtn.setImageResource(R.drawable.ic_play_sliding)
                    mainPlayPause.setImageResource(R.drawable.ic_play_main)

                }
                val gameIntent = Intent(this, GameActivity::class.java)
                startActivity(gameIntent)
                true
            }


            R.id.share_this_app -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "Check Out Sanam Puri Songs On Google Play Free! DOWNLOAD NOW \nhttps://play.google.com/store/apps/details?id=com.synonian.raj.sanampurisong")
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share this App Using..."))
                true
            }
            R.id.rate_this_app -> {

                val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.synonian.raj.sanampurisong")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)


                true
            }

            R.id.out_store-> {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.synonian.raj.sanampurisong")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                true
            }

            R.id.about -> {

                val i = Intent(this, AboutActivity::class.java)
                startActivity(i)
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }


    private fun fetchMusicFromJson(context: Context): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open("sanam.json").bufferedReader().use {
                it.readText()
            }
        } catch (io: IOException) {
            Toast.makeText(context, "Song Loading Failed ! ", Toast.LENGTH_SHORT).show()
            return null
        }
        return jsonString
    }

    override fun onBackPressed() {
        if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED ||
            slidingLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED
        ) {

            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

        } else {
            super.onBackPressed()
        }
    }


    private val audioChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->

        when (focusChange) {

            AudioManager.AUDIOFOCUS_LOSS -> {
                mp.pause()
                slidingPlayPauseBtn.setImageResource(R.drawable.ic_play_sliding)
                mainPlayPause.setImageResource(R.drawable.ic_play_main)
                notificationView.setImageViewResource(R.id.notification_play_pause, R.drawable.ic_play_main)
                manager.notify(0, builder.build())
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mp.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume, keep playing
                mp.setVolume(0.2f, 0.2f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                mp.setVolume(1f, 1f)
                mp.start()
                //Toast.makeText(this, "Gain focus", Toast.LENGTH_SHORT).show()

            }

        }


    }


    val request: AudioFocusRequestCompat =
        AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                    AudioAttributesCompat.Builder()
                            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                            .build()
            )
            .setWillPauseWhenDucked(true)

            .setOnAudioFocusChangeListener(audioChangeListener)
            .build()


    override fun onNewIntent(intent: Intent?) {
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        super.onNewIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(0)
        mp.reset()

        dialog.dismiss()

    }


    //NATIVE  ADS
    fun refreshAd() {

        val builder = AdLoader.Builder(this, getString(R.string.native_ad_id))

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            val activityDestroyed: Boolean = isDestroyed
            if (activityDestroyed || isFinishing || isChangingConfigurations) {
                unifiedNativeAd.destroy()
                return@forUnifiedNativeAd
            }
            currentNativeAd?.destroy()
            currentNativeAd = unifiedNativeAd
            val adView = layoutInflater.inflate(R.layout.native_ads_layout, nullParent) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)

            ad_frame.removeAllViews()
            ad_frame.addView(adView)
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                Toast.makeText(
                        this@MainActivity, "Something went wrong!",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())


    }

}