package com.hym.zhankukotlin.model

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

abstract class CateTypeAdapter<T : Cate> : TypeAdapter<T>() {
    abstract val cateCreator: Cate.CateCreator<T>

    override fun read(reader: JsonReader): T? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
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
        var subCateList: List<SubCate>? = null
        var type: Int? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "backgroundImage" -> {
                    when (reader.peek()) {
                        JsonToken.STRING -> backgroundImage = reader.nextString()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "commonOrderNo" -> {
                    when (reader.peek()) {
                        JsonToken.NUMBER -> commonOrderNo = reader.nextInt()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "description" -> {
                    when (reader.peek()) {
                        JsonToken.STRING -> description = reader.nextString()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "descriptionEn" -> {
                    when (reader.peek()) {
                        JsonToken.STRING -> descriptionEn = reader.nextString()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "icon" -> {
                    when (reader.peek()) {
                        JsonToken.STRING -> icon = reader.nextString()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "iconHover" -> {
                    when (reader.peek()) {
                        JsonToken.STRING -> iconHover = reader.nextString()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "id" -> {
                    when (reader.peek()) {
                        JsonToken.NUMBER -> id = reader.nextInt()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "level" -> {
                    when (reader.peek()) {
                        JsonToken.NUMBER -> level = reader.nextInt()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "name" -> {
                    when (reader.peek()) {
                        JsonToken.STRING -> name = reader.nextString()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "nameEn" -> {
                    when (reader.peek()) {
                        JsonToken.STRING -> nameEn = reader.nextString()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "orderNo" -> {
                    when (reader.peek()) {
                        JsonToken.NUMBER -> orderNo = reader.nextInt()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "parent" -> {
                    when (reader.peek()) {
                        JsonToken.NUMBER -> parent = reader.nextInt()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "statusId" -> {
                    when (reader.peek()) {
                        JsonToken.NUMBER -> statusId = reader.nextInt()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "subCateList" -> {
                    when (reader.peek()) {
                        JsonToken.BEGIN_ARRAY -> {
                            val tempList = mutableListOf<SubCate>()
                            reader.beginArray()
                            while (reader.hasNext()) {
                                when (reader.peek()) {
                                    JsonToken.BEGIN_OBJECT -> SubCate.SubCateTypeAdapter.read(reader)
                                        ?.let { tempList.add(it) }
                                    JsonToken.NULL -> reader.nextNull()
                                    else -> reader.skipValue()
                                }
                            }
                            reader.endArray()
                            subCateList = tempList
                        }
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                "type" -> {
                    when (reader.peek()) {
                        JsonToken.NUMBER -> type = reader.nextInt()
                        JsonToken.NULL -> reader.nextNull()
                        else -> reader.skipValue()
                    }
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        id?.let {
            Cate.getCategory<T>(it)?.let { cache ->
                if (cache.subCateList.isNotEmpty() && subCateList.isNullOrEmpty()) return cache
                if (cache.description.isNotBlank() && description.isNullOrBlank()) return cache
            }
        }
        return cateCreator.create(
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
            subCateList = subCateList ?: emptyList(),
            type = type ?: 0
        )
    }

    override fun write(writer: JsonWriter, obj: T?) {
        if (obj == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()
        writer.name("backgroundImage").value(obj.backgroundImage)
        writer.name("commonOrderNo").value(obj.commonOrderNo)
        writer.name("description").value(obj.description)
        writer.name("descriptionEn").value(obj.descriptionEn)
        writer.name("icon").value(obj.icon)
        writer.name("iconHover").value(obj.iconHover)
        writer.name("id").value(obj.id)
        writer.name("level").value(obj.level)
        writer.name("name").value(obj.name)
        writer.name("nameEn").value(obj.nameEn)
        writer.name("orderNo").value(obj.orderNo)
        writer.name("parent").value(obj.parent)
        writer.name("statusId").value(obj.statusId)
        writer.name("subCateList")
        writer.beginArray()
        obj.subCateList.forEach {
            SubCate.SubCateTypeAdapter.write(writer, it)
        }
        writer.endArray()
        writer.name("type").value(obj.type)
        writer.endObject()
    }
}
