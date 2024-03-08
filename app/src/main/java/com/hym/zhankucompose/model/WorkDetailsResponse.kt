package com.hym.zhankucompose.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * GET https://api.zcool.com.cn/v2/api/work/ZNTY3NDkxOTI=.html?app=android
 */
@Keep
data class WorkDetailsResponse(
    override val code: Int,

    @SerializedName("data")
    override val dataContent: WorkDetails?,

    override val msg: String
) : BaseResponse<WorkDetails>()