package com.example.androidexercise.models

import android.os.Parcel
import android.os.Parcelable

/*
Model data class Address to give a format to the address
 */
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstname)
        parcel.writeString(address1)
        parcel.writeString(address2)
        parcel.writeString(city)
        parcel.writeString(zipcode)
        parcel.writeString(state_name)
        parcel.writeString(alternate_phone)
        parcel.writeString(company)
        parcel.writeValue(state_id)
        parcel.writeValue(country_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}