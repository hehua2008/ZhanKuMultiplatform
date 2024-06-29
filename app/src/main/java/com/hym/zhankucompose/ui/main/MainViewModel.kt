package com.hym.zhankucompose.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hym.zhankucompose.flow.MutableSharedData
import com.hym.zhankucompose.flow.SharedData
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.photo.UrlPhotoInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class MainViewModel : ViewModel() {
    var selectedPage by mutableIntStateOf(0)

    private val _word = MutableSharedData<String>("")
    val word: SharedData<String> = _word

    fun setSearchWord(word: String) {
        val trim = word.trim()
        if (_word.value == trim) return
        _word.value = trim
    }

    var contentType: ContentType = ContentType.WORK
    var contentId: String = ""

    var author: CreatorObj? = null
    var topCate: TopCate? = null
    var subCate: SubCate? = null

    var photoInfos: ImmutableList<UrlPhotoInfo> = persistentListOf()
    var currentPosition: Int = 0
}
