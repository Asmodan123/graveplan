package de.hannisoft.gaveplan.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Place {
    private final Grave grave;
    private final String row;
    private final String id;
    private int rowInt = Integer.MIN_VALUE;
    private final String place;
    private int placeInt = Integer.MIN_VALUE;
    private String deceased;
    private Date dateOfDeatch;
    private Collection<PlaceClass> classes = new HashSet<>();
    private int runtimeYear = 0;

    public Place(Grave grave, String row, String place) {
        this.grave = grave;
        this.row = row;
        this.place = place;
        this.id = row + "/" + place;
    }

    public String getDeceased() {
        return deceased;
    }

    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }

    public Date getDateOfDeatch() {
        return dateOfDeatch;
    }

    public void setDateOfDeatch(Date dateOfDeatch) {
        this.dateOfDeatch = dateOfDeatch;
    }

    public Grave getGrave() {
        return grave;
    }

    public String getRow() {
        return row;
    }

    public String getPlace() {
        return place;
    }

    public int getRowInt() {
        if (rowInt == Integer.MIN_VALUE) {
            rowInt = Integer.parseInt(row.replaceAll("[^0-9\\-]", ""));
        }
        return rowInt;
    }

    public int getPlaceInt() {
        if (placeInt == Integer.MIN_VALUE) {
            placeInt = Integer.parseInt(place.replaceAll("[^0-9\\-]", ""));
        }
        return placeInt;
    }

    public Collection<PlaceClass> getClasses() {
        return classes;
    }

    public String getClassesStirng() {
        StringBuilder sb = new StringBuilder();
        for (PlaceClass classs : classes) {
            sb.append(classs.toString().toLowerCase()).append(' ');
        }
        if (runtimeYear != 0) {
            sb.append("LZ").append(runtimeYear);
        }
        return sb.toString();
    }

    public void addClasses(PlaceClass classes) {
        this.classes.add(classes);
    }

    public boolean isEmpty() {
        return deceased == null || deceased.trim().isEmpty();
    }

    public boolean isRef() {
        return row.equals(grave.getRow()) && place.equals(grave.getPlace());
    }

    public String getId() {
        return id;
    }

    public String getReference() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        StringBuilder sb = new StringBuilder();
        Owner ow = getGrave().getOwner();
        sb.append("Gabstätte: ").append(getGrave().getId());
        sb.append(";===================");
        sb.append(";Nutzungsrecht bis ").append(dateFormat.format(getGrave().getValidTo()));
        sb.append(";").append(getGrave().getName());
        sb.append(";;Nutzungsberechtigter:");
        sb.append(";-----------------------------------;");
        sb.append(ow.getFirstName()).append(' ').append(ow.getLastName()).append(';');
        sb.append(ow.getStreet()).append(';');
        sb.append(ow.getZipAndTown());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(grave.getField());
        sb.append('/').append(row);
        sb.append('/').append(place);
        sb.append(' ').append(deceased);
        sb.append(" ref=").append(grave);
        return sb.toString();
    }

    public int getRuntimeYear() {
        return runtimeYear;
    }

    public void setRuntimeYear(int runtimeYear) {
        this.runtimeYear = runtimeYear;
    }
}
