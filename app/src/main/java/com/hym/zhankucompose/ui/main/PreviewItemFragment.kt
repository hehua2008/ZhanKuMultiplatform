package com.hym.zhankucompose.ui.main

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankucompose.GlideAppExtension
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.databinding.FragmentMainBinding
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.TabReselectedCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
    private val mPageViewModel: PreviewPageViewModel by viewModels()
    private var mBinding: FragmentMainBinding? = null
    private val binding get() = checkNotNull(mBinding)
    private lateinit var mRequestManager: RequestManager

    private lateinit var mCategoryItemLayoutManager: FlexboxLayoutManager
    private lateinit var mCategoryItemAdapter: CategoryItemAdapter
    private lateinit var mButtonItemDecoration: RecyclerView.ItemDecoration

    private var scrollToTop: () -> Unit = EMPTY_BLOCK

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
        mPageViewModel.topCate = topCate
        mPageViewModel.setSubCate(mSubCate)

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

        mRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        updateBackgroundImage()
        updateCategoryLink()

        /*
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        */

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

        return binding.root
    }

    override fun onTabReselected() {
        scrollToTop()
    }

    private fun updateBackgroundImage() {
        (mSubCate?.backgroundImage.takeUnless { it.isNullOrBlank() }
            ?: topCate?.backgroundImage.takeUnless { it.isNullOrBlank() })?.let {
            mRequestManager.load(it)
                .transition(GlideAppExtension.DRAWABLE_CROSS_FADE)
                .apply(GlideAppExtension.blurMulti)
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
        scrollToTop = EMPTY_BLOCK
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
            mPageViewModel.subCate.observe(viewLifecycleOwner) {
                mSubCate = it
                updateBackgroundImage()
                updateCategoryLink()
            }
        }
    }
}