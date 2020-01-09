package com.silent_manager.g29.silent_manager.android_components.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.data_layer.models.Contact

class ContactsAdapter(
    private val contacts: MutableList<Contact>,
    private val onClickContact: (Contact, Int) -> Unit
) :
    RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {

    class MyViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_row, parent, false)

        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contacts[position]

        val name = holder.rowView.findViewById<TextView>(R.id.search_user_name)
        val number = holder.rowView.findViewById<TextView>(R.id.contact_row_number)

        name.text = contact.name
        number.text = contact.number

        holder.rowView.setOnClickListener {
            onClickContact(contact, position)
        }
    }

    fun removedContact(position: Int){
        contacts.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun getItemCount() = contacts.size
}