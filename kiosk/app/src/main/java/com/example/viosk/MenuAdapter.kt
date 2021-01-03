package com.example.viosk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class MenuAdapter<T>(
    private val mData: Array<Pair<String, Int>>,
    private var mHandler: T,
    private var mBiggerLayout: Boolean
) : RecyclerView.Adapter<MenuAdapter<T>.MenuViewHolder>() {

    inner class MenuViewHolder(view: View, listener: ListOnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val mListenerRef: WeakReference<ListOnClickListener>
        private val mItemLayout: LinearLayout = view.findViewById(R.id.menu_item)
        var mMenuTextView: TextView = view.findViewById(R.id.tv_menu_item)
        var mMenuImageView: ImageView = view.findViewById(R.id.iv_menu_item)

        init {
            mItemLayout.setOnClickListener(this)
            mListenerRef = WeakReference(listener)

            if (mBiggerLayout) {
                mItemLayout.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val imageLayoutParam = mMenuImageView.layoutParams;
                imageLayoutParam.width *= 2
                imageLayoutParam.height *= 2
                mMenuImageView.layoutParams = imageLayoutParam
            }
        }

        override fun onClick(v: View) {
            mListenerRef.get()!!.putItem(mData[adapterPosition].first)
        }
    }

    interface ListOnClickListener {
        fun putItem(menuName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter<T>.MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)

        return MenuViewHolder(view, mHandler as ListOnClickListener)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.mMenuTextView.text = mData[position].first
        holder.mMenuImageView.setImageResource(mData[position].second)
    }

    override fun getItemCount() = mData.size
}