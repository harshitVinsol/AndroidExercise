package com.example.androidexercise

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import com.example.androidexercise.AddressesActivity.Companion.ADDRESS_ID
import com.example.androidexercise.AddressesActivity.Companion.ADDRESS_KEY
import com.example.androidexercise.AddressesActivity.Companion.ADDRESS_POSITION
import com.example.androidexercise.AddressesActivity.Companion.DEFAULT_ID
import com.example.androidexercise.AddressesActivity.Companion.IS_ADD
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_add_addresses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
An activity to Add and Update the Addresses
 */
class AddAddressesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_addresses)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showAddButton()
        val isAdd = intent.getBooleanExtra(IS_ADD, true)
        val id = intent.getIntExtra(ADDRESS_ID, 0)
        if (!isAdd) {
            if (id == DEFAULT_ID) {
                check_default.isChecked = true
            }
            setUpdateFields()
        }
        /*
        To check if the button has been clicked to Add or Update an Address by Intent
         */
        button_add.setOnClickListener {
            validateAll()
            if (isAdd) {
                addAddress()
            } else {
                val position = intent.getIntExtra(ADDRESS_POSITION, 0)
                updateAddress(id, position)
            }
        }
        /*
        Enabling Enter/DONE to submit form on pincode EditText
         */
        pincode.setOnEditorActionListener { _, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                button_add.performClick()
            }
            false
        }
        validateOnChange()
    }

    /*
    function to Update the address by id and calling a PUT request using updateAddressById() of AddressService
     */
    private fun updateAddress(id: Int, position: Int) {
        if (validateInput()) {
            showProgressBar()
            val mAddress = Address(
                id,
                name.text.toString().trim(),
                add1.text.toString().trim(),
                add2.text.toString().trim(),
                city.text.toString().trim(),
                pincode.text.toString().trim(),
                state.text.toString().trim(),
                null,
                null,
                1400,
                105
            )

            val addressService = ServiceBuilder.buildService(AddressService::class.java)
            val requestCall = addressService.updateAddressById(
                id,
                mAddress.firstname,
                mAddress.address1,
                mAddress.city,
                mAddress.country_id,
                mAddress.state_id,
                mAddress.zipcode,
                "2221"
            )

            requestCall.enqueue(object : Callback<Address> {
                override fun onFailure(call: Call<Address>, t: Throwable) {
                    Toast.makeText(baseContext, R.string.update_address_fail, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onResponse(call: Call<Address>, response: Response<Address>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddAddressesActivity,
                            R.string.update_address_success,
                            Toast.LENGTH_SHORT
                        ).show()

                        val newAddress: Address? = response.body()
                        val defaultSharedPref =
                            getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
                        val editor = defaultSharedPref.edit()
                        if (check_default.isChecked) {
                            val defaultValue = newAddress?.id
                            if (defaultValue != null) {
                                DEFAULT_ID = defaultValue
                                editor.putInt(DEFAULT_KEY, defaultValue)
                                editor.apply()
                            }
                        } else if (DEFAULT_ID == newAddress?.id) {
                            editor.putInt(DEFAULT_KEY, 0)
                            editor.apply()
                            DEFAULT_ID = 0
                        }
                        val intent = Intent()
                        intent.putExtra(ADDED_ADDRESS, newAddress)
                        intent.putExtra(ADDRESS_POSITION, position)
                        intent.putExtra(IS_DEFAULT_KEY, DEFAULT_ID)
                        setResult(UPDATED_CODE, intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddAddressesActivity,
                            R.string.update_address_fail,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            })
        } else {
            showAddButton()
        }
    }

    /*
    function to Add the address and calling a POST request using addAddress() of AddressService
     */
    private fun addAddress() {
        if (validateInput()) {
            showProgressBar()
            val mAddress = Address(
                300,
                name.text.toString().trim(),
                add1.text.toString().trim(),
                add2.text.toString().trim(),
                city.text.toString().trim(),
                pincode.text.toString().trim(),
                null,
                null,
                null,
                1400,
                105
            )

            val addressService = ServiceBuilder.buildService(AddressService::class.java)
            val requestCall = addressService.addAddressToList(
                mAddress.firstname,
                mAddress.address1,
                mAddress.address2,
                mAddress.city,
                mAddress.country_id,
                mAddress.state_name,
                mAddress.state_id,
                mAddress.zipcode,
                "2221"
            )

            requestCall.enqueue(object : Callback<Address> {
                override fun onFailure(call: Call<Address>, t: Throwable) {
                    Toast.makeText(
                        this@AddAddressesActivity,
                        R.string.add_address_fail,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onResponse(call: Call<Address>, response: Response<Address>) {
                    if (response.isSuccessful) {
                        val newAddress: Address? = response.body()
                        Toast.makeText(
                            this@AddAddressesActivity,
                            R.string.add_address_success,
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        if (check_default.isChecked) {
                            val defaultValue = newAddress?.id
                            val defaultSharedPref =
                                getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
                            val editor = defaultSharedPref.edit()
                            if (defaultValue != null) {
                                DEFAULT_ID = defaultValue
                                editor.putInt(DEFAULT_KEY, defaultValue)
                                editor.apply()
                            }
                        }
                        val intent = Intent()
                        intent.putExtra(ADDED_ADDRESS, newAddress)
                        intent.putExtra(IS_DEFAULT_KEY, DEFAULT_ID)
                        setResult(ADDED_CODE, intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddAddressesActivity,
                            R.string.add_address_fail,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            })
        } else {
            showAddButton()
        }
    }

    /*
    A Boolean function to validate the EditText of AddAddress Layout
     */
    private fun validateInput() =
        (validateName() && validateAddress1() && validateAddress2() && validateCity() && validateState() && validatePincode())

    /*
    A Boolean function to validate the name field
     */
    private fun validateName(): Boolean {
        return if (name.text.toString().isBlank()) {
            name_input_layout.error = resources.getString(R.string.proper_name)
            name.requestFocus()
            false
        } else {
            name_input_layout.error = null
            true
        }
    }

    /*
    A Boolean function to validate the Address Line 1 field
     */
    private fun validateAddress1(): Boolean {
        return if (add1.text.toString().isBlank()) {
            add1_input_layout.error = resources.getString(R.string.proper_address)
            add1.requestFocus()
            false
        } else {
            add1_input_layout.error = null
            true
        }
    }

    /*
    A Boolean function to validate the Address Line 2 field
     */
    private fun validateAddress2(): Boolean {
        return if (add2.text.toString().isBlank()) {
            add2_input_layout.error = resources.getString(R.string.proper_address)
            add2.requestFocus()
            false
        } else {
            add2_input_layout.error = null
            true
        }
    }

    /*
    A Boolean function to validate the City field
     */
    private fun validateCity(): Boolean {
        return if (city.text.toString().isBlank()) {
            city_input_layout.error = resources.getString(R.string.proper_city)
            city.requestFocus()
            false
        } else {
            city_input_layout.error = null
            true
        }
    }

    /*
    A Boolean function to validate the State field
     */
    private fun validateState(): Boolean {
        return if (state.text.toString().isBlank()) {
            state_input_layout.error = resources.getString(R.string.proper_state)
            state.requestFocus()
            false
        } else {
            state_input_layout.error = null
            true
        }
    }

    /*
    A Boolean function to validate the Pincode field
     */
    private fun validatePincode(): Boolean {
        return if (pincode.text.toString().length != 6) {
            pincode_input_layout.error = resources.getString(R.string.proper_pincode)
            pincode.requestFocus()
            false
        } else {
            pincode_input_layout.error = null
            true
        }
    }

    /*
    A function to set all the fields while updating an address
     */
    private fun setUpdateFields() {
        val address = intent.getParcelableExtra<Address>(ADDRESS_KEY)
        name.setText(address?.firstname)
        add1.setText(address?.address1)

        if (address?.address2 == null) {
            add2.setText(R.string.add_line_2)
        } else {
            add2.setText(address.address2)
        }

        landmark.setText(R.string.landmark)
        city.setText(address?.city)

        if (address?.state_name == null) {
            state.setText(R.string.state)
        } else {
            state.setText(address.state_name)
        }

        pincode.setText(address?.zipcode)
    }

    /*
    An extension function of EditText that takes a lambda function
     */
    private fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cb(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /*
    A function to validate the fields onChange()
     */
    private fun validateOnChange() {
        name.onChange {
            validateName()
        }
        add1.onChange {
            validateAddress1()
        }
        add2.onChange {
            validateAddress2()
        }
        city.onChange {
            validateCity()
        }
        state.onChange {
            validateState()
        }
        pincode.onChange {
            validatePincode()
        }
    }

    /*
    A function to validate all fields
     */
    private fun validateAll() {
        validateName()
        validateAddress1()
        validateAddress2()
        validateCity()
        validateState()
        validatePincode()
    }

    /*
    A function to show Progress bar and remove add Button
     */
    private fun showProgressBar() {
        button_add.isVisible = false
        progress_bar_add_address.isVisible = true
    }

    /*
    A function to show add Button and remove Progress bar
     */
    private fun showAddButton() {
        button_add.isVisible = true
        progress_bar_add_address.isVisible = false
    }

    companion object {
        const val DEFAULT_SHARED_PREF = "DefaultKeySharedPref"
        const val DEFAULT_KEY = "DefaultKey"
        const val ADDED_ADDRESS = "addedAddress"
        const val IS_DEFAULT_KEY = "isDefault"
        const val ADDED_CODE = 101
        const val UPDATED_CODE = 102
    }
}
