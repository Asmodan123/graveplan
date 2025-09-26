package de.hannisoft.gaveplan.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Grave {
    private final GraveSite graveSite;
    private final String row;
    private final String id;
    private int rowInt = Integer.MIN_VALUE;
    private final String place;
    private int placeInt = Integer.MIN_VALUE;
    private String deceased;
    private Date dateOfBirth;
    private Date dateOfDeath;
    private Collection<GraveClass> classes = new HashSet<>();
    private int runtimeYear = 0;

    public Grave(GraveSite graveSite, String row, String place) {
        this.graveSite = graveSite;
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

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setDateOfDeatch(Date dateOfDeatch) {
        this.dateOfDeath = dateOfDeatch;
    }

    public GraveSite getGraveSite() {
        return graveSite;
    }

    public String getRow() {
        return row;
    }

    public String getPlace() {
        return place;
    }

    public int getRowInt() {
        if (rowInt == Integer.MIN_VALUE) {
            String rowStr = row.trim();
            int i = rowStr.indexOf(" ");
            if (i > 0) {
                rowStr = rowStr.substring(0, i - 1);
            }
            rowInt = Integer.parseInt(rowStr.replaceAll("[^0-9\\-]", ""));
        }
        return rowInt;
    }

    public int getPlaceInt() {
        if (placeInt == Integer.MIN_VALUE) {
            String placeStr = place.trim();
            int i = placeStr.indexOf(" ");
            if (i > 0) {
                placeStr = placeStr.substring(0, i - 1);
            }
            placeInt = Integer.parseInt(placeStr.replaceAll("[^0-9\\-]", ""));
        }
        return placeInt;
    }

    public Collection<GraveClass> getClasses() {
        return classes;
    }

    public String getClassesStirng() {
        StringBuilder sb = new StringBuilder();
        for (GraveClass classs : classes) {
            sb.append(classs.toString().toLowerCase()).append(' ');
        }
        if (runtimeYear != 0) {
            sb.append("LZ").append(runtimeYear);
        }
        return sb.toString();
    }

    public void addClasses(GraveClass classes) {
        this.classes.add(classes);
    }

    public boolean isEmpty() {
        return deceased == null || deceased.trim().isEmpty();
    }

    public boolean isRef() {
        return row.equals(graveSite.getRow()) && place.equals(graveSite.getPlace());
    }

    public String getId() {
        return id;
    }

    public String getReference() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append("Gabstätte: ").append(getGraveSite().getId());
        sb.append(";===================");
        if (!getGraveSite().getCriterias().isEmpty()) {
            sb.append(";");
            for (String crit : getGraveSite().getCriterias()) {
                sb.append(crit).append(", ");
            }
            sb.setLength(sb.length() - 2);
        }
        if (getGraveSite().getValidTo() != null) {
            sb.append(";Nutzungsrecht bis ").append(dateFormat.format(getGraveSite().getValidTo()));
        }
        sb.append(";").append(getGraveSite().getName());
        sb.append(";;Nutzungsberechtigter:");
        sb.append(";-----------------------------------;");
        Owner ow = getGraveSite().getOwner();
        if (ow != null) {
            sb.append(ow.getFirstName()).append(' ').append(ow.getLastName()).append(';');
            sb.append(ow.getStreet()).append(';');
            sb.append(ow.getZipAndTown());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(graveSite.getField());
        sb.append('/').append(row);
        sb.append('/').append(place);
        sb.append(' ').append(deceased);
        sb.append(" ref=").append(graveSite);
        return sb.toString();
    }

    public int getRuntimeYear() {
        return runtimeYear;
    }

    public void setRuntimeYear(int runtimeYear) {
        this.runtimeYear = runtimeYear;
    }
}
