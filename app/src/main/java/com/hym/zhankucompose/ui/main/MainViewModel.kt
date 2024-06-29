package com.hym.zhankucompose.ui.main

import androidx.lifecycle.ViewModel
import com.hym.zhankucompose.flow.MutableSharedData
import com.hym.zhankucompose.flow.SharedData

class MainViewModel : ViewModel() {
    private val _word = MutableSharedData<String>("")
    val word: SharedData<String> = _word

    fun setSearchWord(word: String) {
        val trim = word.trim()
        if (_word.value == trim) return
        _word.value = trim
    }
}