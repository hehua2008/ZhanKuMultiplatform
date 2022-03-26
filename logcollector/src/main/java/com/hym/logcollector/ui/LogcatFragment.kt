package com.hym.logcollector.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hym.logcollector.LogcatService
import com.hym.logcollector.LogcatServiceImpl
import com.hym.logcollector.R
import com.hym.logcollector.base.LogConfig
import com.hym.logcollector.base.LogLevel
import com.hym.logcollector.databinding.FragmentLogcatBinding
import com.hym.logcollector.databinding.LogTypeSelectButtonBinding

/**
 * @author hehua2008
 * @date 2021/8/21
 */
internal class LogcatFragment : Fragment() {
    companion object {
        private const val LOG_CONFIG = "LOG_CONFIG"

        @JvmStatic
        fun newInstance(logConfig: LogConfig): LogcatFragment {
            val fragment = LogcatFragment()
            fragment.arguments = bundleOf(LOG_CONFIG to logConfig)
            return fragment
        }
    }

    lateinit var logConfig: LogConfig
        private set
    private lateinit var mLogAdapter: LogListAdapter
    private lateinit var mLogViewModel: LogcatViewModel

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val logcatService = service as LogcatServiceImpl
            logcatService.logConfig = logConfig
            mLogViewModel.logcatService = logcatService
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    private var _binding: FragmentLogcatBinding? = null

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
        mLogAdapter = LogListAdapter(logConfig.logLevelColorMapper)
        mLogViewModel = ViewModelProvider(this).get(LogcatViewModel::class.java)
        val activity = requireActivity()
        val serviceIntent = Intent(activity, LogcatService::class.java)
        activity.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    @Suppress("LongMethod")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogcatBinding.inflate(inflater, container, false).apply {
            logRecyclerview.adapter = mLogAdapter
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
                keywordEdit.doAfterTextChanged {
                    val keyword = it?.toString() ?: ""
                    keywordClear.isVisible = keyword.isNotEmpty()
                    mLogViewModel.setKeyword(keyword)
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

            logcatTypeGroup.run {
                val firstView = this[0] as AppCompatRadioButton
                if (logConfig.logcatTypes.size == 1) {
                    firstView.isChecked = true
                    firstView.text = logConfig.logcatTypes[0].name
                    return@run
                }
                val lp = firstView.layoutParams
                logConfig.logcatTypes.forEachIndexed { index, logcatType ->
                    if (index == 0) {
                        firstView.text = logcatType.name
                        return@forEachIndexed
                    }
                    val radioButton = LogTypeSelectButtonBinding.inflate(inflater).root
                    radioButton.text = logcatType.name
                    addView(radioButton, lp)
                }
                (getChildAt(mLogViewModel.logTypeIndex) as AppCompatRadioButton).isChecked = true
                setOnCheckedChangeListener { _, id ->
                    val child = findViewById<AppCompatRadioButton>(id)
                    val index = indexOfChild(child)
                    mLogViewModel.setLogType(index)
                }
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

            logcatClear.setOnClickListener {
                mLogViewModel.clearLog()
            }
        }

        mLogViewModel.toastMsg.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

        mLogViewModel.logList.observe(viewLifecycleOwner) {
            val recyclerView = binding.logRecyclerview
            val onBottom = (recyclerView.layoutManager as LinearLayoutManager)
                .findLastCompletelyVisibleItemPosition() == mLogAdapter.itemCount - 1
            mLogAdapter.submitList(it) {
                // If submitList runs on main thread, commitCallback will always run on main thread
                if (onBottom) recyclerView.smoothScrollToPosition(mLogAdapter.itemCount)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.logRecyclerview.adapter = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(mServiceConnection)
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