package com.cniekirk.traintimes.utils.extensions

fun <E> Iterable<E>.updated(index: Int, elem: E) = mapIndexed { i, existing ->  if (i == index) elem else existing }