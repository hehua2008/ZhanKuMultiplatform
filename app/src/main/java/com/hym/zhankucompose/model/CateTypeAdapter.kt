package com.hym.zhankucompose.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

abstract class CateTypeAdapter<T : Cate>(private val clazz: Class<T>) : KSerializer<T> {
    abstract val cateCreator: Cate.CateCreator<T>

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(clazz.simpleName) {
            element<String>("backgroundImage")
            element<Int>("commonOrderNo")
            element<String>("description")
            element<String>("descriptionEn")
            element<String>("icon")
            element<String>("iconHover")
            element<Int>("id")
            element<Int>("level")
            element<String>("name")
            element<String>("nameEn")
            element<Int>("orderNo")
            element<Int>("parent")
            element<Int>("statusId")
            element<Int>("type")
            if (clazz != SubCate::class.java) {
                element<List<SubCate>>("subCateList")
            }
        }

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeStructure(descriptor) {
            var backgroundImage: String? = null
            var commonOrderNo: Int? = null
            var description: String? = null
            var descriptionEn: String? = null
            var icon: String? = null
            var iconHover: String? = null
            var id: Int? = null
            var level: Int? = null
            var name: String? = null
            var nameEn: String? = null
            var orderNo: Int? = null
            var parent: Int? = null
            var statusId: Int? = null
            var type: Int? = null
            var subCateList: List<SubCate>? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> backgroundImage = decodeStringElement(descriptor, 0)
                    1 -> commonOrderNo = decodeIntElement(descriptor, 1)
                    2 -> description = decodeStringElement(descriptor, 2)
                    3 -> descriptionEn = decodeStringElement(descriptor, 3)
                    4 -> icon = decodeStringElement(descriptor, 4)
                    5 -> iconHover = decodeStringElement(descriptor, 5)
                    6 -> id = decodeIntElement(descriptor, 6)
                    7 -> level = decodeIntElement(descriptor, 7)
                    8 -> name = decodeStringElement(descriptor, 8)
                    9 -> nameEn = decodeStringElement(descriptor, 9)
                    10 -> orderNo = decodeIntElement(descriptor, 10)
                    11 -> parent = decodeIntElement(descriptor, 11)
                    12 -> statusId = decodeIntElement(descriptor, 12)
                    13 -> type = decodeIntElement(descriptor, 13)
                    14 -> subCateList = if (clazz != SubCate::class.java) {
                        decodeSerializableElement(
                            descriptor, 14, ListSerializer(SubCate.SubCateTypeAdapter)
                        )
                    } else null

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            id?.let {
                Cate.getCategory<T>(it)?.let { cache ->
                    if (cache.subCateList.isNotEmpty() && subCateList.isNullOrEmpty()) return@decodeStructure cache
                    if (cache.description.isNotBlank() && description.isNullOrBlank()) return@decodeStructure cache
                }
            }
            return@decodeStructure cateCreator.create(
                backgroundImage = backgroundImage ?: "",
                commonOrderNo = commonOrderNo ?: 0,
                description = description ?: "",
                descriptionEn = descriptionEn ?: "",
                icon = icon ?: "",
                iconHover = iconHover ?: "",
                id = id ?: 0,
                level = level ?: 0,
                name = name ?: "",
                nameEn = nameEn ?: "",
                orderNo = orderNo ?: 0,
                parent = parent ?: 0,
                statusId = statusId ?: 0,
                type = type ?: 0,
                subCateList = subCateList ?: emptyList()
            )
        }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.backgroundImage)
            encodeIntElement(descriptor, 1, value.commonOrderNo)
            encodeStringElement(descriptor, 2, value.description)
            encodeStringElement(descriptor, 3, value.descriptionEn)
            encodeStringElement(descriptor, 4, value.icon)
            encodeStringElement(descriptor, 5, value.iconHover)
            encodeIntElement(descriptor, 6, value.id)
            encodeIntElement(descriptor, 7, value.level)
            encodeStringElement(descriptor, 8, value.name)
            encodeStringElement(descriptor, 9, value.nameEn)
            encodeIntElement(descriptor, 10, value.orderNo)
            encodeIntElement(descriptor, 11, value.parent)
            encodeIntElement(descriptor, 12, value.statusId)
            encodeIntElement(descriptor, 13, value.type)
            if (clazz != SubCate::class.java) {
                encodeSerializableElement(
                    descriptor, 14, ListSerializer(SubCate.SubCateTypeAdapter), value.subCateList
                )
            }
        }
    }
}
