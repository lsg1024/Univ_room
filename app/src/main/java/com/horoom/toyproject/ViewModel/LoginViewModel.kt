package com.horoom.toyproject.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult : LiveData<Boolean> = _loginResult

    fun login(userId: String, password: String) {
        // 로그인 로직
        _loginResult.value = userId == "" && password == ""
    }
}