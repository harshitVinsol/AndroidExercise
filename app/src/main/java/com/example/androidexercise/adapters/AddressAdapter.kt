package com.example.androidexercise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidexercise.*
import com.example.androidexercise.AddressesActivity.Companion.DEFAULT_ID
import com.example.androidexercise.models.Address

/*
Class AddressAdapter to be used in RecyclerView for showing addresses having list of Address and context of Activity called from
 */
class AddressAdapter(
    var addressList: MutableList<Address>,
    private val callback: (Address, Int, ViewHolder) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    /*
    function to attach a layout to the ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return ViewHolder(v)
    }

    /*
    A function to set the list as a parameter to addressList and call notifyDataSetChanged()
     */
    fun setList(list: MutableList<Address>) {
        addressList = list
        notifyDataSetChanged()
    }

    /*
    function to return size of addressList
     */
    override fun getItemCount(): Int {
        return addressList.size
    }

    /*
    function to bind each element of the RecyclerView
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addressList[position]
        val str =
            address.firstname + "\n" + address.address1 + ", " + address.address2 + ",\n" + address.city + ",\n" + address.zipcode
        holder.textAddress.text = str
        /*
        To check if the Address is Default Address and set default tick visible if true
         */
        holder.defaultAddress.isVisible = address.id == DEFAULT_ID

        holder.settingsButton.setOnClickListener {
            callback.invoke(address, position, holder)
        }
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textAddress: TextView = itemView.findViewById(R.id.text_address)
    val defaultAddress: ImageView = itemView.findViewById(R.id.default_tick)
    val settingsButton: ImageButton = itemView.findViewById(R.id.settings_button)
}