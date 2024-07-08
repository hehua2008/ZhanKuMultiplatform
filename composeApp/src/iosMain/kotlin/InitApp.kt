import coil3.annotation.InternalCoilApi
import coil3.util.ServiceLoaderComponentRegistry
import com.hym.zhankumultiplatform.MyAppViewModel
import com.hym.zhankumultiplatform.di.GlobalComponent
import com.hym.zhankumultiplatform.getAppViewModel
import kotlin.experimental.ExperimentalObjCName

/**
 * @author hehua2008
 * @date 2024/7/8
 */
@OptIn(ExperimentalObjCName::class, InternalCoilApi::class)
@ObjCName(name = "doInit", swiftName = "doInit")
fun doInit() {
    ServiceLoaderComponentRegistry.register(GlobalComponent.Instance.ktorNetworkFetcherServiceLoaderTarget)
    getAppViewModel<MyAppViewModel>().getCategoryItemsFromNetworkAsync()
}
