package com.hopen.lib.sample.picassiette

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.WorkerThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.hopen.lib.picassiette.core.Picassiette
import com.hopen.lib.picassiette.core.Picassiette.Config
import com.hopen.lib.picassiette.core.Target
import com.hopen.lib.sample.R
import kotlinx.android.synthetic.main.item_view.view.*

class ItemAdapter(context: Context) : RecyclerView.Adapter<ItemViewHolder>() {

    private val imageSize = context.resources.getDimensionPixelSize(R.dimen.picture_size).let {
        it to it
    }

    private val items: List<Pair<String, String>> = (1..200).map {
        Pair("Item $it", "https://via.placeholder.com/400x400.png?text=$it")
    }

    private val picassiette = Picassiette.newInstance(Config<Bitmap>(context).apply {
        onFetchData = this@ItemAdapter::fetchData
    }, Bitmap::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (label, imageUrl) = items[position]
        holder.tvLabel.text = label
        picassiette.fetch(imageUrl, imageSize, holder.target)
    }

    @WorkerThread
    private fun fetchData(key: String, customParams: Any?): Bitmap? {
        val imageSize = customParams as? Pair<Int, Int>
        val width = imageSize?.first
        val height = imageSize?.second
        return BitmapDownloader.downloadBitmap(key, width, height)
    }

}

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val tvLabel: TextView = view.label

    val imgPicture: ImageView = view.picture

    val target = object : Target<Bitmap>() {

        override fun onPreReceive(key: String, customParams: Any?) {
            imgPicture.setImageDrawable(null)
        }

        override fun onReceive(data: Bitmap?) {
            imgPicture.setImageBitmap(data)
        }
    }

}