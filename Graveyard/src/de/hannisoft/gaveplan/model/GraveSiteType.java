package de.hannisoft.gaveplan.model;

import java.util.HashMap;
import java.util.Map;

public enum GraveSiteType {
    UNBEKANNT(null,false, false,false,false), 
    WAHLGRAB("Wahlgrab", true,true,true,false), 
    WAHLGRAB_RASEN("Wahlgrab (Rasenlage)",true,true,true,true), 
    WAHLGRAB_URNE_RASEN("Wahlgrab Urne (Rasenlage)",true,false,true,true),
    WAHLGRAB_GRUEN("Wahlgrab (Grünes Grab)",true,true,true,false),
    REIHENGRAB("Reihengrab",false,true,true,false),
    REIHENGRAB_RASEN("Reihengrab (Rasenlage)",false,true,true,true),
    REIHENGRAB_URNE_RASEN("Reihengrab Urne (Rasenlage)", false,false,true,true),
    REIHENGRAB_GRUEN("Reihengrab (Grünes Grab)", false,true,true,false),
    ROSENGRAB("Rosengrab",false,false,true,false),
    GESPERRT("GESPERRT",false,false,false,false),
    KRIEGSGRAEBER("Kriegsgräber",false,false,false,false);

    private static final Map<String, GraveSiteType> nameMap = new HashMap<>();

    private final String name;
    private final boolean wahlgrab;
    private final boolean sarg;
    private final boolean urne;
    private final boolean rasenlage;

    private GraveSiteType(String name, boolean wahlgrab, boolean sarg, boolean urne, boolean rasenlage) {
        this.name = name;
        this.wahlgrab=wahlgrab;
        this.sarg=sarg;
        this.urne=urne;
        this.rasenlage=rasenlage;
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
        	System.err.println("Unknown GraveSiteType "+name);
        }
        return type == null ? UNBEKANNT : type;
    }
}
