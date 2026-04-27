package br.ufpr.musicbookmark.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import br.ufpr.musicbookmark.R
import br.ufpr.musicbookmark.controller.MusicController
import br.ufpr.musicbookmark.model.Music

class MusicAdapter(
    private val context: Context,
    private var musics: MutableList<Music>
) : BaseAdapter() {

    override fun hasStableIds() = true

    private class ViewHolder(
        val imgFavorite: ImageView,
        val tvTitle: TextView,
        val tvStatusBadge: TextView,
        val tvArtistYearGenre: TextView,
        val ratingBarDisplay: RatingBar
    )

    override fun getCount() = musics.size
    override fun getItem(position: Int) = musics[position]
    override fun getItemId(position: Int) = musics[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_music, parent, false)
            holder = ViewHolder(
                imgFavorite       = view.findViewById(R.id.imgFavorite),
                tvTitle           = view.findViewById(R.id.tvTitle),
                tvStatusBadge     = view.findViewById(R.id.tvStatusBadge),
                tvArtistYearGenre = view.findViewById(R.id.tvArtistYearGenre),
                ratingBarDisplay  = view.findViewById(R.id.ratingBarDisplay)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val music = musics[position]

        holder.tvTitle.text = music.title
        holder.tvArtistYearGenre.text = "${music.artist} · ${music.year} · ${music.genre}"
        holder.tvStatusBadge.text = music.status
        holder.tvStatusBadge.setBackgroundColor(
            if (music.status == MusicController.STATUS_LISTENED) Color.parseColor("#4CAF50")
            else Color.parseColor("#FF9800")
        )
        holder.ratingBarDisplay.rating = music.rating.toFloat()
        holder.imgFavorite.setImageResource(
            if (music.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )

        return view
    }

    fun updateList(newList: List<Music>) {
        musics.clear()
        musics.addAll(newList)
        notifyDataSetChanged()
    }
}
