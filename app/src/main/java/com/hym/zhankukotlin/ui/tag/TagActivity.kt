package com.hym.zhankukotlin.ui.tag

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.author.AuthorItemFragment
import com.hym.zhankukotlin.ui.main.PreviewItemFragment

class TagActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.tag_activity)

        val authorUid: Int = intent.getIntExtra(AuthorItemFragment.AUTHOR_UID, 0)
        val authorName: String? = intent.getStringExtra(AuthorItemFragment.AUTHOR_NAME)
        val isAuthor = authorUid != 0 && authorName != null
        val topCate: TopCate? = intent.getParcelableExtra(PreviewItemFragment.TOP_CATE)
        val subCate: SubCate? = intent.getParcelableExtra(PreviewItemFragment.SUB_CATE)
        title = if (isAuthor) authorName else topCate?.name ?: subCate?.name

        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                if (isAuthor) {
                    replace(R.id.container, AuthorItemFragment.newInstance(authorUid, authorName!!))
                } else {
                    replace(R.id.container, PreviewItemFragment.newInstance(topCate, subCate))
                }
            }
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