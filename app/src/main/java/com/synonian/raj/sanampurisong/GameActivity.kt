package com.synonian.raj.sanampurisong

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.gson.GsonBuilder
import com.muddzdev.styleabletoast.StyleableToast
import com.synonian.raj.sanampurisong.GlobalVariable.handler
import java.io.IOException

class GameActivity : AppCompatActivity() {

    lateinit var gInterAd : InterstitialAd
    lateinit var dialog: AlertDialog
    private var nullParent: ViewGroup? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        StyleableToast.makeText(this,"Loading game list", Toast.LENGTH_SHORT, R.style.mytoast).show()

        MobileAds.initialize(this) {}
        gInterAd = InterstitialAd(this)
        gInterAd.adUnitId = getString(R.string.interAd2)
        gInterAd.loadAd(AdRequest.Builder().build())

        val layout = layoutInflater.inflate(R.layout.custom_dialog, nullParent, false)
        val dialogBuilder = AlertDialog.Builder(this)
        dialog = dialogBuilder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setView(layout)
        dialog.window!!.decorView.setBackgroundResource(R.drawable.rounded_layout)
        dialog.window!!.setLayout(700, 600)
        dialog.setCancelable(false)


        val gameRecyclerView = findViewById<RecyclerView>(R.id.game_recyclerView)
        val gameAdView = findViewById<AdView>(R.id.adView_game)


        val gameBody = fetchGameJson(this)
        val gson = GsonBuilder().create()
        val gameList = gson.fromJson(gameBody,Game::class.java)

        gameRecyclerView.layoutManager = GridLayoutManager(this, 2)
        gameRecyclerView.adapter = GameAdapter(gameList)


        gameAdView.loadAd(AdRequest.Builder().build())
        gameAdView.adListener = object : AdListener(){
            override fun onAdLoaded() {
                gameAdView.visibility = View.VISIBLE
                super.onAdLoaded()

            }
        }


        gInterAd.adListener =  object : AdListener(){

            override fun onAdLoaded() {
                //Toast.makeText(this@MainActivity, "Showing Ads", Toast.LENGTH_SHORT).show()
                dialog.show()
                handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    dialog.dismiss()
                    gInterAd.show()
                }, 2000)
                super.onAdLoaded()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                dialog.dismiss()
            }

            override fun onAdFailedToLoad(p0: LoadAdError?) {
                Toast.makeText(this@GameActivity, "Something went wrong with ad!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                super.onAdFailedToLoad(p0)

            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.game_menu_item,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.game_ad -> {
                gInterAd.loadAd(AdRequest.Builder().build())
                dialog.show()
                true }

            R.id.rate_app -> {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.synonian.raj.sanampurisong")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                true}
            R.id.share_app-> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "Check Out Sanam Puri Songs On Google Play Free! DOWNLOAD NOW \nhttps://play.google.com/store/apps/details?id=com.synonian.raj.sanampurisong")
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent,"Share this App Using..."))
                true}

            R.id.out_store-> {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.synonian.raj.sanampurisong")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                true
            }

            R.id.about->{
                val i = Intent(this, AboutActivity::class.java)
                startActivity(i)
                true
            }
                else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun fetchGameJson (context: Context): String? {

        val gameString: String?
        try {
            gameString = context.assets.open("data.json").bufferedReader().use {
                it.readText()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Something Went wrong !", Toast.LENGTH_LONG).show()
            return null
        }
        return gameString
    }

    override fun onDestroy() {
        GlobalVariable.manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        GlobalVariable.manager.cancelAll()
        super.onDestroy()
    }

}