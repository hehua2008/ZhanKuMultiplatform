package com.hym.zhankukotlin.network

enum class Order(val path: String) {
    ALL_RECOMMEND("1!-1!"), EDITOR_CHOICE("2!-1!"), HOME_RECOMMEND("3!-1!"), LATEST_PUBLISH("-1!0!")
}