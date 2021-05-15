package com.synonian.raj.sanampurisong

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.muddzdev.styleabletoast.StyleableToast
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.synonian.raj.sanampurisong.GlobalVariable.mainPlayPause
import com.synonian.raj.sanampurisong.GlobalVariable.mainProgressBar
import com.synonian.raj.sanampurisong.GlobalVariable.mp
import com.synonian.raj.sanampurisong.GlobalVariable.repeatSong
import com.synonian.raj.sanampurisong.GlobalVariable.shuffleOn
import com.synonian.raj.sanampurisong.GlobalVariable.shuffleSong
import com.synonian.raj.sanampurisong.GlobalVariable.skipNext
import com.synonian.raj.sanampurisong.GlobalVariable.skipPrev
import com.synonian.raj.sanampurisong.GlobalVariable.slidingLayout
import com.synonian.raj.sanampurisong.GlobalVariable.slidingPlayPauseBtn
import com.synonian.raj.sanampurisong.GlobalVariable.slidingProgressBar
import com.synonian.raj.sanampurisong.GlobalVariable.slidingSongArtist
import com.synonian.raj.sanampurisong.GlobalVariable.slidingSongTitle
import com.synonian.raj.sanampurisong.GlobalVariable.titleSongMain

lateinit var ss: List<MusicList>

class MusicAdapter(private val m: Model) : RecyclerView.Adapter<MusicItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicItemHolder {

        val inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.music_item, parent, false)
        ss = m.music
        return MusicItemHolder(row)

    }

    override fun onBindViewHolder(holder: MusicItemHolder, position: Int) {
        val data = m.music[position]
        val songTitle = holder.itemView.findViewById<TextView>(R.id.song_title_main)
        val songArtist = holder.itemView.findViewById<TextView>(R.id.song_artist_main)
        songTitle.text = data.title
        songArtist.text = data.artist

        songArtist.isSelected = true
        songTitle.isSelected = true

        holder.itemView.findViewById<ImageButton>(R.id.share_btn).setOnClickListener {
            Toast.makeText(it.context, "Sharing", Toast.LENGTH_SHORT).show()
            val i = Intent()
            i.action = Intent.ACTION_SEND
            i.putExtra(
                Intent.EXTRA_TEXT,
                "Listen ${data.title} by ${data.artist} free on this App ! DOWNLOAD NOW ! \nhttps://play.google.com/store/apps/details?id=com.synonian.raj.sanampurisong"
            )
            i.type = "text/plain"
            it.context.startActivity(Intent.createChooser(i, "Share Song Using"))
        }

        holder.data = data

    }

    override fun getItemCount(): Int {
        return m.music.size
    }

}

