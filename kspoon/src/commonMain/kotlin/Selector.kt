package dev.burnoo.ksoup

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

internal const val NULL_VALUE = "[null]"

internal fun String.handleNullability() = if (this == NULL_VALUE) null else this

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class Selector(
    val value: String,
    val attr: String = NULL_VALUE,
    val defValue: String = NULL_VALUE,
    val index: Int = 0,
    val regex: String = NULL_VALUE,
)
