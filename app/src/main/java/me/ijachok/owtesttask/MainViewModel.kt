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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: RepositoryImpl) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isNavigating = MutableStateFlow(false)
    val isNavigating = _isNavigating.asStateFlow()

    private val _contentType = MutableStateFlow(ContentType.GAME)
    val contentType = _contentType.asStateFlow()

    private var allIds: Ids? = null
    private var currentTypeId = 0

    init {
        firstStart()
    }

    fun firstStart() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllIds().collectLatest { ids ->
                allIds = ids
                currentTypeId = ids.data.first().id
                repository.getTypeByID(currentTypeId).collectLatest { typeObject ->
                    _contentType.value = typeObject.type
                }
            }
            _isLoading.value = false
        }

    }


    fun nextType() {
        Log.d("abba", "nextType: ${_contentType.value}")
        if (!_isNavigating.value) {
            viewModelScope.launch {
                _isNavigating.value = true
                allIds?.let { currentIds ->

                    currentTypeId =
                        if (currentTypeId == currentIds.data.last().id)
                            currentIds.data.first().id
                        else currentTypeId + 1

                    repository.getTypeByID(currentTypeId).collectLatest { response ->
                       if (response.type != ContentType.GAME) _contentType.value = response.type
                    }
                }
                _isNavigating.value = false
            }
        }
    }

    fun getAllIds() {
        viewModelScope.launch {
            repository.getAllIds().collectLatest { response ->
                Log.d("abba", "getAllIds: ${response.data}")
            }
        }
    }

    fun getTypeByID(id: Int) {
        viewModelScope.launch {
            repository.getTypeByID(id).collectLatest { response ->
                Log.d("abba", "getAllIds: ${response.type}")
            }
        }
    }
}
