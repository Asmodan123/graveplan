package de.hannisoft.gaveplan.model;

import java.util.HashMap;
import java.util.Map;

public enum ElementType {
    UNKONWN(""), RAND("rand"), FELD("feld");

    private static final Map<String, ElementType> nameMap = new HashMap<>();

    private final String name;

    private ElementType(String name) {
        this.name = name;
    }

    private static java.util.Map<String, ElementType> getNameMap() {
        if (nameMap.size() == 0) {
            synchronized (nameMap) {
                if (nameMap.size() == 0) {
                    for (ElementType elementType : ElementType.class.getEnumConstants()) {
                        nameMap.put(elementType.getName(), elementType);
                    }
                }
            }
        }
        return nameMap;
    }

    public String getName() {
        return this.name;
    }

    public static ElementType getTypeByName(String name) {
        if (name == null) {
            return UNKONWN;
        }
        ElementType type = getNameMap().get(name);
        return type == null ? UNKONWN : type;
    }
}
