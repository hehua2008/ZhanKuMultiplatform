package com.hym.zhankucompose.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hym.zhankucompose.flow.MutableSharedData
import com.hym.zhankucompose.flow.SharedData
import com.hym.zhankucompose.navigation.DetailsArgs
import com.hym.zhankucompose.navigation.ImagePagerArgs
import com.hym.zhankucompose.navigation.NavArgs
import com.hym.zhankucompose.navigation.TagListArgs
import com.hym.zhankucompose.navigation.WebViewArgs
import com.hym.zhankucompose.util.WeakMap

class MainViewModel : ViewModel() {
    var selectedPage by mutableIntStateOf(0)

    private val _word = MutableSharedData<String>("")
    val word: SharedData<String> = _word

    fun setSearchWord(word: String) {
        val trim = word.trim()
        if (_word.value == trim) return
        _word.value = trim
    }

    var argumentsMap = WeakMap<String, NavArgs>()

    var detailsArguments: DetailsArgs? = null

    var tagListArguments: TagListArgs? = null

    var imagePagerArguments: ImagePagerArgs? = null

    var webViewArguments: WebViewArgs? = null
}
