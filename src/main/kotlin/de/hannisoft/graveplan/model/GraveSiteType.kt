package de.hannisoft.graveplan.model

enum class GraveSiteType(
    val fullName: String?,
    val shortName: String,
    val wahlgrab: Boolean,
    val sarg: Boolean,
    val urne: Boolean,
    val rasenlage: Boolean
) {
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

    val isReihengrab: Boolean get() = !wahlgrab

    companion object {
        private val nameMap: MutableMap<String, GraveSiteType> by lazy {
            values()
                .filter { it.fullName != null }
                .associateByTo(mutableMapOf()) { it.fullName!! }
        }

        fun getTypeByName(name: String?): GraveSiteType {
            if (name == null) return UNBEKANNT
            return nameMap[name] ?: run {
                System.err.println("Unknown GraveSiteType $name")
                UNBEKANNT
            }
        }
    }
}
