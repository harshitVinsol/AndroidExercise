package com.example.androidexercise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidexercise.AddAddressesActivity.Companion.ADDED_ADDRESS
import com.example.androidexercise.AddAddressesActivity.Companion.ADDED_CODE
import com.example.androidexercise.AddAddressesActivity.Companion.DEFAULT_KEY
import com.example.androidexercise.AddAddressesActivity.Companion.DEFAULT_SHARED_PREF
import com.example.androidexercise.AddAddressesActivity.Companion.IS_DEFAULT_KEY
import com.example.androidexercise.AddAddressesActivity.Companion.UPDATED_CODE
import com.example.androidexercise.adapters.AddressAdapter
import com.example.androidexercise.adapters.ViewHolder
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_addresses.*
import kotlinx.android.synthetic.main.item_address.*
import kotlinx.android.synthetic.main.item_address.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
An Activity to show all the Addresses available using a recycler view and Adding an Address using a floating action button
*/
class AddressesActivity : AppCompatActivity() {
    private var listOfAddress = mutableListOf<Address>()
    private lateinit var adapter: AddressAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)

        val defaultSharedPref = getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
        DEFAULT_ID = defaultSharedPref.getInt(DEFAULT_KEY, 0)

        address_recycler.layoutManager = LinearLayoutManager(this)
        adapter =
            AddressAdapter(listOfAddress) { address: Address, position: Int, holder: ViewHolder ->
                createSettingsPopupMenu(address, position, holder)
            }

        if (savedInstanceState == null || !listLoaded) {
            progress_circular_address.isVisible = true
            loadAddress()
        } else {
            listOfAddress =
                savedInstanceState.getParcelableArrayList<Address>(ADDRESS_LIST) as MutableList<Address>
            adapter.setList(listOfAddress)
            address_recycler.adapter = adapter
            showFab()
        }
    }

    /*
    A function to show the FAB at center if the size of listOfAddress is 0 and FAB at bottom right if size of listOfAddress is not 0
     */
    private fun showFab() {
        if (listOfAddress.size == 0) {
            changeFabToCenter()
        } else {
            changeFabToBottom()
        }
    }

    /*
    Creating a Pop up menu to Update or Delete the Address
    */
    private fun createSettingsPopupMenu(address: Address, position: Int, holder: ViewHolder) {
        val popUpMenu = PopupMenu(this, holder.settingsButton)
        val inflater: MenuInflater = popUpMenu.menuInflater
        inflater.inflate(R.menu.setting_menu, popUpMenu.menu)
        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.setting_update -> {
                    updateAddress(address.id, address, position)
                    address_recycler.adapter?.notifyItemChanged(position)
                    true
                }

                R.id.setting_delete -> {
                    progress_circular_address.isVisible = true
                    if (address.id == DEFAULT_ID) {
                        DEFAULT_ID = 0
                        default_tick.isVisible = false
                    }
                    deleteAddress(address.id, position)
                    true
                }

                else -> false
            }
        }
    }

    /*
    function to Delete the address from pop up Menu by calling deleteAddress(id) from AddressService
     */
    private fun deleteAddress(id: Int, position: Int) {
        val addressService = ServiceBuilder.buildService(AddressService::class.java)
        val requestCall = addressService.deleteAddress(id)

        requestCall.enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(
                    this@AddressesActivity,
                    "Failed to Delete Address",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AddressesActivity,
                        "Address Deleted Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (id == DEFAULT_ID) {
                        DEFAULT_ID = 0
                    }
                    listOfAddress.removeAt(position)
                    address_recycler.adapter?.notifyDataSetChanged()
                    progress_circular_address.isVisible = false
                } else {
                    Toast.makeText(
                        this@AddressesActivity,
                        "Failed to Delete Address : " + response.code().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        progress_circular_address.isVisible = false
    }

    /*
    function to Update the address from pop up menu sending an intent to AddAddress activity to input the updated fields
     */
    private fun updateAddress(id: Int, address: Address, position: Int) {
        val intent = Intent(this, AddAddressesActivity::class.java)
        intent.putExtra(ADDRESS_KEY, address)
        intent.putExtra(INTENT_KEY, false)
        intent.putExtra(ADDRESS_ID, id)
        intent.putExtra(ADDRESS_POSITION, position)
        startActivityForResult(intent, REQUEST_CODE)
    }

    /*
    function to save the list while Orientation is changed
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (listLoaded) {
            outState.putParcelableArrayList(ADDRESS_LIST, listOfAddress as ArrayList<Address>)
        }
    }

    /*
    function to Load all the available addresses by calling a GET by getAddressList() of AddressService and assigning the list of
    all the available addresses to the addressRecyclerView
     */
    private fun loadAddress() {
        val addressService = ServiceBuilder.buildService(AddressService::class.java)
        val requestCall = addressService.getAddressList()
        requestCall.enqueue(object : Callback<MutableList<Address>> {
            override fun onFailure(call: Call<MutableList<Address>>, t: Throwable) {
                Toast.makeText(
                    this@AddressesActivity,
                    "Failed to load addresses",
                    Toast.LENGTH_SHORT
                ).show()
                listLoaded = false
            }

            override fun onResponse(
                call: Call<MutableList<Address>>,
                response: Response<MutableList<Address>>
            ) {
                if (response.isSuccessful) {
                    progress_circular_address.isVisible = false
                    listOfAddress = response.body() as ArrayList<Address>
                    listLoaded = true
                    adapter.setList(listOfAddress)
                    address_recycler.adapter = adapter
                    showFab()
                } else {
                    listLoaded = false
                    Toast.makeText(
                        this@AddressesActivity,
                        "Failed to load addresses : " + response.code().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    /*
    function to change the Floating action button to Bottom right from center
     */
    private fun changeFabToBottom() {
        address_foa_bottom.isInvisible = false
        add_book_blank.isInvisible = true
        kindly_add_address.isInvisible = true
        address_foa_centre.isInvisible = true
    }

    /*
    function to change the Floating action button to center from Bottom right
    */
    private fun changeFabToCenter() {
        address_foa_bottom.isInvisible = true
        add_book_blank.isInvisible = false
        kindly_add_address.isInvisible = false
        address_foa_centre.isInvisible = false
    }

    /*
    A function to control onClick of both the Floating action buttons i.e. center and bottom
    */
    fun onFabClick(view: View) {
        val intent = Intent(this, AddAddressesActivity::class.java)
        intent.putExtra(INTENT_KEY, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityForResult(intent, REQUEST_CODE)
    }

    /*
    onActivityResult() to check the results of update and add intent and assign updated listOfAddress to the adapter
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == ADDED_CODE && data != null) {
            val addedAddress = data.getParcelableExtra<Address>(ADDED_ADDRESS)
            DEFAULT_ID = data.getIntExtra(IS_DEFAULT_KEY, 0)
            listOfAddress.add(addedAddress!!)
            adapter.setList(listOfAddress)
            address_recycler.adapter = adapter
            showFab()
        } else if (requestCode == REQUEST_CODE && resultCode == UPDATED_CODE && data != null) {
            val addedAddress = data.getParcelableExtra<Address>(ADDED_ADDRESS)
            DEFAULT_ID = data.getIntExtra(IS_DEFAULT_KEY, 0)
            val position = data.getIntExtra(ADDRESS_POSITION, 0)
            if (addedAddress != null) {
                listOfAddress[position] = addedAddress
                adapter.setList(listOfAddress)
                address_recycler.adapter = adapter
            }
        }
    }

    companion object {
        //DEFAULT_ID is set to 0 if there is no address saved as a default address
        var DEFAULT_ID = 0
        const val REQUEST_CODE = 100
        const val INTENT_KEY = "isAdd"
        private const val ADDRESS_LIST = "addressList"
        private var listLoaded: Boolean = false
        const val ADDRESS_ID = "id"
        const val ADDRESS_KEY = "address"
        const val ADDRESS_POSITION = "position"
    }
}
