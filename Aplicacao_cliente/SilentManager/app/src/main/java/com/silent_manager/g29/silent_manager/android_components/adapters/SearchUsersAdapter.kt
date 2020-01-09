package com.silent_manager.g29.silent_manager.android_components.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.data_layer.models.User

class SearchUsersAdapter(
    private val users: MutableList<User>,
    private val onClickUser: (User, Int) -> Unit
) :
    RecyclerView.Adapter<SearchUsersAdapter.MyViewHolder>() {

    class MyViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_search_row, parent, false)

        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = users[position]

        val name = holder.rowView.findViewById<TextView>(R.id.search_user_name)

        name.text = user.name

        holder.rowView.setOnClickListener {
            onClickUser(user, position)
        }
    }

    fun getCurrentUsersDisplayed(): Array<User>{
        return users.toTypedArray()
    }

    fun filterUserList(name: String){

    }

    fun filterUserListWithNewList(name: Array<User>) {
        users.clear()
        users.addAll(name)
        notifyDataSetChanged()
    }

    fun removedContact(position: Int) {
        users.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun getItemCount() = users.size
}