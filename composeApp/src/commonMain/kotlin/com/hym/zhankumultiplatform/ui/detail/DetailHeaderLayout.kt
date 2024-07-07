package com.hym.zhankumultiplatform.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import com.hym.zhankumultiplatform.compose.BUTTON_CONTENT_PADDING
import com.hym.zhankumultiplatform.compose.COMMON_PADDING
import com.hym.zhankumultiplatform.compose.EMPTY_BLOCK
import com.hym.zhankumultiplatform.compose.LabelFlowLayout
import com.hym.zhankumultiplatform.compose.RemoveAccessibilityExtraSpace
import com.hym.zhankumultiplatform.compose.SMALL_PADDING_VALUES
import com.hym.zhankumultiplatform.compose.SimpleLinkText
import com.hym.zhankumultiplatform.compose.SingleLineTextWithDrawable
import com.hym.zhankumultiplatform.compose.htmlToAnnotatedString
import com.hym.zhankumultiplatform.model.Cate
import com.hym.zhankumultiplatform.model.CreatorObj
import com.hym.zhankumultiplatform.model.SubCate
import com.hym.zhankumultiplatform.model.TopCate
import com.hym.zhankumultiplatform.model.WorkDetails
import com.hym.zhankumultiplatform.navigation.LocalNavListener
import com.hym.zhankumultiplatform.navigation.TagListArgs
import com.hym.zhankumultiplatform.navigation.WebViewArgs
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import zhankumultiplatform.composeapp.generated.resources.Res
import zhankumultiplatform.composeapp.generated.resources.vector_comment
import zhankumultiplatform.composeapp.generated.resources.vector_eye
import zhankumultiplatform.composeapp.generated.resources.vector_favorite

