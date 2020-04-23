package com.cniekirk.traintimes.model.adapter

import com.squareup.moshi.JsonQualifier

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@JsonQualifier
annotation class SingleToArray