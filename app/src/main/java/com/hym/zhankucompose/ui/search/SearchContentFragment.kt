package com.hym.zhankucompose.ui.search

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankucompose.MyAppViewModel
import com.hym.zhankucompose.R
import com.hym.zhankucompose.databinding.FragmentMainBinding
import com.hym.zhankucompose.getAppViewModel
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.HeaderFooterLoadStateAdapter
import com.hym.zhankucompose.ui.TabReselectedCallback
import com.hym.zhankucompose.ui.main.MainViewModel
import com.hym.zhankucompose.ui.main.PagingPreviewItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlin.collections.set

@AndroidEntryPoint
class SearchContentFragment : Fragment(), Observer<LifecycleOwner>, TabReselectedCallback {
    companion object {
        private const val TAG = "SearchContentFragment"
        const val CONTENT_TYPE = "CONTENT_TYPE"

        @JvmStatic
        fun newInstance(contentType: ContentType): SearchContentFragment {
            val fragment = SearchContentFragment()
            fragment.arguments = Bundle().apply {
                putString(CONTENT_TYPE, contentType.name)
            }
            return fragment
        }
    }

    private val mMainViewModel: MainViewModel by activityViewModels()
    private val mPageViewModel: SearchContentPageViewModel by viewModels()
    private var mBinding: FragmentMainBinding? = null
    private val binding get() = checkNotNull(mBinding)
    private lateinit var mContentType: ContentType

    private lateinit var mPagingPreviewItemAdapter: PagingPreviewItemAdapter
    private lateinit var mCategoryItemLayoutManager: FlexboxLayoutManager
    private lateinit var mCategoryItemAdapter: TopCateItemAdapter
    private lateinit var mButtonItemDecoration: RecyclerView.ItemDecoration
    private lateinit var mPreviewItemDecoration: RecyclerView.ItemDecoration

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CONTENT_TYPE, mContentType.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        mContentType = ContentType.valueOf(activeBundle!!.getString(CONTENT_TYPE)!!)
        mPageViewModel.contentType = mContentType

        mPagingPreviewItemAdapter = PagingPreviewItemAdapter()
        mCategoryItemLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
        mCategoryItemLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        mCategoryItemAdapter = TopCateItemAdapter(mPageViewModel)

        mButtonItemDecoration = object : RecyclerView.ItemDecoration() {
            private val mOffset = resources.getDimensionPixelSize(
                R.dimen.button_item_horizontal_offset
            ) and 1.inv()
            private val mHalfOffset = mOffset shr 1

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val itemPosition =
                    (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val itemCount = state.itemCount
                val left = if (itemPosition == 0) mOffset else mHalfOffset
                val right = if (itemPosition == itemCount - 1) mOffset else mHalfOffset
                outRect.set(left, 0, right, 0)
            }
        }
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

        binding.previewHeader.categoryLink.isVisible = false

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

        binding.previewHeader.catRecrcler.layoutManager = mCategoryItemLayoutManager
        binding.previewHeader.catRecrcler.addItemDecoration(mButtonItemDecoration)
        binding.previewHeader.catRecrcler.adapter = mCategoryItemAdapter

        binding.previewHeader.order1.text = RecommendLevel.ALL_LEVEL.text
        binding.previewHeader.order2.text = RecommendLevel.EDITOR_CHOICE.text
        binding.previewHeader.order3.text = RecommendLevel.ALL_RECOMMEND.text
        binding.previewHeader.order4.text = RecommendLevel.HOME_RECOMMEND.text
        binding.previewHeader.orderGroup.setOnCheckedChangeListener { group, checkedId ->
            val recommendLevel: RecommendLevel = when (checkedId) {
                R.id.order_1 -> RecommendLevel.ALL_LEVEL
                R.id.order_2 -> RecommendLevel.EDITOR_CHOICE
                R.id.order_3 -> RecommendLevel.ALL_RECOMMEND
                R.id.order_4 -> RecommendLevel.HOME_RECOMMEND
                else -> RecommendLevel.ALL_LEVEL
            }
            mPageViewModel.setRecommendLevel(recommendLevel)
        }

        binding.previewHeader.secondOrder1.text = SortOrder.BEST_MATCH.text
        binding.previewHeader.secondOrder2.text = SortOrder.LATEST_PUBLISH.text
        binding.previewHeader.secondOrder3.text = SortOrder.MOST_RECOMMEND.text
        binding.previewHeader.secondOrder4.text = SortOrder.MOST_COMMENT.text
        binding.previewHeader.secondOrderGroup.setOnCheckedChangeListener { group, checkedId ->
            val sortOrder: SortOrder = when (checkedId) {
                R.id.second_order_1 -> SortOrder.BEST_MATCH
                R.id.second_order_2 -> SortOrder.LATEST_PUBLISH
                R.id.second_order_3 -> SortOrder.MOST_RECOMMEND
                R.id.second_order_4 -> SortOrder.MOST_COMMENT
                else -> SortOrder.BEST_MATCH
            }
            mPageViewModel.setSortOrder(sortOrder)
        }
        binding.previewHeader.secondOrderGroup.isVisible = true

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
            scrollToTop()
        }

        return binding.root
    }

    override fun onTabReselected() {
        scrollToTop()
    }

    private fun scrollToTop() {
        binding.previewRecycler.run {
            scrollToPosition(0)
            post {
                nestedScrollBy(0, -binding.previewHeader.root.height)
            }
        }
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
        binding.previewHeader.catRecrcler.layoutManager = null
        binding.previewHeader.catRecrcler.adapter = null
        mBinding = null
    }

    override fun onChanged(viewLifecycleOwner: LifecycleOwner) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            getAppViewModel<MyAppViewModel>().categoryItems.observe(viewLifecycleOwner) { topCates ->
                val titleUrlMap: MutableMap<String, TopCate?> = LinkedHashMap(1 + topCates.size)
                titleUrlMap["全部"] = null
                topCates.forEach {
                    titleUrlMap[it.name] = it
                }
                mCategoryItemAdapter.setNameValueMap(titleUrlMap)
            }

            mMainViewModel.word.observe(viewLifecycleOwner) {
                mPageViewModel.setWord(it)
            }
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