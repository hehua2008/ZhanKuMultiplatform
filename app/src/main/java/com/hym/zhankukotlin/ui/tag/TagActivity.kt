package com.hym.zhankukotlin.ui.tag

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.main.PreviewItemFragment

class TagActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.tag_activity)

        val topCate: TopCate? = intent.getParcelableExtra(PreviewItemFragment.TOP_CATE)
        val subCate: SubCate? = intent.getParcelableExtra(PreviewItemFragment.SUB_CATE)
        title = topCate?.name ?: subCate?.name

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PreviewItemFragment.newInstance(topCate, subCate))
                .commitNow()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}