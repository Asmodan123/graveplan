package de.hannisoft.gaveplan.model;

import java.util.HashMap;
import java.util.Map;

public enum GraveSiteType {
    UNBEKANNT(null), WAHLGRAB("Wahlgrab");

    private static final Map<String, GraveSiteType> nameMap = new HashMap<>();

    private final String name;

    private GraveSiteType(String name) {
        this.name = name;
    }

    private static java.util.Map<String, GraveSiteType> getNameMap() {
        if (nameMap.size() == 0) {
            synchronized (nameMap) {
                if (nameMap.size() == 0) {
                    for (GraveSiteType graveType : GraveSiteType.class.getEnumConstants()) {
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

    public static GraveSiteType getTypeByName(String name) {
        if (name == null) {
            return UNBEKANNT;
        }
        GraveSiteType type = getNameMap().get(name);
        return type == null ? UNBEKANNT : type;
    }
}
