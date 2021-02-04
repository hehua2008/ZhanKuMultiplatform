package com.hym.zhankukotlin.network

import java.util.*

class PreviewItem private constructor() {
    @JvmField
    var imageUrl: String? = null

    @JvmField
    var targetUrl: String? = null

    @JvmField
    var title: String? = null

    @JvmField
    var views: String? = null

    @JvmField
    var comments: String? = null

    @JvmField
    var favorites: String? = null

    @JvmField
    var author: String? = null

    @JvmField
    var time: String? = null

    @JvmField
    var category: CategoryItem? = null

    override fun hashCode(): Int {
        return Objects.hash(targetUrl ?: imageUrl)
    }

    override fun equals(obj: Any?): Boolean {
        return when {
            obj !is PreviewItem -> false
            targetUrl != null -> {
                targetUrl == obj.targetUrl
            }
            else -> {
                imageUrl == obj.imageUrl
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(imageUrl).append('\n')
            .append(targetUrl).append('\n')
            .append(title).append('\n')
            .append(views).append('\n')
            .append(comments).append('\n')
            .append(favorites).append('\n')
            .append(author).append('\n')
            .append(time).append('\n')
            .append(category).append('\n')
        return sb.toString()
    }

    class Builder {
        private var imageUrl: String? = null
        private var targetUrl: String? = null
        private var title: String? = null
        private var views: String? = null
        private var comments: String? = null
        private var favorites: String? = null
        private var author: String? = null
        private var time: String? = null
        private var category: CategoryItem? = null

        fun imageUrl(imageUrl: String?): Builder {
            this.imageUrl = imageUrl
            return this
        }

        fun targetUrl(targetUrl: String?): Builder {
            this.targetUrl = targetUrl
            return this
        }

        fun title(title: String?): Builder {
            this.title = title
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

        fun author(author: String?): Builder {
            this.author = author
            return this
        }

        fun time(time: String?): Builder {
            this.time = time
            return this
        }

        fun category(category: CategoryItem?): Builder {
            this.category = category
            return this
        }

        fun build(): PreviewItem {
            val ret = PreviewItem()
            ret.imageUrl = imageUrl
            ret.targetUrl = targetUrl
            ret.title = title
            ret.views = views
            ret.comments = comments
            ret.favorites = favorites
            ret.author = author
            ret.time = time
            ret.category = category

            imageUrl = null
            targetUrl = null
            title = null
            views = null
            comments = null
            favorites = null
            author = null
            time = null
            category = null

            return ret
        }
    }
}