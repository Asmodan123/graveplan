package de.hannisoft.graveplan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GraveSite {
    public static final String NAME_SIZE = "plangröße=";
    private static final int NAME_SIZE_LEN = NAME_SIZE.length();

    private final String field;
    private final String row;
    private final int rowInt;
    private final String place;
    private final int placeInt;
    private final String id;
    private final String fileName;

    private GraveSiteType type;
    private String name;
    private Date validFrom;
    private Date validTo;
    private Owner owner;
    private int size;
    private int rowSize = 0;
    private int placeSize = 0;

    private List<Grave> graves = new ArrayList<>();
    private List<String> criterias = null;

    public GraveSite(String field, String row, String place) {
        this.field = field;
        this.row = row;
        this.rowInt = Integer.parseInt(row.replaceAll("[^0-9\\-]", ""));
        this.place = place;
        this.placeInt = Integer.parseInt(place.replaceAll("[^0-9\\-]", ""));
        this.id = createId(field, row, place);
        this.fileName = getFileName(this);
    }

    public static String createId(String field, String row, String place) {
        return new StringBuilder().append(field).append("/").append(row).append("/").append(place).toString();
    }

    public static String getFileName(GraveSite graveSite) {
        return new StringBuilder().append(graveSite.getField()).append("_").append(graveSite.getRow()).append("_")
                .append(graveSite.getPlace()).append(".html").toString();
    }

    public String getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getRow() {
        return row;
    }

    public String getPlace() {
        return place;
    }

    public GraveSiteType getType() {
        return type;
    }

    public void setType(GraveSiteType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (name != null) {
            try {
                int pos = name.toLowerCase().indexOf(NAME_SIZE);
                if (pos > -1) {
                    rowSize = Integer.parseInt(name.substring(pos + NAME_SIZE_LEN, pos + NAME_SIZE_LEN + 1));
                    placeSize = Integer.parseInt(name.substring(pos + NAME_SIZE_LEN + 1, pos + NAME_SIZE_LEN + 2));
                }
            } catch (Exception e) {
                System.err.println("InvalGrossid GraveName '" + name + "': " + e.getMessage());
                return;
            }
        }
        if (rowSize == 0 || placeSize == 0) {
            System.err.println("Invalid GraveName '" + name + "' on " + this);
        }
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<Grave> getGraves() {
        return graves;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ").append(id);
        return sb.toString();
    }

    public int getRowInt() {
        return rowInt;
    }

    public int getPlaceInt() {
        return placeInt;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getPlaceSize() {
        return placeSize;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getCriterias() {
        return criterias == null ? Collections.emptyList() : criterias;
    }

    public void addCriteria(String crit) {
        if (crit == null || crit.trim().isEmpty()) {
            return;
        }

        if (criterias == null) {
            criterias = new ArrayList<>();
        }
        criterias.add(crit);
    }

    public boolean isBroached() {
        return criterias != null && criterias.contains("abgeräumt");
    }

    public boolean isStele() {
        return criterias != null && criterias.contains("Stele");
    }
}
