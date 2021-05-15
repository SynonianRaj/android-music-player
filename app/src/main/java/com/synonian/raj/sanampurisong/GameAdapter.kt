package com.synonian.raj.sanampurisong

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class GameAdapter(val game: Game): RecyclerView.Adapter<GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val layout = LayoutInflater.from(parent.context)
        val row = layout.inflate(R.layout.game_item, parent, false)
        return GameViewHolder(row)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val pos = game.data[position]
        holder.itemView.findViewById<TextView>(R.id.game_title).text = pos.title
        val img = holder.itemView.findViewById<ImageView>(R.id.game_image)
        Picasso.get().load(pos.thumbnailUrl).into(img)

        holder.g = pos
    }

    override fun getItemCount(): Int {
        return game.data.count()
    }
}

class GameViewHolder(v: View, var g:GameList?= null): RecyclerView.ViewHolder(v) {
    init {

        v.setOnClickListener {
            val i = Intent(v.context, GameView::class.java)
            i.putExtra("url",g?.url)
            i.putExtra("orientation", g?.orientation)
            v.context.startActivity(i)
        }
    }
}
