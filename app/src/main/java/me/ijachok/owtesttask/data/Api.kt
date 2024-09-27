package me.ijachok.owtesttask.data

import me.ijachok.owtesttask.model.Ids
import me.ijachok.owtesttask.model.TypeObject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("entities/getAllIds")
    suspend fun getAllIds():Ids

    @GET("object/{id}")
    suspend fun getTypeByID(@Path("id")id:Int):TypeObject

    companion object{
        const val BASE_URL = "http://demo3005513.mockable.io/api/v1/"
    }
}