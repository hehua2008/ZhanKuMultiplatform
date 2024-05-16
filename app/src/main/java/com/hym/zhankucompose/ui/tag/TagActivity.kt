package com.hym.zhankucompose.ui.tag

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.R
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.author.AuthorItemPage
import com.hym.zhankucompose.ui.main.PreviewItemPage
import com.hym.zhankucompose.ui.theme.ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

const val EXTRA_AUTHOR = "AUTHOR"
const val EXTRA_TOP_CATE = "TOP_CATE"
const val EXTRA_SUB_CATE = "SUB_CATE"

@AndroidEntryPoint
class TagActivity : BaseActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        val author: CreatorObj? = intent.getParcelableExtra(EXTRA_AUTHOR)
        val topCate: TopCate? = intent.getParcelableExtra(EXTRA_TOP_CATE)
        val subCate: SubCate? = intent.getParcelableExtra(EXTRA_SUB_CATE)
        val mTitle = author?.username ?: topCate?.name ?: subCate?.name ?: ""

        setContent {
            ComposeTheme {
                val density = LocalDensity.current
                val systemBarsTop = WindowInsets.systemBars.getTop(density)
                val topAppBarHeight = remember(density, systemBarsTop) {
                    with(density) { systemBarsTop.toDp() } + 36.dp
                }

                Column {
                    TopAppBar(
                        modifier = Modifier.height(topAppBarHeight),
                        title = {
                            Row(
                                modifier = Modifier.fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (author != null) {
                                    AsyncImage(
                                        model = author.avatar1x,
                                        contentDescription = author.username,
                                        modifier = Modifier.clip(CircleShape)
                                    )
                                }
                                Text(
                                    text = mTitle,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        },
                        navigationIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.vector_arrow_back),
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable { finish() }
                                    .fillMaxHeight()
                                    .padding(horizontal = 12.dp)
                            )
                        }
                    )

                    if (author != null) {
                        AuthorItemPage(author = author, modifier = Modifier.zIndex(-1f))
                    } else {
                        PreviewItemPage(
                            topCate = topCate!!,
                            initialSubCate = subCate,
                            modifier = Modifier.zIndex(-1f)
                        )
                    }
                }
            }
        }
    }
}
