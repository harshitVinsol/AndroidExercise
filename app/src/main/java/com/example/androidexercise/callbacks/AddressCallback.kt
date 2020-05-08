package com.example.androidexercise.callbacks

interface AddressCallback{
    fun createSettingsPopupMenu()
    fun deleteAddress(id : Int, position : Int)
    fun updateAddress(id : Int)
}