package com.hym.zhankucompose.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.core.text.HtmlCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideSubcomposition
import com.bumptech.glide.integration.compose.RequestState
import com.bumptech.glide.load.DataSource
import com.hym.zhankucompose.compose.toAnnotatedString

/**
 * @author hehua2008
 * @date 2024/3/17
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailContentImage(
    detailImage: DetailImage,
    modifier: Modifier = Modifier,
    size: IntSize = IntSize(detailImage.data.width, detailImage.data.height),
    loadingPainter: Painter? = null,
    failurePainter: Painter? = null,
    onGetSize: ((size: IntSize) -> Unit)? = null,
    onClick: (detailImage: DetailImage) -> Unit
) {
    val enter = remember { fadeIn() }
    val exit = remember { fadeOut() }

    GlideSubcomposition(
        model = detailImage.data.url,
        modifier = modifier
            .fillMaxWidth()
            .run {
                if (size.width == 0 || size.height == 0) this
                else aspectRatio(size.width / size.height.toFloat())
            }
            .pointerInput(detailImage, onClick) {
                detectTapGestures {
                    onClick(detailImage)
                }
            }
    ) {
        val snapshotState = state

        val imageContent: @Composable () -> Unit = {
            Image(
                painter = painter,
                contentDescription = detailImage.data.url,
                modifier = modifier.run {
                    if (onGetSize == null) this
                    else onGloballyPositioned {
                        onGetSize(it.size)
                    }
                },
                contentScale = ContentScale.FillWidth
            )
        }

        // If loaded from memory, do not show animation
        if ((snapshotState as? RequestState.Success)?.dataSource === DataSource.MEMORY_CACHE) {
            imageContent()
        } else { // Show animation
            AnimatedVisibility(
                visible = (snapshotState is RequestState.Success),
                enter = enter,
                exit = exit
            ) {
                imageContent()
            }

            loadingPainter?.let {
                AnimatedVisibility(
                    visible = (snapshotState === RequestState.Loading),
                    enter = enter,
                    exit = exit
                ) {
                    Image(
                        painter = it,
                        contentDescription = "Loading",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.surfaceContainer),
                        contentScale = ContentScale.Inside,
                        alpha = 0.5f
                    )
                }

            }

            failurePainter?.let {
                AnimatedVisibility(
                    visible = (snapshotState === RequestState.Failure),
                    enter = enter,
                    exit = exit
                ) {
                    Image(
                        painter = it,
                        contentDescription = "Failure",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.surfaceContainer),
                        contentScale = ContentScale.Inside,
                        alpha = 0.5f
                    )
                }
            }
        }
    }
}

@Composable
fun DetailContentText(
    detailText: DetailText,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    val density = LocalDensity.current
    val annotatedString = remember(detailText, density) {
        HtmlCompat.fromHtml(detailText.data, HtmlCompat.FROM_HTML_MODE_COMPACT)
            .toAnnotatedString(density)
    }

    Text(text = annotatedString, modifier = modifier, style = textStyle)
}

@Preview
@Composable
private fun PreviewDetailContentText() {
    val detailText = DetailText(Html, 0)
    DetailContentText(detailText, Modifier.background(Color.White))
}

private const val Html =
    """
    <html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body><p><span style="font-size:16px"><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong>老师，玻璃如何打光？</strong></span></span></span></p> 
    <p></p> 
    <p><span style="font-size:16px"><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong>老师，玻璃如何渲染？</strong></span></span></span></p> 
    <p></p> 
    <p><span style="font-size:16px"><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong>老师，玻璃有黑色怎么办？</strong></span></span></span></p> 
    <p></p> 
    <p><span style="font-size:16px"><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong>老师，玻璃有杂色怎么办？</strong></span></span></span></p> 
    <p></p> 
    <p><span style="font-size:16px"><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong>老师，玻璃不通透怎么办？</strong></span></span></span></p> 
    <p></p> 
    <p><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong><span style="font-size:13px">老师，</span><span style="font-size:15px">老师！</span><span style="font-size:17px">老师！！！</span></strong></span></span></p> 
    <p></p> 
    <p></p> 
    <p>老师内心：\uD83D\uDC80</p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02d8kxpqukn13ujyjvn0v03234.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p>恰好前段时间，花西子发了一个非常漂亮的莲花玻璃，于是我直奔87老师那要到了制作教程！</p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02asqsbwrd9jg6yx1oqy3c3533.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p>今天，咱就不废话，直接开干！</p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02gcncutulqoosvtcmoaxf3834.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p>刚好也用这个案例举例说明，模型渲染的几个要素：</p> 
    <p></p> 
    <h3><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong><em>1. 模型</em></strong></span></span></h3> 
    <h3><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong><em>2. 环境</em></strong></span></span></h3> 
    <p></p> 
    <h3><span style="color:#ff6827"><span style="letter-spacing:0.5px"><strong><em>3. 颜色</em></strong></span></span></h3> 
    <p></p> 
    <p>像我们做的视觉类作品，都是以结果为导向的。很多同学说，真实的玻璃是啥啥啥参数，所以我们应该按真实的来，但其实三维是模拟真实，是有偏差的，它不是真实的。</p> 
    <p></p> 
    <p></p> 
    <p>就像充气娃娃模拟的真人，她就不是真人，就不可能有... </p> 
    <p></p> 
    <p></p> 
    <p>额... 好像偏题了。好，既然要做荷花，那我们就先来看看它，从真实荷花中寻找灵感与细节。</p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02dopbajwn0kyes4jxu8k13933.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p>1.1【创建平面】开局得体面，先创建一个平面。</p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02iymudxqaqdbrhuz4foeo3235.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p>1.2【增加布线】按快捷键<strong>C</strong>，把模型转换为<strong>可编辑对象</strong>。调节点，形成花瓣的样子。如果分段不够 ，可以用“<strong>循环-路径切割</strong>”工具增加“<strong>边</strong>”，由少到多的逐渐增加<strong>横向</strong>与<strong>纵向</strong>的布线。 </p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02v37axbpdicyysyssmrjg3535.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p>1.3【调节细节】移动<strong>点</strong>调整出花瓣的形态。 </p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02eqkagkvjhdbe8k6t1vhf3730.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>1.4</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【挤出厚度】选择<strong>所有的面</strong>，按<strong>Ctrl</strong>+<strong>移动</strong>拖拽，<strong>挤出厚度</strong>。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02s3qyymbgryvyatodew4v3032.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>1.5</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【弯曲花瓣】给模型增加一个<strong>弯曲</strong>效果，让花瓣整体弯折起来。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02fcssjl4qh9rjhf30fk9v3338.png"> 
    </div> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02yayjhwvtn60b4fbmb30z3138.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>1.6</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【添加细分】加入<strong>细分曲面</strong>，让模型更<strong>圆滑</strong>。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02ywc2j9ombfolioqpa8vm3835.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>1.7</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【增加花瓣】用同样方法，制作出多个不同的花瓣。可以尝试<strong>调整厚度</strong>，厚度不同的玻璃的折射效果也不一样。（可以为模型增加一次细分，继续做出不同的丰富细节）</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02lwkpqdvn286h5wxk9syy3438.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><strong>1.8</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">【边缘厚度调节】为了让花瓣更精细，可以给</span></span></span></span><strong>花瓣边缘</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">处增加些</span></span></span></span><strong>厚度细节</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">，后期做的差不多的时候还可以用</span></span></span></span><strong>雕刻工具</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">加入更细致的细节。在此我们先不花费太多时间做细。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02jbxv16hiv7fscji5nimu3832.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>1.9</strong>【拼装花瓣】下面这个是我用来渲染的模型。我要赶在这几个小时把图肝完，后面还有好多砖要搬。所以模型我没有继续做了，会缺失一些小细节。有兴趣的同学可以点个赞，超50给大家整个雕刻教程。模型更精细，渲染就会更有细节。（当然，你也可以用克隆工具来安排花瓣）</span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02e6ofpus572ln4xmpx2wb3431.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>2.1</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【创建花蕊底座】因为荷花是透明的，所以中间的花蕊也是会看到的。创建球体，选上半部分的点，压平成为一个半球体底座。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02db1yoh6luedcmtgha3xk3236.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>2.2</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【创建花蕊】同理再创建个球，拉成椭圆，再拉出管柱，然后种到底座上，拼到荷花里。一朵娇羞的荷花模型就做好啦~ 至此，模型部分搞定！</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02eowyqgcdiq0sddclc9cm3539.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><span style="color:#ff6827"><strong>第二个难点：对玻璃影响最大的就是环境</strong></span>，特别是透明类材质，同样的材质、同样的模型、同一个场景光照，位置不一样，玻璃渲染的结果也是不一样的。</span></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">这里我使用的是CR渲染器，为啥不用OC？额，是因为到期了，没续费...</span></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">CR也是渲染器，而且更简单，对显卡没要求，有开心版可用。大家也不用担心，这个案例所做出的效果，用啥渲染器基本是一样的。制作的方法思路也是完全相同的，参数基本也都是默认，不需要啥调节。莫慌~ 放心飞~</span></p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>3.1</strong>【渲染器设置】渲染器的初始设置，<strong>最大采样</strong>调节到10就ok。</span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02nlfqvgcco10qsqsuvaic3235.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>3.2</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【添加HDR光照】把看着明亮一些的HDR，加到CR渲染器的着色器里。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02o5gklalo6l5hdzteeb0l3833.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>3.3</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【添加材质球】如图所示添加<strong>传统材质球</strong>（OC渲染器则用玻璃材质）。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/023gyoy8smbilla84nnhgb3337.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>3.4</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【渲染一个看看】</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02buuz5vgdwed235ukmcbs3633.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>3.5</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【调出玻璃效果】有些渲染器不像OC有现场的玻璃材质。CR渲染器，在材质球里<strong>勾选折射</strong>和<strong>反射</strong>，它就变成<strong>玻璃效果</strong>~ （此时天空是亮灰色，所以渲染出来就如下图所示白色透明效果）</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02hde2yn9xrtpopddg3v003039.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>3.6</strong><span style="color:#222222"><span style="font-size:17px"><span style="background-color:#ffffff">【让效果骚动起来】如果你想让效果变得不平庸，简单~ 你对天空耍个流氓，贴一张表情包：</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02nhdlval6dusyswepxuuj3437.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">你对天空说早安，贴一张表情包：</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02frmec2qholaslau0mhid3134.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">等等，<strong>天空不是要贴HDR嘛？怎么变成表情包了？</strong></span></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">其实，天空加HDR是一种手段，不是标准，这回也不是我们的目的。</span></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">如果找一张镭射图给天空呢？天空镭射了，玻璃是不是就不一样的？这就是环境对玻璃的影响。这是最大的环境，后面还有“环境的第二部分”。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02akjbmofa8reh1ykusxgh3637.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="font-size:17px"><span style="background-color:#ffffff"><span style="letter-spacing:0.5px"><span style="letter-spacing:0.544px"><span style="color:#ff6827"><strong>第三个难点：玻璃的颜色</strong></span></span><span style="color:#222222">，给它个黄色，它就黄了~</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02axrcemt4tmocnnb1gxnj3434.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">老王出现，它就绿了~</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02td8rv76qksxznw2xz1co3938.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">那给个渐变呢？em... 仔细想想，好像也不对？</span></p> 
    <p></p> 
    <p></p> 
    <h3><strong><span style="letter-spacing:0.5px"><span style="color:#ff6827">那如何才能精确的给到颜色？</span></span></strong></h3> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>UV啊！精确贴图请认准UV~</strong>Skr~</span></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">在UV界面里，把花瓣的UV投射一下，都可以不用拆，流氓投射就可以。</span></p> 
    <p></p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02dg0vmy0kohogukpeksvv3739.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">不同花瓣的UV，大致是差不多的，所以我们也不需要挨个画它们的贴图，我们把所有花瓣的UV叠到一起画！（当然，一个一个画会更细致，可以精确到每个像素的颜色）</span></span></span></span></p> 
    <p></p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/025lbtpcd2og5vga7ptq3v3234.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">为了方便观察，我们导出一个网格UV就可以，给它画几个颜色看看，试试效果。（样条贴图所有渲染都是贴图到折射通道里面，OC材质里它叫传输，默认叫透明，其实都一个意思。希望同学继续努力，收复这些地区，然后把软件通道名称统一了）</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/028590t1voj95p7kbxchms3333.png"> 
    </div> 
    <p></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02hcdldqlt291tlpma3jul3139.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">渲染一个看看~ 玻璃的颜色会特别的敏感，特别的蓝，所有渲染都这样。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02tbks1xwyypiv2h4n2vlf3531.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">我们把它的</span></span></span></span><strong>伽马值</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">调大些，让玻璃更加透亮些~</span></span></span></span></p> 
    <p></p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02vap7ib8busgcnjae9ffi3738.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>如果想要玻璃更白，而且还有一丝丝的颜色怎么办呢？</strong>回到UV里，画出一丝丝白线，并把它们搞成彩色，与刚刚的颜色混合到一起试试。（可以尝试不同的效果，不同的花瓣，材质可以不一样）</span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02eusdrl5acy4s7wjuq8he3637.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">把材质替换上去渲染一下，发现有些效果，但不太明显。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02ofuellcetgp45xa9p37t3938.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">效果不明显，那我们就直接只要彩丝~，后期直接滤色到花瓣上去。</span></span></span></span></p> 
    <p></p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/027goewk4xjigvt1n6muov3234.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">同时再把一丝丝的效果做成凹凸。先</span></span></span></span><strong>适当模糊</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">一下，然后加入到</span></span></span></span><strong>凹凸通道</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">里。</span></span></span></span></p> 
    <p></p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02df5t8lwv49litqqh29ij3336.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">此时荷花玻璃就有了一丝丝凹凸的效果。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/022rxjoadfapxj7e7zttc93338.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#ff6827"><strong><span style="letter-spacing:0.5px">还记得上面说的：环境的第二部分嘛？那就是DIY环境。</span></strong></span></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">假设你就是这朵花，你身边的一切都是你的环境，不仅仅是头上的天空（即大环境：镭射图），身边的电脑、手机、别人的女朋友，都是环境，都会影响玻璃荷花的效果。</span></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">身边的环境，最常见的就是灯光，比如这个位置有个灯：</span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02kt5csbyuyhw6uuaoxwff3736.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">有了灯，一下就把花给搞亮了~</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02vqbfb2xkrx1gz0buntge3834.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">花一起做不方便后期，所以<strong>可以分开渲染</strong>。是可以，不是必须，就看你的习惯了。 单独选中这一部分的花，用灯光为其制作反射的细节。</span></p> 
    <p></p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02p0wm4ibp9mm3shnpvndi3134.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px"><strong>环境可以是表情包，那可以是反光板吗？</strong></span></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">当然可以啊！谁都可以上场骚5分钟的~</span></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">开始骚~ 多色的反光板会让玻璃的折射有多色的效果。</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02exjidkqbnzyda970r0oh3530.png"> 
    </div> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02mj1pjyeieyjcgxrt5x813335.png"> 
    </div> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/028csd20wplanab7n1bqv83634.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">然后把分开渲染的花瓣合成到一起。因为是分开渲染的，所以可以用曲线配合蒙版，给花瓣单独调调色~（我这里用的是AE来合成，PS也是可以的）</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02lbb0ahiwlga2mudsmbkh3137.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">一些多色的效果，不一定非要渲染，也可以合成哒~</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/023sbiwhubchc3kxbnmqyc3337.png"> 
    </div> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02cyaltpc3kd7kx2d6gu1f3037.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">然后再给画面加些光效：</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02hahq4y2lm78nesn1chdg3430.png"> 
    </div> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02gg5f6atlyfohghsg2csx3437.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">可以配合蒙版，把玻璃花瓣的</span></span></span></span><strong>边缘</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">，加一丢丢类似</span></span></span></span><strong>RGB分离</strong><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">的效果</span></span></span></span></p> 
    <p> </p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02uv7qrxy0qog05vsv03yy3838.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="color:#222222"><span style="font-size:17px"><span style="letter-spacing:0.5px"><span style="background-color:#ffffff">至此就完事啦~ 来看看最终的效果：</span></span></span></span></p> 
    <p></p> 
    <div class="media-wrap image-wrap"> 
     <img src="https://img.zcool.cn/community/02jopdfvdcyf37radnhcmg3231.png"> 
    </div> 
    <p> </p> 
    <p></p> 
    <p></p> 
    <p></p> 
    <p><span style="letter-spacing:0.5px">这里海报可以通过后期炮制的更加通透些，我要赶着搬砖就不再往细里调了，大家照着做的时候，可以用上面讲的几种方式再改进改进~ </span></p> 
    <p></p> 
    <p>ok，本期教程就到这里，希望大家学的开心。</p> 
    <p></p> 
    <p></p> 
    <p>也特别感谢花西子设计师，给我们带来如此优秀的设计！</p> 
    <p></p> 
    <p></p> 
    <p>感谢你的认真观看，如有收获，欢迎点个赞，转发分享给你的朋友~\uD83D\uDC8F</p></body>
    </html>
    """
