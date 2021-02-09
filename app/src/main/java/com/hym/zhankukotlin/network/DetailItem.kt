package com.hym.zhankukotlin.network

class DetailItem private constructor(
    @JvmField
    val title: String? = null,

    @JvmField
    val time: String? = null,

    @JvmField
    val views: String? = null,

    @JvmField
    val comments: String? = null,

    @JvmField
    val favorites: String? = null,

    val categorys: List<CategoryItem> = emptyList(),

    val imgUrls: List<String> = emptyList()
) {
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
            return DetailItem(title, time, views, comments, favorites, categorys, imgUrls)
        }
    }
}