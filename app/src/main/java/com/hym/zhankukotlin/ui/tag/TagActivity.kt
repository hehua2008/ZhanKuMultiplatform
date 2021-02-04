package com.hym.zhankukotlin.ui.tag

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.network.CategoryItem
import com.hym.zhankukotlin.ui.main.PreviewItemFragment

class TagActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.tag_activity)

        val categoryItem: CategoryItem =
            intent.getParcelableExtra(PreviewItemFragment.CATEGORY_ITEM)!!
        title = categoryItem.title

        if (savedInstanceState === null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PreviewItemFragment.newInstance(categoryItem))
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