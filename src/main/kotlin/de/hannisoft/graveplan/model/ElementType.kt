package de.hannisoft.graveplan.model

import kotlin.collections.get

enum class ElementType(val typeName: String) {
    UNKONWN(""),
    RAND("rand"),
    FELD("feld"),
    RASTER("raster");

    companion object {
        private val nameMap: Map<String, ElementType> by lazy {
            entries.associateBy { it.typeName }
        }

        fun getTypeByName(name: String?): ElementType =
            nameMap[name] ?: UNKONWN
    }
}