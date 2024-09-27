package me.ijachok.owtesttask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.ijachok.owtesttask.data.RepositoryImpl
import me.ijachok.owtesttask.model.ContentType
import me.ijachok.owtesttask.model.Ids
import me.ijachok.owtesttask.model.TypeObject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: RepositoryImpl) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isNavigating = MutableStateFlow(false)
    val isNavigating = _isNavigating.asStateFlow()

    private val _contentFromApi = MutableStateFlow(TypeObject(0,""))
    val contentFromApi = _contentFromApi.asStateFlow()

    private var allIds: Ids? = null
    private var currentTypeId = 0

    init {
        firstStart()
    }

    fun firstStart() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllIds().collectLatest { idsResponse ->
                if(idsResponse.success && idsResponse.data != null){
                    allIds = idsResponse.data
                    currentTypeId = idsResponse.data.data.first().id
                    repository.getTypeByID(currentTypeId).collectLatest { typeObjectResponse ->
                        if(typeObjectResponse.success && typeObjectResponse.data != null)
                            _contentFromApi.value = typeObjectResponse.data
                    }
                }
            }
            _isLoading.value = false
        }

    }


    fun nextType() {
        if (!_isNavigating.value) {
            viewModelScope.launch {
                _isNavigating.value = true
                allIds?.let { currentIds ->
                    currentTypeId =
                        if (currentTypeId == currentIds.data.last().id)
                            currentIds.data.first().id
                        else currentTypeId + 1

                    repository.getTypeByID(currentTypeId).collectLatest { typeObjectResponse ->
                        if (typeObjectResponse.success && typeObjectResponse.data != null){
                            if (typeObjectResponse.data.type != ContentType.GAME) {
                                _contentFromApi.value = typeObjectResponse.data
                            }
                        }
                    }
                }
                _isNavigating.value = false
            }
        }
    }

    fun getAllIds() {
        viewModelScope.launch {
            repository.getAllIds().collectLatest { idsResponse ->
                Log.d("abba", "getAllIds: ${idsResponse.data}")
            }
        }
    }

    fun getTypeByID(id: Int) {
        viewModelScope.launch {
            repository.getTypeByID(id).collectLatest { typeObjectResponse ->

                Log.d("abba", "getAllIds: ${typeObjectResponse.data}")
            }
        }
    }
}
