package com.applory.pictureserverkt

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.function.Function

class TestPage<T>: Page<T>{

    private var totalElements: Long = 0

    private var totalPages = 0

    private var number = 0

    private var numberOfElements = 0

    private var size = 0

    var last = false
    var first = false
    var next = false
    var previous = false

    private var content: List<T> = ArrayList()

    override fun getContent(): MutableList<T> {
        return content as MutableList<T>
    }

    override fun hasContent(): Boolean {
        return false
    }

    override fun getSort(): Sort {
        TODO("Not yet implemented")
    }

    override fun isFirst(): Boolean {
        return first
    }

    override fun isLast(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasNext(): Boolean {
        return next
    }

    override fun hasPrevious(): Boolean {
        return previous
    }

    override fun nextPageable(): Pageable {
        TODO("Not yet implemented")
    }

    override fun previousPageable(): Pageable {
        TODO("Not yet implemented")
    }

    override fun <U : Any?> map(converter: Function<in T, out U>): Page<U> {
        TODO("Not yet implemented")
    }

    override fun getTotalElements(): Long {
        return totalElements
    }

    override fun getTotalPages(): Int {
        return totalPages
    }

    override fun iterator(): MutableIterator<T> {
        TODO("Not yet implemented")
    }

    override fun getNumber(): Int {
        return number
    }

    override fun getNumberOfElements(): Int {
        return numberOfElements
    }

    override fun getSize(): Int {
        return size
    }
}
