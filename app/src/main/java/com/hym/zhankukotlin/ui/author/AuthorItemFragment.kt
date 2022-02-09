package com.hym.zhankukotlin.ui.author

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.FragmentMainBinding
import com.hym.zhankukotlin.model.*
import com.hym.zhankukotlin.ui.HeaderFooterLoadStateAdapter
import com.hym.zhankukotlin.ui.main.PagingPreviewItemAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

class AuthorItemFragment : Fragment(), Observer<LifecycleOwner> {
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

    private val mPageViewModel: AuthorPageViewModel by viewModels(factoryProducer = { mVMFactory })
    private var mBinding: FragmentMainBinding? = null
    private val binding get() = checkNotNull(mBinding)
    private lateinit var mAuthor: CreatorObj

    private lateinit var mPagingPreviewItemAdapter: PagingPreviewItemAdapter
    private lateinit var mPreviewItemDecoration: RecyclerView.ItemDecoration

    private val mVMFactory = object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (AuthorPageViewModel::class.java.isAssignableFrom(modelClass)) {
                AuthorPageViewModel(mAuthor.id) as T
            } else {
                super.create(modelClass)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(AUTHOR, mAuthor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        mAuthor = activeBundle!!.getParcelable(AUTHOR)!!

        mPagingPreviewItemAdapter = PagingPreviewItemAdapter()

        mPreviewItemDecoration = object : RecyclerView.ItemDecoration() {
            private val mOffset = resources.getDimensionPixelSize(
                R.dimen.preview_item_offset
            ) and 1.inv()
            private val mHalfOffset = mOffset shr 1

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val itemPosition =
                    (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                if (itemPosition and 1 == 0) {
                    outRect.set(mOffset, 0, mHalfOffset, mOffset)
                } else {
                    outRect.set(mHalfOffset, 0, mOffset, mOffset)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        updateCategoryLink()

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        binding.swipeRefresh.setOnRefreshListener { mPagingPreviewItemAdapter.refresh() }

        binding.previewRecycler.addItemDecoration(mPreviewItemDecoration)
        binding.previewRecycler.adapter = mPagingPreviewItemAdapter.withLoadStateFooter(
            HeaderFooterLoadStateAdapter(mPagingPreviewItemAdapter)
        )
        binding.previewRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var mLastState = RecyclerView.SCROLL_STATE_IDLE

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mLastState = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val scaledTouchSlop =
                    ViewConfiguration.getTouchSlop() * recyclerView.resources.displayMetrics.density
                if (mLastState == RecyclerView.SCROLL_STATE_SETTLING &&
                    dy < -scaledTouchSlop && recyclerView.canScrollVertically(-1)
                ) {
                    binding.fab.show()
                } else if (!recyclerView.canScrollVertically(-1) ||
                    (dy > scaledTouchSlop && recyclerView.canScrollVertically(1))
                ) {
                    binding.fab.hide()
                }
            }
        })

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
        binding.fab.hide()
        binding.fab.setOnClickListener {
            binding.previewRecycler.scrollToPosition(0)
        }

        return binding.root
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
        binding.previewRecycler.adapter = null
        binding.previewRecycler.clearOnScrollListeners()
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
            mPageViewModel.mediatorLiveData.observe(viewLifecycleOwner) {
                mPagingPreviewItemAdapter.refresh()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mPagingPreviewItemAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mPagingPreviewItemAdapter.loadStateFlow
                // Only emit when REFRESH LoadState changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.previewRecycler.scrollToPosition(0) }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mPageViewModel.pagingFlow.collectLatest { pagingData ->
                mPagingPreviewItemAdapter.submitData(pagingData)
            }
        }
    }
}