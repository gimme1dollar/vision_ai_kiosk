package com.example.viosk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class MenuAdapter<T>(private val data: Array<Pair<String, Int>>, private var handler: T) :
    RecyclerView.Adapter<MenuAdapter<T>.MenuViewHolder>() {

    inner class MenuViewHolder(view: View, listener: ListOnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val listenerRef: WeakReference<ListOnClickListener>
        private val layout: LinearLayout = view.findViewById(R.id.menu_item)
        val textView: TextView = view.findViewById(R.id.tv_menu_item)
        val imageView: ImageView = view.findViewById(R.id.iv_menu_item)

        init {
            layout.setOnClickListener(this)
            listenerRef = WeakReference(listener)
        }

        override fun onClick(v: View) {
            listenerRef.get()!!.putItem(data[adapterPosition].first)
        }
    }

    interface ListOnClickListener {
        fun putItem(menuName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter<T>.MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)

        return MenuViewHolder(view, handler as ListOnClickListener)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.textView.text = data[position].first
        holder.imageView.setImageResource(data[position].second)
    }

    override fun getItemCount() = data.size
}