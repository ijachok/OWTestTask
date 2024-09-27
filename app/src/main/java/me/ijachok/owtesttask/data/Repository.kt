package me.ijachok.owtesttask.data

import kotlinx.coroutines.flow.Flow
import me.ijachok.owtesttask.model.Ids
import me.ijachok.owtesttask.model.Response
import me.ijachok.owtesttask.model.TypeObject

interface Repository {
    suspend fun getAllIds(): Flow<Response<Ids>>
    suspend fun getTypeByID(id:Int):Flow<Response<TypeObject>>
}