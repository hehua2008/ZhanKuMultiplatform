package com.hym.photoviewer

/**
 * @author hehua2008
 * @date 2022/3/25
 */
interface PhotoSaver {
    companion object {
        internal lateinit var INSTANCE: PhotoSaver

        @JvmStatic
        fun setInstance(photoSaver: PhotoSaver) {
            synchronized(PhotoSaver) {
                INSTANCE = photoSaver
            }
        }
    }

    fun onSave(vararg photoInfos: PhotoInfo<*>)
}