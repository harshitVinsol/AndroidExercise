package com.example.androidexercise.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidexercise.*
import com.example.androidexercise.AddressesActivity.Companion.DEFAULT_ID
import com.example.androidexercise.AddressesActivity.Companion.INTENT_KEY
import com.example.androidexercise.callbacks.AddressCallback
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import kotlinx.android.synthetic.main.item_address.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
Class AddressAdapter to be used in RecyclerView for showing addresses having list of Address and context of Activity called from
 */
class AddressAdapter(var addressList: MutableList<Address> , private val callback : (Address, Int, ViewHolder) -> Unit) : RecyclerView.Adapter<ViewHolder>(){
    companion object{
        const val ADDRESS_BUNDLE = "addressBundle"
        const val ADDRESS_ID = "id"
        const val ADDRESS_POSITION = "positon"
        const val NAME = "name"
        const val ADD1 = "add1"
        const val ADD2 = "add2"
        const val CITY = "city"
        const val STATE = "state"
        const val PINCODE = "pincode"
    }
    /*
    function to attach a layout to the ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return ViewHolder(v)
    }
    public fun setList(list : MutableList<Address>){
        addressList = list
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
        val str = address.firstname + "\n" + address.address1 + ", "+ address.address2+ ",\n" + address.city + ",\n" + address.zipcode
        holder.textAddress.text = str
        /*
        To check if the Address is Default Address and set default tick visible if true
         */
        if(address.id == DEFAULT_ID){
            holder.defaultAddress.isVisible = true
        }
        holder.settingsButton.setOnClickListener{
            callback.invoke(address , position , holder)
        }
    }
}

class ViewHolder( itemView : View) : RecyclerView.ViewHolder(itemView){
    val textAddress = itemView.findViewById<TextView>(R.id.text_address)
    val defaultAddress = itemView.findViewById<ImageView>(R.id.default_tick)
    val settingsButton = itemView.findViewById<ImageButton>(R.id.settings_button)
}