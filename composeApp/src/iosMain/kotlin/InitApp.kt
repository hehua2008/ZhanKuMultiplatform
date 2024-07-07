import com.hym.zhankumultiplatform.MyAppViewModel
import com.hym.zhankumultiplatform.getAppViewModel
import kotlin.experimental.ExperimentalObjCName

/**
 * @author hehua2008
 * @date 2024/7/8
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName(name = "doInit", swiftName = "doInit")
fun doInit() {
    getAppViewModel<MyAppViewModel>().getCategoryItemsFromNetworkAsync()
}
