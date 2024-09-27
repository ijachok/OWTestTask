package me.ijachok.owtesttask.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.ijachok.owtesttask.model.Ids
import me.ijachok.owtesttask.model.TypeObject
import java.io.EOFException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val api: Api):Repository {
    override suspend fun getAllIds(): Flow<Ids> {
        return flow {
            val response = try {
                api.getAllIds()
            } catch (e: Exception) {
                Log.d("catch", "getAllIds: ${e.stackTraceToString()}")
                return@flow
            }
            emit(response)
        }
    }

    override suspend fun getTypeByID(id: Int): Flow<TypeObject> {
        return flow {
            val response = try {
                api.getTypeByID(id)
            } catch (e: Exception) {
                Log.e("catch", "getTypeByID: ${e.stackTraceToString()}")
                return@flow
            }
            emit(response)
        }
    }


}