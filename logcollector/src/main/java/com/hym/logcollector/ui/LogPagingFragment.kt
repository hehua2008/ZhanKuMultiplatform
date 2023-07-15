package com.hym.logcollector.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.hym.logcollector.R
import com.hym.logcollector.base.LogConfig
import com.hym.logcollector.base.LogLevel
import com.hym.logcollector.databinding.FragmentLogFileBinding
import com.hym.logcollector.util.CharsetList
import com.hym.logcollector.util.FileUtils
import com.hym.logcollector.util.PermissionUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * @author hehua2008
 * @date 2021/8/22
 */
internal class LogPagingFragment : Fragment() {
    companion object {
        private const val LOG_CONFIG = "LOG_CONFIG"

        private val DANGEROURS_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        @JvmStatic
        fun newInstance(logConfig: LogConfig): LogPagingFragment {
            val fragment = LogPagingFragment()
            fragment.arguments = bundleOf(LOG_CONFIG to logConfig)
            return fragment
        }
    }

    private val mVMFactory = object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (LogPagingViewModel::class.java.isAssignableFrom(modelClass)) {
                LogPagingViewModel(logConfig) as T
            } else {
                super.create(modelClass)
            }
        }
    }

    lateinit var logConfig: LogConfig
        private set
    private lateinit var mLogAdapter: LogPagingAdapter
    private lateinit var mLogViewModel: LogPagingViewModel
    private lateinit var mStartActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var mRequestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mFileChooserLauncher: ActivityResultLauncher<Unit>

    private var _binding: FragmentLogFileBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = checkNotNull(_binding)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(LOG_CONFIG, logConfig)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activeBundle = (savedInstanceState ?: arguments)!!
        logConfig = activeBundle.getParcelable(LOG_CONFIG)!!
        mLogAdapter = LogPagingAdapter(logConfig.logLevelColorMapper)
        mLogViewModel = ViewModelProvider(this, mVMFactory).get(LogPagingViewModel::class.java)

        mStartActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // ignore
        }

        mRequestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return@registerForActivityResult
                val activity = activity ?: return@registerForActivityResult
                val names = it.filterValues { value -> !value }.also { filtered ->
                    if (filtered.isEmpty()) return@registerForActivityResult
                }.keys.mapNotNull { key ->
                    PermissionUtils.getPermissionName(activity, key)
                }.joinToString()
                val msg = getString(R.string.request_permission_failed, names)
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
            }

        initFileChooserLauncher()
    }

    @SuppressLint("LongMethod")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogFileBinding.inflate(inflater, container, false).apply {
            logRecyclerview.adapter = mLogAdapter.withLoadStateHeaderAndFooter(
                HeaderFooterLoadStateAdapter(),
                HeaderFooterLoadStateAdapter()
            )
            logRecyclerview.addItemDecoration(
                DividerItemDecoration(inflater.context, DividerItemDecoration.VERTICAL).apply {
                    val divider =
                        ResourcesCompat.getDrawable(resources, R.drawable.log_item_divider, null)!!
                    setDrawable(divider)
                }
            )

            val thumbDrawable =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.log_fast_scrollbar_thumb_bg,
                    requireContext().theme
                ) as StateListDrawable
            val trackDrawable =
                ResourcesCompat.getDrawable(resources, android.R.color.transparent, null)
            FastScroller(
                logRecyclerview, thumbDrawable, trackDrawable, thumbDrawable, trackDrawable
            )

            logSearchLayout.apply {
                keywordEdit.hint = getString(R.string.log_input_keyword_and_search)
                keywordEdit.setOnEditorActionListener { v, actionId, event ->
                    if (actionId != EditorInfo.IME_ACTION_SEARCH) {
                        return@setOnEditorActionListener false
                    }
                    val keyword = v.text?.toString() ?: ""
                    keywordClear.isVisible = keyword.isNotEmpty()
                    mLogViewModel.setKeyword(keyword)
                    clearEditFocusAndHideSoftInput()
                    return@setOnEditorActionListener true
                }

                keywordClear.setOnClickListener {
                    keywordEdit.text = null
                    val keyword = keywordEdit.text?.toString() ?: ""
                    mLogViewModel.setKeyword(keyword)
                    clearEditFocusAndHideSoftInput()
                }
            }

            logCancelLoading.setOnClickListener {
                mLogViewModel.cancelLoadingLogFile()
                logLoadingLayout.isVisible = false
            }

            logSelectButton.setOnClickListener {
                if (requestStorageManager()) return@setOnClickListener
                if (requestPermissions()) return@setOnClickListener
                mFileChooserLauncher.launch(Unit)
            }

            logParserSpinner.adapter = ArrayAdapter(
                requireActivity(),
                R.layout.log_spinner_dropdown_item,
                logConfig.logFileParsers.map { it.displayName }
            )
            logParserSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                    mLogViewModel.setLogFileParser(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) = Unit
            }

            logLevelSpinner.adapter = ArrayAdapter(
                requireActivity(),
                R.layout.log_spinner_dropdown_item,
                LogLevel.values().map { if (it == LogLevel.DEFAULT) "LEVEL" else "$it" }
            )
            logLevelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                    mLogViewModel.setLogLevel(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) = Unit
            }

            logDecoderSpinner.adapter = ArrayAdapter(
                requireActivity(),
                R.layout.log_spinner_dropdown_item,
                CharsetList
            )
            logDecoderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                    mLogViewModel.setLogDecoder(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) = Unit
            }
        }

        mLogViewModel.toastMsg.observe(viewLifecycleOwner) {
            val resId = it.first
            val args = it.second
            val msg = if (args != null) {
                getString(resId, *args)
            } else {
                getString(resId)
            }
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
        }

        mLogViewModel.loadingProgress.observe(viewLifecycleOwner) {
            binding.logLoadingProgress.progress = (it * 100).toInt()
            binding.logLoadingLayout.isVisible = (it != 1f)
        }

        mLogViewModel.refresh.observe(viewLifecycleOwner) {
            mLogAdapter.refresh()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Any transformations after the flow's cachedIn step will not be cached, and will
            // instead by re-run immediately on next launch.
            mLogViewModel.pagingFlow.collectLatest { pagingData ->
                mLogAdapter.submitData(pagingData)
            }
        }

        // It will not work since we don't support refresh for now.
        viewLifecycleOwner.lifecycleScope.launch {
            mLogAdapter.loadStateFlow
                // Only emit when REFRESH LoadState changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    binding.logRecyclerview.smoothScrollToPosition(mLogAdapter.itemCount)
                }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.logRecyclerview.adapter = null
        _binding = null
    }

    private fun initFileChooserLauncher() {
        val contract = object : ActivityResultContract<Unit, Uri?>() {
            override fun createIntent(context: Context, input: Unit): Intent =
                Intent(Intent.ACTION_GET_CONTENT).run {
                    type = "text/plain"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    Intent.createChooser(this, null)
                }

            override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                return intent?.data
            }
        }

        val callback = ActivityResultCallback<Uri?> {
            it ?: return@ActivityResultCallback
            FileUtils.getFile(requireActivity(), it)?.let { file -> mLogViewModel.setLogFile(file) }
        }

        mFileChooserLauncher = registerForActivityResult(contract, callback)
    }

    private fun requestStorageManager(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            mStartActivityResultLauncher.launch(
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData(Uri.parse("package:${requireContext().packageName}"))
            )
            return true
        }
        return false
    }

    private fun requestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        val notGrantedPermissions =
            PermissionUtils.checkSelfPermissions(requireContext(), *DANGEROURS_PERMISSIONS)
        if (notGrantedPermissions.isEmpty()) return false
        mRequestPermissionsLauncher.launch(notGrantedPermissions)
        return true
    }

    private fun clearEditFocusAndHideSoftInput() {
        binding.logSearchLayout.keywordEdit.clearFocus()
        val imm = ContextCompat.getSystemService(
            requireActivity(),
            InputMethodManager::class.java
        )
        imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}