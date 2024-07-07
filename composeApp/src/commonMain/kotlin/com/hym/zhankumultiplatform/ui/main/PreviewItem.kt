package com.hym.zhankumultiplatform.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import com.hym.zhankumultiplatform.compose.BUTTON_CONTENT_PADDING
import com.hym.zhankumultiplatform.compose.COMMON_PADDING
import com.hym.zhankumultiplatform.compose.EMPTY_BLOCK
import com.hym.zhankumultiplatform.compose.RemoveAccessibilityExtraSpace
import com.hym.zhankumultiplatform.compose.SingleLineTextWithDrawable
import com.hym.zhankumultiplatform.compose.copyToClipboard
import com.hym.zhankumultiplatform.model.Content
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * @author hehua2008
 * @date 2024/3/8
 */
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun PreviewItem(
    content: Content,
    modifier: Modifier = Modifier,
    viewsPainter: Painter? = null,
    commentPainter: Painter? = null,
    onImageClick: () -> Unit = EMPTY_BLOCK,
    onAuthorClick: () -> Unit = EMPTY_BLOCK
) {
    val titleTextStyle = MaterialTheme.typography.titleSmall.let {
        remember(it) {
            it.copy(fontWeight = FontWeight.Bold)
        }
    }
    val labelTextStyle = MaterialTheme.typography.labelMedium.let {
        remember(it) {
            it.copy(
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

    ConstraintLayout(modifier = modifier) {
        val (imgCard, avatar, author, description, time, viewCount, comments) = createRefs()
        createVerticalChain(
            imgCard, author, description, time, chainStyle = ChainStyle.SpreadInside
        )
        createHorizontalChain(avatar, author, chainStyle = ChainStyle.Packed)
        createHorizontalChain(
            time, viewCount, comments,
            chainStyle = ChainStyle.SpreadInside
        )

        AsyncImage(
            model = content.cover1x,
            contentDescription = content.cover,
            modifier = Modifier
                //.aspectRatio(4 / 3f)
                .constrainAs(imgCard) {
                    top.linkTo(parent.top)
                    bottom.linkTo(author.top)
                    centerHorizontallyTo(parent)
                    width = Dimension.matchParent
                    height = Dimension.ratio("4:3")
                }
                .fillMaxSize()
                .clip(ShapeDefaults.Small)
                .clickable(onClick = onImageClick),
            contentScale = ContentScale.Crop/*,
            transition = CrossFade*/
        )

        AsyncImage(
            model = content.creatorObj.avatar1x,
            contentDescription = content.creatorObj.avatar,
            modifier = Modifier
                //.aspectRatio(1f)
                .constrainAs(avatar) {
                    top.linkTo(author.top, margin = COMMON_PADDING)
                    bottom.linkTo(author.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(author.start)
                    height = Dimension.fillToConstraints
                    width = Dimension.ratio("1:1")
                }
                .clip(CircleShape)
                .clickable(onClick = onAuthorClick),
            contentScale = ContentScale.Crop/*,
            transition = CrossFade*/
        )

        RemoveAccessibilityExtraSpace {
            Button(
                onClick = onAuthorClick,
                modifier = Modifier
                    .constrainAs(author) {
                        top.linkTo(imgCard.bottom)
                        bottom.linkTo(description.top)
                        start.linkTo(avatar.end)
                        end.linkTo(parent.end)
                    }
                    .padding(top = COMMON_PADDING)
                /*.combinedClickable(
                    onLongClick = { content.creatorObj.username.copyToClipboard() },
                    onClick = onAuthorClick
                )*/,
                shape = ShapeDefaults.Small,
                contentPadding = BUTTON_CONTENT_PADDING
            ) {
                Text(
                    text = content.creatorObj.username,
                    maxLines = 1
                )
            }
        }

        Text(
            text = content.formatTitle,
            modifier = Modifier
                .constrainAs(description) {
                    top.linkTo(author.bottom)
                    bottom.linkTo(time.top)
                    centerHorizontallyTo(parent)
                    width = Dimension.matchParent
                    height = Dimension.wrapContent
                }
                .padding(top = COMMON_PADDING)
                .combinedClickable(
                    onLongClick = { content.formatTitle.copyToClipboard() },
                    onClick = EMPTY_BLOCK
                ),
            style = titleTextStyle,
            maxLines = 2,
            minLines = 2
        )

        Text(
            text = content.updateTimeStr,
            modifier = Modifier
                .constrainAs(time) {
                    top.linkTo(description.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(viewCount.start)
                }
                .padding(vertical = COMMON_PADDING)
                .alpha(labelAlpha),
            style = labelTextStyle
        )

        SingleLineTextWithDrawable(
            text = content.viewCountStr,
            modifier = Modifier
                .constrainAs(viewCount) {
                    top.linkTo(time.top)
                    bottom.linkTo(time.bottom)
                    start.linkTo(time.end)
                    end.linkTo(comments.start)
                }
                .alpha(labelAlpha),
            prefix = viewsPainter?.let {
                { Icon(painter = it, contentDescription = null) }
            },
            style = labelTextStyle
        )

        SingleLineTextWithDrawable(
            text = content.commentCountStr,
            modifier = Modifier
                .constrainAs(comments) {
                    top.linkTo(time.top)
                    bottom.linkTo(time.bottom)
                    start.linkTo(viewCount.end)
                    end.linkTo(parent.end)
                }
                .alpha(labelAlpha),
            prefix = commentPainter?.let {
                { Icon(painter = it, contentDescription = null) }
            },
            style = labelTextStyle
        )
    }
}

@Preview
@Composable
private fun PreviewPreviewItem() {
    PreviewItem(Content.Demo, Modifier.background(Color.White))
}
