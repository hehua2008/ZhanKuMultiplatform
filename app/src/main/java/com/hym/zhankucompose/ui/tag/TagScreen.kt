package com.hym.zhankucompose.ui.tag

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
import androidx.compose.runtime.Composable
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
import com.hym.zhankucompose.R
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.ui.author.AuthorItemPage
import com.hym.zhankucompose.ui.main.PreviewItemPage
import com.hym.zhankucompose.ui.theme.ComposeTheme

/**
 * @author hehua2008
 * @date 2024/6/29
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagScreen(
    author: CreatorObj?,
    topCate: TopCate?,
    subCate: SubCate?,
    onNavigateToDetails: (contentType: ContentType, contentId: String) -> Unit,
    onNavigateToTagList: (author: CreatorObj?, topCate: TopCate?, subCate: SubCate?) -> Unit,
    onNavigateToWebView: (url: String, title: String) -> Unit
) {
    ComposeTheme {
        val navController = LocalNavController.current
        val density = LocalDensity.current
        val systemBarsTop = WindowInsets.systemBars.getTop(density)
        val topAppBarHeight = remember(density, systemBarsTop) {
            with(density) { systemBarsTop.toDp() } + 36.dp
        }
        val title = remember { author?.username ?: topCate?.name ?: subCate?.name ?: "" }

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
                            text = title,
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
                            .clickable {
                                navController.popBackStack()
                            }
                            .fillMaxHeight()
                            .padding(horizontal = 12.dp)
                    )
                }
            )

            if (author != null) {
                AuthorItemPage(
                    author = author,
                    onNavigateToDetails = onNavigateToDetails,
                    onNavigateToTagList = onNavigateToTagList,
                    onNavigateToWebView = onNavigateToWebView,
                    modifier = Modifier.zIndex(-1f)
                )
            } else {
                PreviewItemPage(
                    topCate = topCate!!,
                    onNavigateToDetails = onNavigateToDetails,
                    onNavigateToTagList = onNavigateToTagList,
                    onNavigateToWebView = onNavigateToWebView,
                    initialSubCate = subCate,
                    modifier = Modifier.zIndex(-1f)
                )
            }
        }
    }
}
