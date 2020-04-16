package com.example.androidexercise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View.OnCreateContextMenuListener
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidexercise.adapters.AddressAdapter
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_addresses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext

lateinit var DEFAULT_ID: String
const val INTENT_KEY = "CalledTo"
/*
An Activity to show all the Addresses available using a recycler view and Adding an Address using a floating action button
 */
class Addresses : AppCompatActivity() {
    private lateinit var addressRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)

        addressRecyclerView = findViewById(R.id.address_recycler)
        addressRecyclerView.layoutManager = LinearLayoutManager(this)

        address_foa.setOnClickListener {
            val intent = Intent(this, AddAddresses::class.java)
            intent.putExtra(INTENT_KEY, "add")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val defaultSharedPref = getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
        DEFAULT_ID = defaultSharedPref.getInt(DEFAULT_KEY,0).toString()
        loadAddress()
    }
    /*
    function to Load all the available addresses by calling a GET by getAddressList() of AddressService and assigning the list of
    all the available addresses to the addressRecylerView
     */
    private fun loadAddress(){
        val addressService = ServiceBuilder.buildService(AddressService::class.java)
        val requestCall = addressService.getAddressList()

        requestCall.enqueue(object : Callback<List<Address>>{
            override fun onFailure(call: Call<List<Address>>, t: Throwable) {
                Toast.makeText(this@Addresses, "Failed to load addresses", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Address>>, response: Response<List<Address>>) {
                if(response.isSuccessful){
                    val addressList = response.body() as List<Address>
                    addressRecyclerView.adapter = AddressAdapter(addressList, baseContext)
                }
                else{
                    Toast.makeText(this@Addresses, "Failed to load addresses : "+ response.code().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
