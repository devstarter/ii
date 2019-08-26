package org.ayfaar.app.utils

import org.mockito.Mockito

fun Any?.oneOf(vararg subject: Any) = subject.contains(this)

inline fun <A, B, C> Iterable<Pair<A, B>>.mapSecond(transform: (B) -> C) = this.map { (a, b) -> Pair(a, transform(b)) }


fun <T> uninitialized(): T = null as T

fun  anyInt() = Mockito.anyInt()
fun  anyString() = Mockito.anyString()

fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}

fun <T> eq(value: T): T {
    Mockito.eq<T>(value)
    return uninitialized()
}
