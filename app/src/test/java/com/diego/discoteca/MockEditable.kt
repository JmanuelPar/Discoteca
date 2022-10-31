package com.diego.discoteca

import android.text.Editable
import android.text.InputFilter

class MockEditable(private val str: String) : Editable {

    override fun toString() = str

    override val length: Int
        get() = str.length

    override fun get(index: Int): Char {
        TODO("Not required")
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        TODO("Not required")
    }

    override fun getChars(start: Int, end: Int, dest: CharArray?, destoff: Int) {
        TODO("Not required")
    }

    override fun <T : Any?> getSpans(start: Int, end: Int, type: Class<T>?): Array<T> {
        TODO("Not required")
    }

    override fun getSpanStart(tag: Any?): Int {
        TODO("Not required")
    }

    override fun getSpanEnd(tag: Any?): Int {
        TODO("Not required")
    }

    override fun getSpanFlags(tag: Any?): Int {
        TODO("Not required")
    }

    override fun nextSpanTransition(start: Int, limit: Int, type: Class<*>?): Int {
        TODO("Not required")
    }

    override fun setSpan(what: Any?, start: Int, end: Int, flags: Int) {
        TODO("Not required")
    }

    override fun removeSpan(what: Any?) {
        TODO("Not required")
    }

    override fun append(text: CharSequence?): Editable {
        TODO("Not required")
    }

    override fun append(text: CharSequence?, start: Int, end: Int): Editable {
        TODO("Not required")
    }

    override fun append(text: Char): Editable {
        TODO("Not required")
    }

    override fun replace(st: Int, en: Int, source: CharSequence?, start: Int, end: Int): Editable {
        TODO("Not required")
    }

    override fun replace(st: Int, en: Int, text: CharSequence?): Editable {
        TODO("Not required")
    }

    override fun insert(where: Int, text: CharSequence?, start: Int, end: Int): Editable {
        TODO("Not required")
    }

    override fun insert(where: Int, text: CharSequence?): Editable {
        TODO("Not required")
    }

    override fun delete(st: Int, en: Int): Editable {
        TODO("Not required")
    }

    override fun clear() {
        TODO("Not required")
    }

    override fun clearSpans() {
        TODO("Not required")
    }

    override fun setFilters(filters: Array<out InputFilter>?) {
        TODO("Not required")
    }

    override fun getFilters(): Array<InputFilter> {
        TODO("Not required")
    }
}