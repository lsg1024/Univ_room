package com.example.toyproject.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.DTO.room_result
import com.example.toyproject.`interface`.Retrofit_API
import kotlinx.coroutines.launch

class MapViewModel() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }
    val text: LiveData<String> = _text

}