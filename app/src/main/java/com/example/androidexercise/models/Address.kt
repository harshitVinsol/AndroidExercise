package com.example.androidexercise.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
Model data class Address to give a format to the address
 */
@Parcelize
data class Address(
    val id: Int,
    val firstname: String,
    val address1: String,
    val address2: String,
    val city: String,
    val zipcode: String,
    val state_name: String?,
    val alternate_phone: String?,
    val company: String?,
    val state_id: Int?,
    val country_id: Int?
) : Parcelable