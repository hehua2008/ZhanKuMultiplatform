package com.hym.zhankukotlin.network

import java.util.*

class PreviewItem private constructor(
        @JvmField
        val imageUrl: String? = null,

        @JvmField
        val targetUrl: String? = null,

        @JvmField
        val title: String? = null,

        @JvmField
        val views: String? = null,

        @JvmField
        val comments: String? = null,

        @JvmField
        val favorites: String? = null,

        @JvmField
        val author: String? = null,

        @JvmField
        val time: String? = null,

        @JvmField
        val category: CategoryItem? = null
) {
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
            return PreviewItem(
                    imageUrl,
                    targetUrl,
                    title,
                    views,
                    comments,
                    favorites,
                    author,
                    time,
                    category
            )
        }
    }
}