package com.hym.zhankukotlin.ui.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.hym.zhankukotlin.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SearchActivity"
    }

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchPagerAdapter = SearchPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = searchPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        binding.searchLayout.apply {
            keywordEdit.doAfterTextChanged {
                val word = (it?.trim() ?: "").toString()
                keywordClear.isVisible = word.isNotEmpty()
                searchViewModel.setWord(word)
            }
            keywordEdit.setOnEditorActionListener { v, actionId, event ->
                if (actionId != EditorInfo.IME_ACTION_SEARCH) {
                    return@setOnEditorActionListener false
                }
                clearEditFocusAndHideSoftInput()
                return@setOnEditorActionListener true
            }

            keywordClear.setOnClickListener {
                keywordEdit.text = null
                clearEditFocusAndHideSoftInput()
            }
        }
    }

    private fun clearEditFocusAndHideSoftInput() {
        binding.searchLayout.keywordEdit.clearFocus()
        val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}