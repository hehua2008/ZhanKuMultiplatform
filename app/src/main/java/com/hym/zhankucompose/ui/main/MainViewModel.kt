package com.hym.zhankucompose.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _word = MutableLiveData<String>("")
    val word: LiveData<String> = _word

    fun setSearchWord(word: String) {
        val trim = word.trim()
        if (_word.value == trim) return
        _word.value = trim
    }
}