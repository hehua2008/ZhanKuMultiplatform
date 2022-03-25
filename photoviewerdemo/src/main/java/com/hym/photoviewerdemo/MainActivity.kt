package com.hym.photoviewerdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hym.photoviewer.PhotoViewActivity
import com.hym.photoviewer.UrlPhotoInfo

class MainActivity : Activity(), View.OnClickListener {
    private lateinit var photoInfos: ArrayList<UrlPhotoInfo>
    private lateinit var photoViewIntent: Intent

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button).setOnClickListener(this)

        photoInfos = arrayListOf(
            UrlPhotoInfo(
                original = "https://img.zcool.cn/community/01ba8e622958b311013f01cde3da64.jpg@1600w_1l_2o_100sh.jpg",
                thumb = "https://img.zcool.cn/community/01ba8e622958b311013f01cde3da64.jpg@1280w_1l_2o_100sh.jpg",
                width = 1600,
                height = 1068
            ),
            UrlPhotoInfo(
                original = "https://img.zcool.cn/community/01de3e622958b211013e8cd055e2f8.jpg@1600w_1l_2o_100sh.jpg",
                thumb = "https://img.zcool.cn/community/01de3e622958b211013e8cd055e2f8.jpg@1280w_1l_2o_100sh.jpg",
                width = 1600,
                height = 1068
            ),
            UrlPhotoInfo(
                original = "https://img.zcool.cn/community/01d9a1622958b211013e8cd0bd1367.jpg@1600w_1l_2o_100sh.jpg",
                thumb = "https://img.zcool.cn/community/01d9a1622958b211013e8cd0bd1367.jpg@1280w_1l_2o_100sh.jpg",
                width = 1600,
                height = 1067
            ),
            UrlPhotoInfo(
                original = "https://img.zcool.cn/community/0131e8622958b011013f01cdb4a7da.jpg@1600w_1l_2o_100sh.jpg",
                thumb = "https://img.zcool.cn/community/0131e8622958b011013f01cdb4a7da.jpg@1280w_1l_2o_100sh.jpg",
                width = 1600,
                height = 1065
            ),
            UrlPhotoInfo(
                original = "https://img.zcool.cn/community/01cd23622958b111013f01cd3b0b8f.jpg@1600w_1l_2o_100sh.jpg",
                thumb = "https://img.zcool.cn/community/01cd23622958b111013f01cd3b0b8f.jpg@1280w_1l_2o_100sh.jpg",
                width = 1600,
                height = 1067
            ),
            UrlPhotoInfo(
                original = "https://img.zcool.cn/community/01071c622958b211013e8cd08aa4e0.jpg@2000w_1l_2o_100sh.jpg",
                thumb = "https://img.zcool.cn/community/01071c622958b211013e8cd08aa4e0.jpg@1280w_1l_2o_100sh.jpg",
                width = 2000,
                height = 1333
            )
        )

        photoViewIntent = Intent(this, PhotoViewActivity::class.java)
            .putParcelableArrayListExtra(PhotoViewActivity.PHOTO_INFOS, photoInfos)
    }

    override fun onClick(v: View) {
        startActivity(photoViewIntent)
    }
}