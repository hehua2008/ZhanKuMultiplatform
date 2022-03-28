package com.hym.zhankukotlin.ui.main

import android.graphics.Rect
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideAppExtension
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.FragmentMainBinding
import com.hym.zhankukotlin.model.RecommendLevel
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.FastScroller
import com.hym.zhankukotlin.ui.HeaderFooterLoadStateAdapter
import com.hym.zhankukotlin.ui.TabReselectedCallback
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

class PreviewItemFragment : Fragment(), Observer<LifecycleOwner>, TabReselectedCallback {
    companion object {
        private const val TAG = "PreviewItemFragment"
        const val TOP_CATE = "TOP_CATE"
        const val SUB_CATE = "SUB_CATE"

        @JvmStatic
        fun newInstance(topCate: TopCate? = null, subCate: SubCate? = null): PreviewItemFragment {
            val fragment = PreviewItemFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(TOP_CATE, topCate)
                putParcelable(SUB_CATE, subCate)
            }
            return fragment
        }
    }

    var topCate: TopCate? = null
        private set
    private var mSubCate: SubCate? = null
    private val mPageViewModel: PreviewPageViewModel by viewModels(factoryProducer = { mVMFactory })
    private var mBinding: FragmentMainBinding? = null
    private val binding get() = checkNotNull(mBinding)
    private lateinit var mRequestManager: GlideRequests

    private lateinit var mPagingPreviewItemAdapter: PagingPreviewItemAdapter
    private lateinit var mCategoryItemLayoutManager: FlexboxLayoutManager
    private lateinit var mCategoryItemAdapter: CategoryItemAdapter
    private lateinit var mButtonItemDecoration: RecyclerView.ItemDecoration
    private lateinit var mPreviewItemDecoration: RecyclerView.ItemDecoration

    private val mVMFactory = object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (PreviewPageViewModel::class.java.isAssignableFrom(modelClass)) {
                PreviewPageViewModel(topCate).apply { setSubCate(mSubCate) } as T
            } else {
                super.create(modelClass)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TOP_CATE, topCate)
        outState.putParcelable(SUB_CATE, mSubCate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        topCate = activeBundle!!.getParcelable(TOP_CATE)
        mSubCate = activeBundle.getParcelable(SUB_CATE)

        mPagingPreviewItemAdapter = PagingPreviewItemAdapter()
        mCategoryItemLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
        mCategoryItemLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        mCategoryItemAdapter = CategoryItemAdapter(topCate, mSubCate, mPageViewModel)

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

        mRequestManager = GlideApp.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        updateBackgroundImage()
        updateCategoryLink()

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        binding.swipeRefresh.setOnRefreshListener { mPagingPreviewItemAdapter.refresh() }

        val theme = requireContext().theme
        val thumbDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.fast_scrollbar_thumb_bg, theme)
                    as StateListDrawable
        val trackDrawable =
            ResourcesCompat.getDrawable(resources, android.R.color.transparent, null)
        FastScroller(
            binding.previewRecycler, thumbDrawable, trackDrawable, thumbDrawable, trackDrawable
        )

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

        binding.previewHeader.order1.text = RecommendLevel.EDITOR_CHOICE.text
        binding.previewHeader.order2.text = RecommendLevel.ALL_RECOMMEND.text
        binding.previewHeader.order3.text = RecommendLevel.HOME_RECOMMEND.text
        binding.previewHeader.order4.text = RecommendLevel.LATEST_PUBLISH.text
        binding.previewHeader.orderGroup.setOnCheckedChangeListener { group, checkedId ->
            val recommendLevel: RecommendLevel = when (checkedId) {
                R.id.order_1 -> RecommendLevel.EDITOR_CHOICE
                R.id.order_2 -> RecommendLevel.ALL_RECOMMEND
                R.id.order_3 -> RecommendLevel.HOME_RECOMMEND
                R.id.order_4 -> RecommendLevel.LATEST_PUBLISH
                else -> RecommendLevel.EDITOR_CHOICE
            }
            mPageViewModel.setRecommendLevel(recommendLevel)
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
        binding.previewHeader.paged.pageSizeTitle.isVisible = true
        binding.previewHeader.paged.pageSizeSpinner.isVisible = true
        binding.previewHeader.paged.pageSizeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                private val pageSizes = resources.getStringArray(R.array.page_sizes).map {
                    it.toInt()
                }

                override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                    if (pos >= 0 && pos < pageSizes.size) {
                        mPageViewModel.setPageSize(pageSizes[pos])
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) = Unit
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

    private fun updateBackgroundImage() {
        (mSubCate?.backgroundImage.takeUnless { it.isNullOrBlank() }
            ?: topCate?.backgroundImage.takeUnless { it.isNullOrBlank() })?.let {
            mRequestManager.load(it)
                .transition(GlideAppExtension.DRAWABLE_CROSS_FADE)
                .blur()
                .into(binding.previewHeader.bgView)
        }
    }

    private fun updateCategoryLink() {
        val sb = StringBuilder("https://www.zcool.com.cn/discover?")
        topCate?.id?.let { cate ->
            sb.append("cate=").append(cate)
            mSubCate?.id?.let { subCate ->
                sb.append("&subCate=").append(subCate)
            }
        }
        binding.previewHeader.categoryLink.text = sb
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
        mRequestManager.clear(binding.previewHeader.bgView)
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
            mPageViewModel.subCate.observe(viewLifecycleOwner) {
                mSubCate = it
                updateBackgroundImage()
                updateCategoryLink()
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