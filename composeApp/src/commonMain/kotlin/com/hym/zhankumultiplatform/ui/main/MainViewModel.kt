package com.hym.zhankumultiplatform.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hym.zhankumultiplatform.flow.MutableSharedData
import com.hym.zhankumultiplatform.flow.SharedData

class MainViewModel : ViewModel() {
    var selectedPage by mutableIntStateOf(0)

    private val _word = MutableSharedData<String>("")
    val word: SharedData<String> = _word

    fun setSearchWord(word: String) {
        val trim = word.trim()
        if (_word.value == trim) return
        _word.value = trim
    }
}
