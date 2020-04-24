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
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
Class AddressAdapter to be used in RecyclerView for showing addresses having list of Address and context of Activity called from
 */
class AddressAdapter(val addressList: MutableList<Address> , val mContext: Context) : RecyclerView.Adapter<ViewHolder>(){
    /*
    function to attach a layout to the ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return ViewHolder(v)
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
        val id = DEFAULT_ID?.toInt()
        if(address.id == id){
            holder.defaultAddress.isInvisible = false
        }
        /*
        Creating a Pop up menu to Update or Delete the Address
         */
        holder.settingsButton.setOnClickListener{
            val popUpMenu: PopupMenu = PopupMenu(mContext, holder.settingsButton)
            val inflater: MenuInflater = popUpMenu.menuInflater
            inflater.inflate(R.menu.setting_menu, popUpMenu.menu)
            popUpMenu.show()

            popUpMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.setting_update -> {
                        updateAddress(address.id, address)
                        true
                    }

                    R.id.setting_delete ->{
                        if(address.id == DEFAULT_ID?.toInt()){
                            DEFAULT_ID = null
                            holder.defaultAddress.isInvisible = true
                        }
                        deleteAddress(address.id, holder.adapterPosition)
                        true
                    }
                    
                    else -> false
                }
            }
        }
    }

    /*
    function to Delete the address from pop up Menu by calling deleteAddress(id) from AddressService
     */
    private fun deleteAddress(id: Int, position: Int){
        val addressService = ServiceBuilder.buildService(AddressService::class.java)
        val requestCall = addressService.deleteAddress(id)

        requestCall.enqueue(object : Callback<Unit>{
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(mContext, "Failed to Delete Address", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if(response.isSuccessful){
                    Toast.makeText(mContext, "Address Deleted Successfully", Toast.LENGTH_SHORT).show()
                    if(id == DEFAULT_ID?.toInt()) {
                        DEFAULT_ID = null
                    }
                    addressList.removeAt(position)
                    notifyDataSetChanged()

                    if(addressList.size == 0){
                        val intent = Intent(mContext, Addresses::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(mContext, intent, null)
                    }
                }
                else{
                    Toast.makeText(mContext, "Failed to Delete Address : "+ response.code().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    /*
    function to Update the address from pop up menu sending an intent to AddAddress activity to input the updated fields
     */
    private fun updateAddress(id: Int, address: Address){
        val add: Bundle = bundleOf(
            "name" to address.firstname,
            "add1" to address.address1,
            "add2" to address.address2,
            "city" to address.city,
            "state" to address.state_name,
            "pincode" to address.zipcode
        )
        val intent = Intent(mContext, AddAddresses::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(INTENT_KEY, "update")
        intent.putExtra("id", id)
        intent.putExtra("address", add)
        startActivity(mContext, intent, null)
    }
}

class ViewHolder( itemView : View) : RecyclerView.ViewHolder(itemView){
    val textAddress = itemView.findViewById<TextView>(R.id.address)
    val defaultAddress = itemView.findViewById<ImageView>(R.id.default_tick)
    val settingsButton = itemView.findViewById<ImageButton>(R.id.settings_button)
}