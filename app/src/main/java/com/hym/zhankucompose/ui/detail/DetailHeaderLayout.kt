package com.hym.zhankucompose.ui.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import androidx.core.text.HtmlCompat
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.BUTTON_CONTENT_PADDING
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.compose.LabelFlowLayout
import com.hym.zhankucompose.compose.RemoveAccessibilityExtraSpace
import com.hym.zhankucompose.compose.SMALL_PADDING_VALUES
import com.hym.zhankucompose.compose.SimpleLinkText
import com.hym.zhankucompose.compose.SingleLineTextWithDrawable
import com.hym.zhankucompose.compose.toAnnotatedString
import com.hym.zhankucompose.model.Cate
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.model.WorkDetails
import com.hym.zhankucompose.ui.author.AuthorItemFragment
import com.hym.zhankucompose.ui.main.PreviewItemFragment
import com.hym.zhankucompose.ui.tag.TagActivity
import com.hym.zhankucompose.ui.webview.WebViewActivity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * @author hehua2008
 * @date 2021/12/10
 */
@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current
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
        ImageVector.vectorResource(R.drawable.vector_eye)
    )
    val commentPainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_comment)
    )
    val favoritePainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_favorite)
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
                        visibility =
                            if (categoriesNames.isEmpty()) Visibility.Gone else Visibility.Visible
                    }
                    .fillMaxWidth()
                    .padding(vertical = COMMON_PADDING),
                allSelected = true,
                itemPadding = SMALL_PADDING_VALUES
            ) {
                val tagCate = categories[it]
                val intent = Intent(context, TagActivity::class.java)
                if (tagCate is TopCate) {
                    intent.putExtra(PreviewItemFragment.TOP_CATE, tagCate)
                } else {
                    intent.putExtra(
                        PreviewItemFragment.TOP_CATE,
                        Cate.getCategory<TopCate>(tagCate.parent)
                    )
                    intent.putExtra(PreviewItemFragment.SUB_CATE, tagCate)
                }
                context.startActivity(intent)
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
                    val intent = Intent(context, TagActivity::class.java)
                        .putExtra(AuthorItemFragment.AUTHOR, creatorObj)
                    context.startActivity(intent)
                }

                GlideImage(
                    model = creatorObj.avatar1x,
                    contentDescription = creatorObj.avatar1x,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable(onClick = onAuthorClick),
                    contentScale = ContentScale.Crop,
                    transition = CrossFade
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
                        visibility =
                            if (onDownloadAllClick != null) Visibility.Visible else Visibility.Gone
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
                val intent = Intent(context, WebViewActivity::class.java)
                    .putExtra(WebViewActivity.WEB_URL, it)
                context.startActivity(intent)
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

            val annotatedShareWords = remember(shareWordsStr, density) {
                HtmlCompat.fromHtml(shareWordsStr, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    .toAnnotatedString(density)
            }

            Text(
                text = annotatedShareWords,
                modifier = Modifier
                    .constrainAs(shareWords) {
                        top.linkTo(time.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        visibility =
                            if (shareWordsStr.isBlank()) Visibility.Gone else Visibility.Visible
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