/**
 * @author hehua2008
 * @date 2021/12/10
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailHeaderLayout(
    titleStr: String,
    creatorObj: CreatorObj,
    linkUrl: String,
    timeStr: String,
    viewCountStr: String,
    commentCountStr: String,
    favoriteCountStr: String,
    shareWordsStr: String = "",
    categories: ImmutableList<Cate> = persistentListOf(),
    modifier: Modifier = Modifier,
    onDownloadAllClick: (() -> Unit)? = null
) {
    val navListener = LocalNavListener.current
    val density = LocalDensity.current
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val titleTextStyle = MaterialTheme.typography.titleLarge.let {
        remember(it, onSurfaceColor) {
            it.copy(color = onSurfaceColor, fontWeight = FontWeight.Bold)
        }
    }
    val shareTextStyle = MaterialTheme.typography.bodyMedium.let {
        remember(it, onSurfaceColor) {
            it.copy(color = onSurfaceColor, fontStyle = FontStyle.Italic)
        }
    }
    val labelTextStyle = MaterialTheme.typography.labelMedium.let {
        remember(it, onSurfaceColor) {
            it.copy(
                color = onSurfaceColor,
                lineHeight = 1.em,
                /*platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ), lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )*/
            )
        }
    }
    val labelAlpha = 0.5f
    val viewsPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_eye)
    )
    val commentPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_comment)
    )
    val favoritePainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_favorite)
    )

    CompositionLocalProvider(LocalContentColor provides onSurfaceColor) {
        ConstraintLayout(modifier = modifier) {
            val (title, tags, authorGroup, downloadAll, link, time, viewCount, comments, favorites, shareWords) = createRefs()
            createVerticalChain(
                title, tags, authorGroup, link, time, shareWords, chainStyle = ChainStyle.Packed
            )
            createHorizontalChain(
                time, viewCount, comments, favorites, chainStyle = ChainStyle.Spread
            )

            Text(
                text = titleStr,
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        bottom.linkTo(tags.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                textAlign = TextAlign.Center,
                style = titleTextStyle
            )

            val categoriesNames = remember(categories) {
                categories.map { it.name }.toImmutableList()
            }

            LabelFlowLayout(
                labels = categoriesNames,
                modifier = Modifier
                    .constrainAs(tags) {
                        top.linkTo(title.bottom)
                        bottom.linkTo(authorGroup.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        //visibility =
                        //    if (categoriesNames.isEmpty()) Visibility.Gone else Visibility.Visible
                    }
                    .fillMaxWidth()
                    .run {
                        if (categoriesNames.isEmpty()) height(0.dp) else this
                    }
                    .padding(vertical = COMMON_PADDING),
                allSelected = true,
                itemPadding = SMALL_PADDING_VALUES
            ) {
                when (val tagCate = categories[it]) {
                    is TopCate -> {
                        navListener.onNavigateToTagList(TagListArgs(null, tagCate, null))
                    }

                    is SubCate -> {
                        val topCate = Cate.getCategory<TopCate>(tagCate.parent)
                        navListener.onNavigateToTagList(TagListArgs(null, topCate, tagCate))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .constrainAs(authorGroup) {
                        top.linkTo(tags.bottom)
                        bottom.linkTo(link.top)
                        start.linkTo(parent.start)
                        end.linkTo(downloadAll.start)
                    }
                    .height(IntrinsicSize.Min)
                    .padding(top = COMMON_PADDING)
            ) {
                val onAuthorClick = {
                    navListener.onNavigateToTagList(TagListArgs(creatorObj, null, null))
                }

                AsyncImage(
                    model = creatorObj.avatar1x,
                    contentDescription = creatorObj.avatar1x,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable(onClick = onAuthorClick),
                    contentScale = ContentScale.Crop/*,
                    transition = CrossFade*/
                )

                RemoveAccessibilityExtraSpace {
                    Button(
                        onClick = onAuthorClick,
                        shape = ShapeDefaults.Small,
                        contentPadding = BUTTON_CONTENT_PADDING
                    )
                    {
                        Text(text = creatorObj.username, maxLines = 1)
                    }
                }
            }

            RemoveAccessibilityExtraSpace {
                Button(
                    onClick = onDownloadAllClick ?: EMPTY_BLOCK,
                    modifier = Modifier.constrainAs(downloadAll) {
                        top.linkTo(authorGroup.top)
                        bottom.linkTo(authorGroup.bottom)
                        start.linkTo(authorGroup.end)
                        end.linkTo(parent.end)
                        //visibility =
                        //    if (onDownloadAllClick != null) Visibility.Visible else Visibility.Gone
                    }.run {
                        if (onDownloadAllClick != null) this else width(0.dp)
                    },
                    shape = ShapeDefaults.Small,
                    contentPadding = BUTTON_CONTENT_PADDING
                ) {
                    Text(text = "一键下载", maxLines = 1)
                }
            }

            SimpleLinkText(
                link = linkUrl,
                modifier = modifier
                    .constrainAs(link) {
                        top.linkTo(authorGroup.bottom)
                        bottom.linkTo(time.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .wrapContentWidth()
                    .padding(top = COMMON_PADDING)
            ) {
                navListener.onNavigateToWebView(WebViewArgs(it, ""))
            }

            Text(
                text = timeStr,
                modifier = Modifier
                    .constrainAs(time) {
                        top.linkTo(link.bottom)
                        bottom.linkTo(shareWords.top)
                        start.linkTo(parent.start)
                        end.linkTo(viewCount.start)
                    }
                    .padding(top = COMMON_PADDING)
                    .alpha(labelAlpha),
                style = labelTextStyle
            )

            SingleLineTextWithDrawable(
                text = viewCountStr,
                modifier = Modifier
                    .constrainAs(viewCount) {
                        top.linkTo(time.top, margin = COMMON_PADDING)
                        bottom.linkTo(time.bottom)
                        start.linkTo(time.end)
                        end.linkTo(comments.start)
                    }
                    .alpha(labelAlpha),
                prefix = {
                    Icon(painter = viewsPainter, contentDescription = null)
                },
                style = labelTextStyle
            )

            SingleLineTextWithDrawable(
                text = commentCountStr,
                modifier = Modifier
                    .constrainAs(comments) {
                        top.linkTo(time.top, margin = COMMON_PADDING)
                        bottom.linkTo(time.bottom)
                        start.linkTo(viewCount.end)
                        end.linkTo(favorites.start)
                    }
                    .alpha(labelAlpha),
                prefix = {
                    Icon(painter = commentPainter, contentDescription = null)
                },
                style = labelTextStyle
            )

            SingleLineTextWithDrawable(
                text = favoriteCountStr,
                modifier = Modifier
                    .constrainAs(favorites) {
                        top.linkTo(time.top, margin = COMMON_PADDING)
                        bottom.linkTo(time.bottom)
                        start.linkTo(comments.end)
                        end.linkTo(parent.end)
                    }
                    .alpha(labelAlpha),
                prefix = {
                    Icon(painter = favoritePainter, contentDescription = null)
                },
                style = labelTextStyle
            )

            val annotatedShareWords = remember(shareWordsStr) {
                shareWordsStr.htmlToAnnotatedString(density)
            }

            Text(
                text = annotatedShareWords,
                modifier = Modifier
                    .constrainAs(shareWords) {
                        top.linkTo(time.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        //visibility =
                        //    if (shareWordsStr.isBlank()) Visibility.Gone else Visibility.Visible
                    }.run {
                        if (shareWordsStr.isBlank()) height(0.dp) else this
                    }
                    .wrapContentWidth()
                    .padding(top = COMMON_PADDING),
                style = shareTextStyle
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDetailHeaderLayout() {
    val workDetails = WorkDetails.Demo
    val categories =
        listOf(workDetails.product.fieldCateObj, workDetails.product.subCateObj).toImmutableList()
    DetailHeaderLayout(
        titleStr = workDetails.product.title,
        categories = categories,
        creatorObj = workDetails.product.creatorObj,
        linkUrl = workDetails.product.pageUrl,
        timeStr = workDetails.product.updateTimeStr,
        viewCountStr = workDetails.product.viewCountStr,
        commentCountStr = workDetails.product.commentCountStr,
        favoriteCountStr = "${workDetails.product.favoriteCount}",
        shareWordsStr = workDetails.sharewords,
        modifier = Modifier.background(Color.White)
    )
}
