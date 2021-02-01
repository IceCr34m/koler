package com.chooloo.www.callmanager.adapter

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chooloo.www.callmanager.ui.listitem.ListItemHolder

class MenuAdapter(
        private val _context: Context,
        private val _menu: Menu
) : RecyclerView.Adapter<ListItemHolder>() {
    private var _onMenuItemClickListener: OnMenuItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        return ListItemHolder(_context)
    }

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        val menuItem = _menu.getItem(position)
        holder.listItem.apply {
            setOnClickListener { _onMenuItemClickListener?.onMenuItemClick(menuItem) }
            setBigText(menuItem.title as String)
            setImageDrawable(menuItem.icon)
        }
    }

    override fun getItemCount(): Int {
        return _menu.size()
    }

    fun setOnMenuItemClickListener(onMenuItemClickListener: OnMenuItemClickListener) {
        _onMenuItemClickListener = onMenuItemClickListener
    }

    interface OnMenuItemClickListener {
        fun onMenuItemClick(menuItem: MenuItem)
    }
}