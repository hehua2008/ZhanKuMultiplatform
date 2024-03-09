package com.hym.zhankucompose.ui.author

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.databinding.FragmentMainBinding
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.ui.TabReselectedCallback
import com.hym.zhankucompose.ui.main.PreviewLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorItemFragment : Fragment(), Observer<LifecycleOwner>, TabReselectedCallback {
    companion object {
        private const val TAG = "AuthorItemFragment"
        const val AUTHOR = "AUTHOR"

        @JvmStatic
        fun newInstance(author: CreatorObj): AuthorItemFragment {
            val fragment = AuthorItemFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(AUTHOR, author)
            }
            return fragment
        }
    }

    private val mPageViewModel: AuthorPageViewModel by viewModels()
    private var mBinding: FragmentMainBinding? = null
    private val binding get() = checkNotNull(mBinding)
    private lateinit var mAuthor: CreatorObj

    private var scrollToTop: () -> Unit = EMPTY_BLOCK

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(AUTHOR, mAuthor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        mAuthor = activeBundle!!.getParcelable(AUTHOR)!!
        mPageViewModel.authorUid = mAuthor.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        updateCategoryLink()

        binding.previewHeader.catRecrcler.isVisible = false

        binding.previewHeader.order1.text = SortOrder.LATEST_PUBLISH.text
        binding.previewHeader.order2.text = SortOrder.MOST_RECOMMEND.text
        binding.previewHeader.order3.text = SortOrder.MOST_FAVORITE.text
        binding.previewHeader.order4.text = SortOrder.MOST_COMMENT.text
        binding.previewHeader.orderGroup.setOnCheckedChangeListener { group, checkedId ->
            val sortOrder: SortOrder = when (checkedId) {
                R.id.order_1 -> SortOrder.LATEST_PUBLISH
                R.id.order_2 -> SortOrder.MOST_RECOMMEND
                R.id.order_3 -> SortOrder.MOST_FAVORITE
                R.id.order_4 -> SortOrder.MOST_COMMENT
                else -> SortOrder.LATEST_PUBLISH
            }
            mPageViewModel.setSortOrder(sortOrder)
        }

        binding.previewHeader.paged.prePage.setOnClickListener {
            clearEditFocusAndHideSoftInput()
            val curPage = mPageViewModel.page.value ?: return@setOnClickListener
            mPageViewModel.setPage(curPage - 1)
        }
        binding.previewHeader.paged.nextPage.setOnClickListener {
            clearEditFocusAndHideSoftInput()
            val curPage = mPageViewModel.page.value ?: return@setOnClickListener
            mPageViewModel.setPage(curPage + 1)
        }
        binding.previewHeader.paged.jumpButton.setOnClickListener {
            clearEditFocusAndHideSoftInput()
            val numberEdit = binding.previewHeader.paged.numberEdit.text.toString()
            if (numberEdit.isEmpty()) return@setOnClickListener
            mPageViewModel.setPage(numberEdit.toInt())
        }

        binding.previewCompose.setContent {
            val lazyPagingItems = mPageViewModel.pagingFlow.collectAsLazyPagingItems()
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(lazyPagingItems, lifecycleOwner) {
                val observer = Observer<Unit> { lazyPagingItems.refresh() }
                mPageViewModel.mediatorLiveData.observe(lifecycleOwner, observer)

                onDispose {
                    mPageViewModel.mediatorLiveData.removeObserver(observer)
                }
            }

            PreviewLayout(lazyPagingItems = lazyPagingItems, setOnScrollToTopAction = {
                scrollToTop = it
            })
        }

        return binding.root
    }

    override fun onTabReselected() {
        scrollToTop()
    }

    private fun updateCategoryLink() {
        val desc = "https://www.zcool.com.cn/u/${mAuthor.id}"
        binding.previewHeader.categoryLink.isVisible = desc.isNotBlank()
        binding.previewHeader.categoryLink.text = desc
    }

    private fun clearEditFocusAndHideSoftInput() {
        binding.previewHeader.paged.numberEdit.clearFocus()
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollToTop = EMPTY_BLOCK
        mBinding = null
    }

    override fun onChanged(viewLifecycleOwner: LifecycleOwner) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mPageViewModel.page.observe(viewLifecycleOwner) {
                binding.previewHeader.paged.root.activePage = it
            }
            mPageViewModel.totalPages.observe(viewLifecycleOwner) {
                binding.previewHeader.paged.root.lastPage = it
            }
        }
    }
}