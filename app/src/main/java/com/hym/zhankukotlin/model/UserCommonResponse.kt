package com.hym.zhankukotlin.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * GET https://api.zcool.com.cn/v2/api/u/13580089/common?app=android
 */
@Keep
data class UserCommonResponse(
    override val code: Int,

    @SerializedName("data")
    override val dataContent: UserCommon?,

    override val msg: String
) : BaseResponse<UserCommon>()