class MusicItemHolder(view: View, var data: MusicList? = null) : RecyclerView.ViewHolder(view){
    init {

        view.setOnClickListener {

           if (!isNetworkAvailable(view.context)){
               StyleableToast.makeText(view.context,"Check for Internet Connection !", Toast.LENGTH_LONG, R.style.noNet).show()
           }
                else {
               if (!mp.isPlaying || mp.isPlaying) {
                   mp.reset()
               }
               play(data?.url)
               showNotification(view.context, data?.title)
               StyleableToast.makeText(view.context, "Playing !", Toast.LENGTH_SHORT, R.style.mytoast).show()
               mainPlayPause.setImageResource(R.drawable.ic_pause_main)
               slidingPlayPauseBtn.setImageResource(R.drawable.ic_pasue_sliding)


               slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
               slidingSongTitle.text = data?.title
               slidingSongArtist.text = data?.artist
               titleSongMain.text = data?.title

               slidingProgressBar.visibility = View.VISIBLE
               slidingPlayPauseBtn.visibility = View.GONE

               mainProgressBar.visibility = View.VISIBLE
               mainPlayPause.visibility = View.GONE

           }
            var item: Int = layoutPosition
            skipPrev.isEnabled = layoutPosition != 0
            skipNext.isEnabled = layoutPosition != ss.count() - 1

            skipNext.setOnClickListener {
                skipPrev.isEnabled = true
                slidingPlayPauseBtn.isEnabled = false
                mainPlayPause.isEnabled =false
                if (shuffleOn) {
                    item = (0 until ss.count() - 1).random()
                    println(item)
                    mp.reset()
                    play(ss[item].url)
                    slidingSongTitle.text = ss[item].title
                    slidingSongArtist.text = ss[item].artist
                    titleSongMain.text = ss[item].title
                    mainProgressBar.visibility = View.VISIBLE
                    slidingProgressBar.visibility = View.VISIBLE

                    showNotification(view.context, ss[item].title)
                   // Toast.makeText(view.context, " NExt Music", Toast.LENGTH_SHORT).show()
                } else {
                    item++
                    if (item <= ss.count() - 1) {
                        println(item)
                        mp.stop()
                        mp.reset()
                        play(ss[item].url)
                        slidingSongTitle.text = ss[item].title
                        slidingSongArtist.text = ss[item].artist
                        titleSongMain.text = ss[item].title
                        mainProgressBar.visibility = View.VISIBLE
                        slidingProgressBar.visibility = View.VISIBLE
                        showNotification(view.context, ss[item].title)

                    }
                    if (item == ss.count() - 1) {
                        skipNext.isEnabled = false
                        skipPrev.isEnabled = true

                    }
                   // Toast.makeText(view.context, "Next Clicked", Toast.LENGTH_SHORT).show()
                }
            }


            skipPrev.setOnClickListener {
                slidingPlayPauseBtn.isEnabled = false
                mainPlayPause.isEnabled =false
                item--
                skipNext.isEnabled = true

                if (item >= 0) {
                    println(item)
                    mp.stop()
                    mp.reset()
                    play(ss[item].url)
                    mainProgressBar.visibility = View.VISIBLE
                    slidingProgressBar.visibility = View.VISIBLE
                    slidingSongTitle.text = ss[item].title
                    slidingSongArtist.text = ss[item].artist
                    titleSongMain.text = ss[item].title
                    showNotification(view.context, ss[item].title)
                }
                if (item <= 0) {
                    skipPrev.isEnabled = false
                }
                //Toast.makeText(view.context, "Prev Clicked", Toast.LENGTH_SHORT).show()
            }




            shuffleSong.setOnClickListener {
                if (!shuffleOn) {
                    shuffleSong.setImageResource(R.drawable.ic_shuffle_on)
                    shuffleOn = true
                    Toast.makeText(view.context, "Shuffle is On !", Toast.LENGTH_SHORT).show()
                } else {
                    shuffleOn = false
                    shuffleSong.setImageResource(R.drawable.ic_shuffle_off)

                    Toast.makeText(view.context, "Shuffle is Off !", Toast.LENGTH_SHORT).show()
                }

            }


            repeatSong.setOnClickListener {
                if (!mp.isLooping) {
                    repeatSong.setImageResource(R.drawable.ic_repeat_one)
                    mp.isLooping = true
                    Toast.makeText(view.context, "Repeating current song !", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    mp.isLooping = false
                    repeatSong.setImageResource(R.drawable.ic_repeat)
                    Toast.makeText(view.context, "Repeating Off !", Toast.LENGTH_SHORT).show()
                }

            }


            mp.setOnCompletionListener {
skipPrev.isEnabled =true
                slidingPlayPauseBtn.isEnabled = false
                mainPlayPause.isEnabled =false
                if (shuffleOn) {
                    item = (0 until ss.count() - 1).random()
                    println(item)
                    mp.reset()
                    play(ss[item].url)
                    slidingSongTitle.text = ss[item].title
                    slidingSongArtist.text = ss[item].artist
                    titleSongMain.text = ss[item].title
                    mainProgressBar.visibility = View.VISIBLE
                    slidingProgressBar.visibility = View.VISIBLE
                    showNotification(view.context, ss[item].title)
                   // Toast.makeText(view.context, " NExt Music", Toast.LENGTH_SHORT).show()

                } else {
                    item++
                    skipPrev.isEnabled = item != 0
                    skipNext.isEnabled = item != ss.count() - 1
                    if (item <= ss.count() - 1) {
                        println(item)
                        mp.reset()
                        play(ss[item].url)
                        slidingSongTitle.text = ss[item].title
                        slidingSongArtist.text = ss[item].artist
                        titleSongMain.text = ss[item].title
                        mainProgressBar.visibility = View.VISIBLE
                        slidingProgressBar.visibility = View.VISIBLE
                        showNotification(view.context, ss[item].title)
                     //   Toast.makeText(view.context, " NExt Music", Toast.LENGTH_SHORT).show()
                    }
                    // if (item == ss.count()-1)
                    else {
                        Toast.makeText(view.context, " Music all completed", Toast.LENGTH_SHORT)
                            .show()
                        //mp.reset()
                        mp.seekTo(0)
                        slidingPlayPauseBtn.setImageResource(R.drawable.ic_play_main)
                        mainPlayPause.setImageResource(R.drawable.ic_play_main)
                        skipPrev.isEnabled = true
                    }

                }
            }

        }

    }




}
