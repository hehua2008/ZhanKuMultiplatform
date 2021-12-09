package com.hym.zhankukotlin.ui.main

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.FragmentMainBinding
import com.hym.zhankukotlin.model.RecommendLevel
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

class PreviewItemFragment : Fragment(), Observer<LifecycleOwner> {
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
    private lateinit var mPageViewModel: PageViewModel
    private var mBinding: FragmentMainBinding? = null
    private val binding get() = checkNotNull(mBinding)

    private lateinit var mPagingPreviewItemAdapter: PagingPreviewItemAdapter
    private lateinit var mCategoryItemLayoutManager: FlexboxLayoutManager
    private lateinit var mCategoryItemAdapter: CategoryItemAdapter
    private lateinit var mButtonItemDecoration: RecyclerView.ItemDecoration
    private lateinit var mPreviewItemDecoration: RecyclerView.ItemDecoration

    private val mVMFactory = object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (PageViewModel::class.java.isAssignableFrom(modelClass)) {
                PageViewModel(topCate).apply { setSubCate(mSubCate) } as T
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

        mPageViewModel = ViewModelProvider(this, mVMFactory).get(PageViewModel::class.java)
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
        binding.previewRecycler.adapter = mPagingPreviewItemAdapter

        binding.catRecrcler.layoutManager = mCategoryItemLayoutManager
        binding.catRecrcler.addItemDecoration(mButtonItemDecoration)
        binding.catRecrcler.adapter = mCategoryItemAdapter

        binding.orderGroup.setOnCheckedChangeListener { group, checkedId ->
            val recommendLevel: RecommendLevel = when (checkedId) {
                R.id.order_all_recommend -> RecommendLevel.ALL_RECOMMEND
                R.id.order_home_recommend -> RecommendLevel.HOME_RECOMMEND
                R.id.order_latest_publish -> RecommendLevel.LATEST_PUBLISH
                R.id.order_editor_choice -> RecommendLevel.EDITOR_CHOICE
                else -> RecommendLevel.EDITOR_CHOICE
            }
            mPageViewModel.setRecommendLevel(recommendLevel)
        }

        binding.pagedLayout.prePage.setOnClickListener {
            clearEditFocusAndHideSoftInput()
            val curPage = mPageViewModel.page.value ?: return@setOnClickListener
            mPageViewModel.setPage(curPage - 1)
        }
        binding.pagedLayout.nextPage.setOnClickListener {
            clearEditFocusAndHideSoftInput()
            val curPage = mPageViewModel.page.value ?: return@setOnClickListener
            mPageViewModel.setPage(curPage + 1)
        }
        binding.pagedLayout.jumpButton.setOnClickListener(View.OnClickListener {
            clearEditFocusAndHideSoftInput()
            val numberEdit = binding.pagedLayout.numberEdit.text.toString()
            if (numberEdit.isEmpty()) return@OnClickListener
            mPageViewModel.setPage(numberEdit.toInt())
        })

        return binding.root
    }

    private fun updateCategoryLink() {
        val desc = mSubCate?.description?.trim() ?: topCate?.description?.trim()
        binding.categoryLink.visibility = if (desc.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.categoryLink.text = desc
    }

    private fun clearEditFocusAndHideSoftInput() {
        binding.pagedLayout.numberEdit.clearFocus()
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.previewRecycler.adapter = null
        binding.catRecrcler.layoutManager = null
        binding.catRecrcler.adapter = null
        mBinding = null
    }

    override fun onChanged(viewLifecycleOwner: LifecycleOwner) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mPageViewModel.page.observe(viewLifecycleOwner) {
                binding.pagedLayout.pageArr = intArrayOf(it, 10000)
            }
            mPageViewModel.mediatorLiveData.observe(viewLifecycleOwner) {
                mPagingPreviewItemAdapter.refresh()
            }
            mPageViewModel.subCate.observe(viewLifecycleOwner) {
                mSubCate = it
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