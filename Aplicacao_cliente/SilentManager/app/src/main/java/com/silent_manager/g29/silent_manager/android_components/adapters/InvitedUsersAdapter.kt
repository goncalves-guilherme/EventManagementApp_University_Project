package com.silent_manager.g29.silent_manager.android_components.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.data_layer.models.User

class InvitedUsersAdapter(
    private val users: MutableList<User>?,
    private val editable: Boolean,
    private val onDeleteUserInvitation: (Int) -> Unit
    ) :
    RecyclerView.Adapter<InvitedUsersAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        if(users.isNullOrEmpty()){
            return 0
        }
        return users.size
    }

    class MyViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.invited_users_row, parent, false)

        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = users?.get(position)
        val userName = holder.rowView.findViewById<TextView>(R.id.invited_user_name)
        val deleteUser = holder.rowView.findViewById<TextView>(R.id.invited_users_row_delete)
        deleteUser.setOnClickListener {
            user?.id?.let{
                onDeleteUserInvitation(it)
                removeUser(user)
                notifyDataSetChanged()
            }
        }

        if(!editable){
            deleteUser.visibility = View.GONE
        }

        userName.text = user?.name
    }

    private fun removeUser(user: User){
        users?.remove(user)
    }

}