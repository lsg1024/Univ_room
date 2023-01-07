package com.example.toyproject.ui.userpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserPageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is userpage Fragment"
    }
    val text: LiveData<String> = _text
}