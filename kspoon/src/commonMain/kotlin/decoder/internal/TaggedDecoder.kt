// Copied from kotlinx serialization 1.7.2, as suggested here:
// https://github.com/Kotlin/kotlinx.serialization/issues/2610#issuecomment-2018555242
/*
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */
@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("ktlint")

package dev.burnoo.ksoup.decoder.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/*
 * These classes are intended to be used only within the kotlinx.serialization.
 * They neither do have stable API, nor internal invariants and are changed without any warnings.
 */
internal abstract class TaggedDecoder<Tag : Any?> : Decoder, CompositeDecoder {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    protected abstract fun SerialDescriptor.getTag(index: Int): Tag

    // ---- API ----
    protected open fun decodeTaggedValue(tag: Tag): Any =
        throw SerializationException("${this::class} can't retrieve untyped values")

    protected open fun decodeTaggedNotNullMark(tag: Tag): Boolean = true
    protected open fun decodeTaggedNull(tag: Tag): Nothing? = null

    protected open fun decodeTaggedBoolean(tag: Tag): Boolean = decodeTaggedValue(tag) as Boolean
    protected open fun decodeTaggedByte(tag: Tag): Byte = decodeTaggedValue(tag) as Byte
    protected open fun decodeTaggedShort(tag: Tag): Short = decodeTaggedValue(tag) as Short
    protected open fun decodeTaggedInt(tag: Tag): Int = decodeTaggedValue(tag) as Int
    protected open fun decodeTaggedLong(tag: Tag): Long = decodeTaggedValue(tag) as Long
    protected open fun decodeTaggedFloat(tag: Tag): Float = decodeTaggedValue(tag) as Float
    protected open fun decodeTaggedDouble(tag: Tag): Double = decodeTaggedValue(tag) as Double
    protected open fun decodeTaggedChar(tag: Tag): Char = decodeTaggedValue(tag) as Char
    protected open fun decodeTaggedString(tag: Tag): String = decodeTaggedValue(tag) as String
    protected open fun decodeTaggedEnum(tag: Tag, enumDescriptor: SerialDescriptor): Int =
        decodeTaggedValue(tag) as Int

    protected open fun decodeTaggedInline(tag: Tag, inlineDescriptor: SerialDescriptor): Decoder = this.apply { pushTag(tag) }

    protected open fun <T : Any?> decodeSerializableValue(deserializer: DeserializationStrategy<T>, previousValue: T?): T =
        decodeSerializableValue(deserializer)

    // ---- Implementation of low-level API ----

    override fun decodeInline(descriptor: SerialDescriptor): Decoder =
        decodeTaggedInline(popTag(), descriptor)

    // TODO this method should be overridden by any sane format that supports top-level nulls
    override fun decodeNotNullMark(): Boolean {
        // Tag might be null for top-level deserialization
        val currentTag = currentTagOrNull ?: return false
        return decodeTaggedNotNullMark(currentTag)
    }

    final override fun decodeNull(): Nothing? = null

    final override fun decodeBoolean(): Boolean = decodeTaggedBoolean(popTag())
    final override fun decodeByte(): Byte = decodeTaggedByte(popTag())
    final override fun decodeShort(): Short = decodeTaggedShort(popTag())
    final override fun decodeInt(): Int = decodeTaggedInt(popTag())
    final override fun decodeLong(): Long = decodeTaggedLong(popTag())
    final override fun decodeFloat(): Float = decodeTaggedFloat(popTag())
    final override fun decodeDouble(): Double = decodeTaggedDouble(popTag())
    final override fun decodeChar(): Char = decodeTaggedChar(popTag())
    final override fun decodeString(): String = decodeTaggedString(popTag())

    final override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = decodeTaggedEnum(popTag(), enumDescriptor)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = this

    override fun endStructure(descriptor: SerialDescriptor) {
        // Nothing
    }

    final override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        decodeTaggedBoolean(descriptor.getTag(index))

    final override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        decodeTaggedByte(descriptor.getTag(index))

    final override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        decodeTaggedShort(descriptor.getTag(index))

    final override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        decodeTaggedInt(descriptor.getTag(index))

    final override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        decodeTaggedLong(descriptor.getTag(index))

    final override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        decodeTaggedFloat(descriptor.getTag(index))

    final override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        decodeTaggedDouble(descriptor.getTag(index))

    final override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        decodeTaggedChar(descriptor.getTag(index))

    final override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
        decodeTaggedString(descriptor.getTag(index))

    final override fun decodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Decoder = decodeTaggedInline(descriptor.getTag(index), descriptor.getElementDescriptor(index))

    final override fun <T : Any?> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T =
        tagBlock(descriptor.getTag(index)) { decodeSerializableValue(deserializer, previousValue) }

    final override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? = tagBlock(descriptor.getTag(index)) {
        decodeIfNullable(deserializer) {
            decodeSerializableValue(deserializer, previousValue)
        }
    }

    private fun <E> tagBlock(tag: Tag, block: () -> E): E {
        pushTag(tag)
        val r = block()
        if (!flag) {
            popTag()
        }
        flag = false
        return r
    }

    internal val tagStack: ArrayList<Tag> = arrayListOf()

    protected val currentTag: Tag
        get() = tagStack.last()
    protected val currentTagOrNull: Tag?
        get() = tagStack.lastOrNull()

    protected fun pushTag(name: Tag) {
        tagStack.add(name)
    }

    protected fun copyTagsTo(other: TaggedDecoder<Tag>) {
        other.tagStack.addAll(tagStack)
    }

    private var flag = false

    protected fun popTag(): Tag {
        val r = tagStack.removeAt(tagStack.lastIndex)
        flag = true
        return r
    }
}

@OptIn(ExperimentalSerializationApi::class)
private inline fun <T : Any> Decoder.decodeIfNullable(deserializer: DeserializationStrategy<T?>, block: () -> T?): T? {
    val isNullabilitySupported = deserializer.descriptor.isNullable
    return if (isNullabilitySupported || decodeNotNullMark()) block() else decodeNull()
}
