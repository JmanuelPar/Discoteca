package com.diego.discoteca.util

enum class Destination {
    API,
    DATABASE,
    NONE
}

enum class AddBy(val code: Int) {
    MANUALLY(1),
    SCAN(2),
    SEARCH(3),
    NONE(-1)
}