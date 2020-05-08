package com.example.androidexercise.services

import com.example.androidexercise.models.Address
import retrofit2.Call
import retrofit2.http.*
/*
An Api interface to write methods that can fire Api calls to the web server
 */
interface AddressService{
    /*
    GET request to return the list of All the Addresses
     */
    @GET("addresses?token=52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78")
    fun getAddressList() : Call<MutableList<Address>>
    /*
    POST request to write an Address
     */
    @FormUrlEncoded
    @POST("addresses?token=52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78")
    fun addAddressToList(
        @Field("address[firstname]") first: String,
        @Field("address[address1]") add1: String,
        @Field("address[address2]") add2: String,
        @Field("address[city]") city: String,
        @Field("address[country_id]") country: Int?,
        @Field("address[state_name]") stateName: String?,
        @Field("address[state_id]") stateId: Int?,
        @Field("address[zipcode]") pincode: String?,
        @Field("address[phone]") phone: String?
        ) : Call<Address>
    /*
    DELETE request to delete an Address by id
     */
    @DELETE("addresses/{id}?token=52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78")
    fun deleteAddress(@Path("id") id : Int) : Call<Unit>
    /*
    PUT request to Update an Address by id
     */
    @FormUrlEncoded
    @PUT("addresses/{id}?token=52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78")
    fun updateAddressById(
        @Path("id") id: Int,
        @Field("address[firstname]") first: String,
        @Field("address[address1]") add1: String,
        @Field("address[city]") city: String,
        @Field("address[country_id]") country: Int?,
        @Field("address[state_id]") state: Int?,
        @Field("address[zipcode]") pincode: String?,
        @Field("address[phone]") phone: String?
    ) : Call<Address>
}
