package com.hym.zhankucompose.ui.main

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hym.zhankucompose.compose.BUTTON_CONTENT_PADDING
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.compose.RemoveAccessibilityExtraSpace
import com.hym.zhankucompose.compose.SingleLineTextWithDrawable
import com.hym.zhankucompose.compose.copyToClipboard
import com.hym.zhankucompose.model.Content

/**
 * @author hehua2008
 * @date 2024/3/8
 */
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun PreviewItem(
    content: Content,
    modifier: Modifier = Modifier,
    viewsPainter: Painter? = null,
    commentPainter: Painter? = null,
    favoritePainter: Painter? = null,
    onImageClick: () -> Unit = EMPTY_BLOCK,
    onAuthorClick: () -> Unit = EMPTY_BLOCK
) {
    val context = LocalContext.current
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
        val (imgCard, avatar, author, description, time, viewCount, comments, favorites) = createRefs()
        createVerticalChain(
            imgCard, author, description, time, chainStyle = ChainStyle.SpreadInside
        )
        createHorizontalChain(avatar, author, chainStyle = ChainStyle.Packed)
        createHorizontalChain(
            time, viewCount, comments, favorites,
            chainStyle = ChainStyle.SpreadInside
        )

        GlideImage(
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
            contentScale = ContentScale.Crop,
            transition = CrossFade
        )

        GlideImage(
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
            contentScale = ContentScale.Crop,
            transition = CrossFade/*,
            requestBuilderTransform = { it.circleCrop() }*/
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
                    onLongClick = { content.creatorObj.username.copyToClipboard(context) },
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
                    onLongClick = { content.formatTitle.copyToClipboard(context) },
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
                    end.linkTo(favorites.start)
                }
                .alpha(labelAlpha),
            prefix = commentPainter?.let {
                { Icon(painter = it, contentDescription = null) }
            },
            style = labelTextStyle
        )

        SingleLineTextWithDrawable(
            text = content.favoriteCountStr,
            modifier = Modifier
                .constrainAs(favorites) {
                    top.linkTo(time.top)
                    bottom.linkTo(time.bottom)
                    start.linkTo(comments.end)
                    end.linkTo(parent.end)
                    visibility = Visibility.Gone
                }
                .alpha(labelAlpha),
            prefix = favoritePainter?.let {
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
