package org.ayfaar.app.utils

fun Any?.oneOf(vararg subject: Any) = subject.contains(this)

inline fun <A, B, C> Iterable<Pair<A, B>>.mapSecond(transform: (B) -> C) = this.map { (a, b) -> Pair(a, transform(b)) }