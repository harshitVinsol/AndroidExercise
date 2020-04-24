package com.example.androidexercise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidexercise.adapters.AddressAdapter
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_addresses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var DEFAULT_ID: String? = null
const val INTENT_KEY = "CalledTo"
/*
An Activity to show all the Addresses available using a recycler view and Adding an Address using a floating action button
*/
class Addresses : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)

        val defaultSharedPref = getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
        DEFAULT_ID = defaultSharedPref.getInt(DEFAULT_KEY,0).toString()

        loadAddress()

        if(address_recycler.adapter?.itemCount == 0){
            changeFab()
        }
        address_recycler.layoutManager = LinearLayoutManager(this)
    }

    /*
    function to Load all the available addresses by calling a GET by getAddressList() of AddressService and assigning the list of
    all the available addresses to the addressRecylerView
     */
    private fun loadAddress(){
        val addressService = ServiceBuilder.buildService(AddressService::class.java)
        val requestCall = addressService.getAddressList()

        requestCall.enqueue(object : Callback<MutableList<Address>>{
            override fun onFailure(call: Call<MutableList<Address>>, t: Throwable) {
                Toast.makeText(this@Addresses, "Failed to load addresses", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                call: Call<MutableList<Address>>,
                response: Response<MutableList<Address>>
            ) {
                if(response.isSuccessful){
                    val addressList = response.body() as MutableList<Address>
                    address_recycler.adapter =
                        AddressAdapter(
                            addressList,
                            baseContext
                        )
                    if(address_recycler.adapter?.itemCount == 0){
                        changeFab()
                    }
                    else{
                        address_foa_bottom.isInvisible = false
                    }
                }
                else{
                    Toast.makeText(this@Addresses, "Failed to load addresses : "+ response.code().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    /*
    function to change the Floating action button if the addressList is empty
     */
    fun changeFab(){
        address_foa_bottom.isInvisible = true
        add_book_blank.isInvisible = false
        kindly_add_address.isInvisible = false
        address_foa_centre.isInvisible = false
    }
    /*
    A function to control onClick of both the Floating action buttons i.e. center and bottom
     */
    public fun onFabClick(view : View){
        val intent = Intent(this, AddAddresses::class.java)
        intent.putExtra(INTENT_KEY, "add")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
