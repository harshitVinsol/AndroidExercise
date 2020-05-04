package com.example.androidexercise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.example.androidexercise.models.Address
import com.example.androidexercise.services.AddressService
import com.example.androidexercise.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_add_addresses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val DEFAULT_SHARED_PREF= "DefaultKeySharedPref"
const val DEFAULT_KEY= "DefaultKey"
/*
An activity to Add and Update the Addresses
 */
class AddAddresses : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_addresses)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val calledTo = intent.getStringExtra(INTENT_KEY)
        val id = intent.getIntExtra("id",0)
        if(calledTo == "update"){
            if(id == DEFAULT_ID) {
                check_default.isChecked = true
            }
            setUpdateFields()
        }
        /*
        To check if the button has been clicked to Add or Update an Address by Intent
         */
        button_add.setOnClickListener {
            validateAll()
            button_add.isEnabled = false

            if(calledTo == "add"){
                addAddress()
            }
            else{
                updateAddress(id)
            }
        }
        validateOnChange()
        /*
        Enabling Enter/DONE to submit form on pincode EditText
         */
        pincode.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)){
                    button_add.performClick()
                }
                return false
            }
        })
    }
    /*
    function to Update the address by id and calling a PUT request using updateAddressById() of AddressService
     */
    private fun updateAddress(id: Int){
        if(validateInput()){
            val mAddress = Address(
                300,
                name.text.toString(),
                add1.text.toString(),
                add2.text.toString(),
                city.text.toString(),
                pincode.text.toString(),
                state.text.toString(),
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
                    Toast.makeText(baseContext, "Failed to Update the Address", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Address>, response: Response<Address>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Address updated Successfully", Toast.LENGTH_SHORT)
                            .show()

                        val newAddress: Address? = response.body()
                        val defaultSharedPref =
                            getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
                        val editor = defaultSharedPref.edit()
                        if (check_default.isChecked) {
                            val defaultValue = newAddress?.id
                            if (defaultValue != null) {
                                editor.putInt(DEFAULT_KEY, defaultValue)
                                editor.apply()
                            }
                        }
                        else{
                            if(DEFAULT_ID == newAddress?.id) {
                                editor.putInt(DEFAULT_KEY, 0)
                                editor.apply()
                                DEFAULT_ID = 0
                            }
                        }
                        val intent = Intent(this@AddAddresses, Addresses::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(baseContext, "Failed to update the Address : " + response.code().toString(), Toast.LENGTH_SHORT ).show()
                    }
                }
            })
        }
        else{
            button_add.isEnabled = true
        }
    }
    /*
    function to Add the address and calling a POST request using addAddress() of AddressService
     */
    private fun addAddress() {
        if (validateInput()) {
            val mAddress = Address(
                300,
                name.text.toString(),
                add1.text.toString(),
                add2.text.toString(),
                city.text.toString(),
                pincode.text.toString(),
                state.text.toString(),
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
                    Toast.makeText(baseContext, "Failed to add the Address", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Address>, response: Response<Address>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Address added Successfully", Toast.LENGTH_SHORT)
                            .show()

                        if (check_default.isChecked) {
                            val newAddress: Address? = response.body()
                            val defaultValue = newAddress?.id
                            val defaultSharedPref =
                                getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
                            val editor = defaultSharedPref.edit()
                            if (defaultValue != null) {
                                editor.putInt(DEFAULT_KEY, defaultValue)
                                editor.apply()
                            }
                        }
                        val intent = Intent(this@AddAddresses, Addresses::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(baseContext, "Failed to add the Address : " + response.code().toString(), Toast.LENGTH_SHORT ).show()
                    }
                }
            })
        }
        else{
            button_add.isEnabled = true
        }
    }
    /*
    A Boolean function to validate the EditText of AddAddress Layout
     */
    private fun validateInput() = (validateName() && validateAddress1() && validateAddress2() && validateCity() && validateState() && validatePincode())
    /*
    A Boolean function to validate the name field
     */
    private fun validateName() : Boolean{
        if(name.text.toString().isBlank()){
            name_input_layout.error = "Enter a proper name"
            name.requestFocus()
            return false
        }
        else{
            name_input_layout.error = null
            return true
        }
    }
    /*
    A Boolean function to validate the Address Line 1 field
     */
    private fun validateAddress1() : Boolean{
        if(add1.text.toString().isBlank()){
            add1_input_layout.error = "Enter a proper Address"
            add1.requestFocus()
            return false
        }
        else{
            add1_input_layout.error = null
            return true
        }
    }
    /*
    A Boolean function to validate the Address Line 2 field
     */
    private fun validateAddress2() : Boolean{
        if(add1.text.toString().isBlank()){
            add2_input_layout.error = "Enter a proper Address"
            add2.requestFocus()
            return false
        }
        else{
            add2_input_layout.error = null
            return true
        }
    }
    /*
    A Boolean function to validate the City field
     */
    private fun validateCity() : Boolean{
        if(city.text.toString().isBlank()){
            city_input_layout.error = "Enter a proper City"
            city.requestFocus()
            return false
        }
        else{
            city_input_layout.error = null
            return true
        }
    }
    /*
    A Boolean function to validate the State field
     */
    private fun validateState() : Boolean{
        if(state.text.toString().isBlank()){
            state_input_layout.error = "Enter a proper State"
            state.requestFocus()
            return false
        }
        else{
            state_input_layout.error = null
            return true
        }
    }
    /*
    A Boolean function to validate the Pincode field
     */
    private fun validatePincode() : Boolean{
        if(pincode.text.toString().length != 6){
            pincode_input_layout.error = "Enter a proper Pincode of six digits"
            pincode.requestFocus()
            return false
        }
        else{
            pincode_input_layout.error = null
            return true
        }
    }

    private fun setUpdateFields(){
        val address = intent.getBundleExtra("address")
        name.setText(address?.get("name").toString())
        add1.setText(address?.get("add1").toString())

        if(address?.get("add2") == null){
            add2.setText(R.string.add_line_2)
        }
        else {
            add2.setText(address.get("add2").toString())
        }

        landmark.setText(R.string.landmark)
        city.setText(address?.get("city").toString())

        if(address?.get("state") == null){
            state.setText(R.string.state)
        }
        else {
            state.setText(address.get("state").toString())
        }

        pincode.setText(address?.get("pincode").toString())
    }
    /*
    An extension function of EditText that takes a lambda function
     */
    private fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { cb(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    /*
    A function to validate the fields onChange()
     */
    private fun validateOnChange(){
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
    private fun validateAll(){
        validateName()
        validateAddress1()
        validateAddress2()
        validateCity()
        validateState()
        validatePincode()
    }
}
