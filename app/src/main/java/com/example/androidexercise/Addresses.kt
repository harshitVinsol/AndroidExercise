package com.example.androidexercise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidexercise.AddAddresses.Companion.DEFAULT_KEY
import com.example.androidexercise.AddAddresses.Companion.DEFAULT_SHARED_PREF
import com.example.androidexercise.adapters.AddressAdapter
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_addresses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
An Activity to show all the Addresses available using a recycler view and Adding an Address using a floating action button
*/
class Addresses : AppCompatActivity() {
    companion object{
        const val INTENT_KEY = "CalledTo"
        private const val ADDRESS_LIST = "addressList"
        private lateinit var listOfAddress : ArrayList<Address>
        private var LIST_LOADED = false
        var DEFAULT_ID: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)

        val defaultSharedPref = getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
        DEFAULT_ID = defaultSharedPref.getInt(DEFAULT_KEY, 0)

        val listOfAddressOrientation = savedInstanceState?.getParcelableArrayList<Address>(ADDRESS_LIST)

        address_recycler.layoutManager = LinearLayoutManager(this)

        if(savedInstanceState == null){
            progress_circular_address.isVisible = true
            loadAddress()
        }
        else{
            if(listOfAddressOrientation != null){
                address_recycler.adapter = AddressAdapter(listOfAddressOrientation as MutableList<Address>, this)
                progress_circular_address.isVisible = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (address_recycler.adapter?.itemCount == 0) {
            changeFabToCenter()
        }
        else{
            changeFabToBottom()
        }
    }
    /*
    function to save the list while Orientation is changed
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(LIST_LOADED)
        outState.putParcelableArrayList(ADDRESS_LIST, listOfAddress)
    }

    /*
    function to Load all the available addresses by calling a GET by getAddressList() of AddressService and assigning the list of
    all the available addresses to the addressRecyclerView
     */
    private fun loadAddress() {
        val addressService = ServiceBuilder.buildService(AddressService::class.java)
        val requestCall = addressService.getAddressList()
        requestCall.enqueue(object : Callback<MutableList<Address>>{
            override fun onFailure(call: Call<MutableList<Address>>, t: Throwable) {
                Toast.makeText(this@Addresses, "Failed to load addresses", Toast.LENGTH_SHORT).show()
                LIST_LOADED = false
            }
            override fun onResponse(
                call: Call<MutableList<Address>>,
                response: Response<MutableList<Address>>
            ) {
                if(response.isSuccessful){
                    progress_circular_address.isVisible = false
                    val addressList = response.body() as MutableList<Address>
                    listOfAddress = addressList as ArrayList<Address>
                    LIST_LOADED = true
                    address_recycler.adapter = AddressAdapter(addressList, baseContext)
                    if(address_recycler.adapter?.itemCount == 0){
                        changeFabToCenter()
                    }
                    else{
                        changeFabToBottom()
                    }
                }
                else{
                    LIST_LOADED = false
                    Toast.makeText(this@Addresses, "Failed to load addresses : "+ response.code().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    /*
    function to change the Floating action button to Bottom right from center
     */
    private fun changeFabToBottom(){
        address_foa_bottom.isInvisible = false
        add_book_blank.isInvisible = true
        kindly_add_address.isInvisible = true
        address_foa_centre.isInvisible = true
    }
    /*
    function to change the Floating action button to center from Bottom right
     */
    private fun changeFabToCenter(){
        address_foa_bottom.isInvisible = true
        add_book_blank.isInvisible = false
        kindly_add_address.isInvisible = false
        address_foa_centre.isInvisible = false
    }
    /*
    A function to control onClick of both the Floating action buttons i.e. center and bottom
     */
    fun onFabClick(view : View){
        val intent = Intent(this, AddAddresses::class.java)
        intent.putExtra(INTENT_KEY, "add")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
