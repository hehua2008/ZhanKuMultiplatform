package com.hym.zhankucompose.model

/**
 * @author hehua2008
 * @date 2021/12/9
 *
{
"id": 247,
"level": 3,
"name": "北京",
"nameEn": "Beijing",
"parent": 7
}
 */
abstract class NowAddressObj {
    abstract val id: Int

    abstract val level: Int

    abstract val name: String

    abstract val nameEn: String

    abstract val parent: Int
}