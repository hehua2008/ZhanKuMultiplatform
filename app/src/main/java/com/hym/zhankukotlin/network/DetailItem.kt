package com.hym.zhankukotlin.network

class DetailItem private constructor() {
    @JvmField
    var title: String? = null

    @JvmField
    var time: String? = null

    @JvmField
    var views: String? = null

    @JvmField
    var comments: String? = null

    @JvmField
    var favorites: String? = null

    lateinit var categorys: List<CategoryItem>

    lateinit var imgUrls: List<String>

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(title).append('\n')
            .append(time).append('\n')
            .append(views).append('\n')
            .append(comments).append('\n')
            .append(favorites).append('\n')
            .append(categorys).append('\n')
            .append(imgUrls).append('\n')
        return sb.toString()
    }

    class Builder {
        private var title: String? = null
        private var time: String? = null
        private var views: String? = null
        private var comments: String? = null
        private var favorites: String? = null
        private var categorys: MutableList<CategoryItem> = mutableListOf()
        private var imgUrls: MutableList<String> = mutableListOf()

        fun title(title: String?): Builder {
            this.title = title
            return this
        }

        fun time(time: String?): Builder {
            this.time = time
            return this
        }

        fun views(views: String?): Builder {
            this.views = views
            return this
        }

        fun comments(comments: String?): Builder {
            this.comments = comments
            return this
        }

        fun favorites(favorites: String?): Builder {
            this.favorites = favorites
            return this
        }

        fun addCategory(category: CategoryItem): Builder {
            categorys.add(category)
            return this
        }

        fun addImageUrl(imgUrl: String): Builder {
            imgUrls.add(imgUrl)
            return this
        }

        fun build(): DetailItem {
            val ret = DetailItem()
            ret.title = title
            ret.time = time
            ret.views = views
            ret.comments = comments
            ret.favorites = favorites
            ret.categorys = categorys
            ret.imgUrls = imgUrls

            title = null
            time = null
            views = null
            comments = null
            favorites = null
            categorys = mutableListOf()
            imgUrls = mutableListOf()

            return ret
        }
    }
}