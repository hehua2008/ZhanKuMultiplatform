package com.hym.zhankukotlin.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.hym.zhankukotlin.databinding.FragmentSearchBinding
import com.hym.zhankukotlin.ui.TabReselectedCallback
import com.hym.zhankukotlin.ui.main.MainViewModel

class SearchFragment : Fragment(), OnTabSelectedListener, TabReselectedCallback {
    companion object {
        private const val TAG = "SearchFragment"
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var searchPagerAdapter: SearchPagerAdapter
    private var mBinding: FragmentSearchBinding? = null
    private val binding get() = checkNotNull(mBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchPagerAdapter = SearchPagerAdapter(childFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false).apply {
            viewPager.adapter = searchPagerAdapter
            tabs.setupWithViewPager(viewPager)
            tabs.addOnTabSelectedListener(this@SearchFragment)
            searchLayout.run {
                keywordEdit.doAfterTextChanged {
                    val word = (it?.trim() ?: "").toString()
                    keywordClear.isVisible = word.isNotEmpty()
                    mainViewModel.setSearchWord(word)
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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        binding.tabs.removeOnTabSelectedListener(this)
        mBinding = null
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        onTabReselected()
    }

    override fun onTabReselected() {
        searchPagerAdapter.currentFragment?.let {
            if (it is TabReselectedCallback) {
                it.onTabReselected()
            }
        }
    }

    private fun clearEditFocusAndHideSoftInput() {
        binding.searchLayout.keywordEdit.clearFocus()
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}