package de.hannisoft.gaveplan.model;

import java.util.HashMap;
import java.util.Map;

public enum GraveType {
    UNBEKANNT(null), WAHLGRAB("Wahlgrab");

    private static final Map<String, GraveType> nameMap = new HashMap<>();

    private final String name;

    private GraveType(String name) {
        this.name = name;
    }

    private static java.util.Map<String, GraveType> getNameMap() {
        if (nameMap.size() == 0) {
            synchronized (nameMap) {
                if (nameMap.size() == 0) {
                    for (GraveType graveType : GraveType.class.getEnumConstants()) {
                        nameMap.put(graveType.getName(), graveType);
                    }
                }
            }
        }
        return nameMap;
    }

    public String getName() {
        return this.name;
    }

    public static GraveType getTypeByName(String name) {
        if (name == null) {
            return UNBEKANNT;
        }
        GraveType type = getNameMap().get(name);
        return type == null ? UNBEKANNT : type;
    }
}
