package me.ijachok.owtesttask.model

data class Response<T>(val success:Boolean, val data:T? = null)