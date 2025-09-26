package de.hannisoft.graveplan.model;

import java.util.HashMap;
import java.util.Map;

public enum GraveSiteType {
 // @formatter:off
    UNBEKANNT(null, "?", false, false, false, false),
    WAHLGRAB("Wahlgrab", "WG", true, true, true, false),
    WAHLGRAB_RASEN("Wahlgrab (Rasenlage)", "WG R", true, true, true, true),
    WAHLGRAB_URNE_RASEN("Wahlgrab Urne (Rasenlage)", "WG U/R", true, false, true, true),
    WAHLGRAB_GRUEN("Wahlgrab (Grünes Grab)", "WG G", true, true, true, false),
    REIHENGRAB("Reihengrab", "RG", false, true, true, false),
    REIHENGRAB_RASEN("Reihengrab (Rasenlage)", "RG R", false, true, true, true),
    REIHENGRAB_URNE_RASEN("Reihengrab Urne (Rasenlage)", "RG U/R", false, false, true, true),
    REIHENGRAB_GRUEN("Reihengrab (Grünes Grab)", "RG G", false, true, true, false),
    ROSENGRAB("Rosengrab", "Ros", false, false, true, false),
    GESPERRT("GESPERRT", "X", false, false, false, false),
    KRIEGSGRAEBER("Kriegsgräber", "Krieg", false, false, false, false);
 // @formatter:on
    private static final Map<String, GraveSiteType> nameMap = new HashMap<>();

    private final String name;
    private final String shortName;
    private final boolean wahlgrab;
    private final boolean sarg;
    private final boolean urne;
    private final boolean rasenlage;

    private GraveSiteType(String name, String shortName, boolean wahlgrab, boolean sarg, boolean urne, boolean rasenlage) {
        this.name = name;
        this.shortName = shortName;
        this.wahlgrab = wahlgrab;
        this.sarg = sarg;
        this.urne = urne;
        this.rasenlage = rasenlage;
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

    public String getShortName() {
        return this.shortName;
    }

    public boolean isWahlgrab() {
        return wahlgrab;
    }

    public boolean isReihengrab() {
        return !wahlgrab;
    }

    public boolean isRasenlage() {
        return rasenlage;
    }

    public boolean isSarg() {
        return sarg;
    }

    public boolean isUrne() {
        return urne;
    }

    public static GraveSiteType getTypeByName(String name) {
        if (name == null) {
            return UNBEKANNT;
        }
        GraveSiteType type = getNameMap().get(name);
        if (type == null) {
            System.err.println("Unknown GraveSiteType " + name);
        }
        return type == null ? UNBEKANNT : type;
    }
}
