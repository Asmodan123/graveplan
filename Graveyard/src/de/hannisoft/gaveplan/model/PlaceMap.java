package de.hannisoft.gaveplan.model;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PlaceMap {
    private final String field;
    private Set<Grave> graves = new HashSet<>();

    private int rowCount = -1;
    private int placeCount = -1;
    private int deltaRow = 0;
    private int deltaPlace = 0;

    private Place[][] placeArray = null;

    public PlaceMap(String field) {
        this.field = field;
    }

    public void finishEdit(Map<String, PlanElement> fieldElements) {
        fillupMissingPlaces();
        initMinMaxValues(fieldElements);
        fillPlaceArray();
        setPlaceClasses();
    }

    private void fillupMissingPlaces() {
        for (Grave grave : graves) {
            HashSet<String> places = new HashSet<>();
            for (Place plc : grave.getPlaces()) {
                places.add(plc.getId());
            }
            for (int i = 0; i < grave.getRowSize(); i++) {
                for (int j = 0; j < grave.getPlaceSize(); j++) {
                    try {
                        int row = (grave.getRowInt() + i);
                        String rowStr = String.format("%02d", row);
                        int plc = (grave.getPlaceInt() + j);
                        String placeStr = String.format("%02d", plc);
                        if (!places.contains(rowStr + "/" + placeStr)) {
                            grave.getPlaces().add(new Place(grave, rowStr, placeStr));
                        }
                    } catch (Exception e) {
                        System.err.println(e.getClass().getSimpleName() + " while filling missing places of " + grave.toString()
                                + " into PlaceMap: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private void initMinMaxValues(Map<String, PlanElement> fieldElements) {
        PlanElement fe = fieldElements.get(field);
        if (fe == null) {
            initMinMaxValuesFromGaves();
        } else {
            System.out.println("Found FieldElement of PlaceMap " + field + " " + fe.toString());
            rowCount = fe.getMaxRow() - fe.getMinRow() + (fe.getMinRow() < 0 ? 0 : 1);
            placeCount = fe.getMaxPlace() - fe.getMinPlace() + (fe.getMinPlace() < 0 ? 0 : 1);
            deltaRow = fe.getMinRow() < 0 ? Math.abs(fe.getMinRow()) : 0;
            deltaPlace = fe.getMinPlace() < 0 ? Math.abs(fe.getMinPlace()) : 0;
            System.out.println("Initialized MinMaxValues of PlaceMap " + field + ": rowCount=" + rowCount + " / placeCount="
                    + placeCount + " / " + deltaRow + " / deltaPlace=" + deltaPlace);// TODO Auto-generated method stub
            // initMinMaxValuesFromGaves();
            System.out.println("");
        }

    }

    private void initMinMaxValuesFromGaves() {
        int minRow = 1;
        int maxRow = 1;
        int minPlace = 1;
        int maxPlace = 1;
        for (Grave grave : graves) {
            for (Place place : grave.getPlaces()) {
                int row = place.getRowInt();
                if (row < minRow) {
                    minRow = row;
                }
                if (row > maxRow) {
                    maxRow = row;
                }

                int plc = place.getPlaceInt();
                if (plc < minPlace) {
                    minPlace = plc;
                }
                if (plc > maxPlace) {
                    maxPlace = plc;
                }
            }
        }
        System.out.println("Found MinMaxValues of PlaceMap " + field + ": Rows=[" + minRow + ", " + maxRow + "] Place=["
                + minPlace + ", " + maxPlace + "]");
        rowCount = maxRow - minRow + (minRow < 0 ? 0 : 1);
        placeCount = maxPlace - minPlace + (minPlace < 0 ? 0 : 1);
        deltaRow = minRow < 0 ? Math.abs(minRow) : 0;
        deltaPlace = minPlace < 0 ? Math.abs(minPlace) : 0;
        System.out.println("Initialized MinMaxValues of PlaceMap " + field + ": rowCount=" + rowCount + " / placeCount="
                + placeCount + " / " + deltaRow + " / deltaPlace=" + deltaPlace);
    }

    private void fillPlaceArray() {
        placeArray = new Place[rowCount][placeCount];
        for (Grave grave : graves) {
            for (Place place : grave.getPlaces()) {
                try {
                    int row = deltaRow + place.getRowInt() - (place.getRowInt() > 0 ? 1 : 0);
                    int plc = deltaPlace + place.getPlaceInt() - (place.getPlaceInt() > 0 ? 1 : 0);
                    placeArray[row][plc] = place;
                } catch (Exception e) {
                    System.err.println(e.getClass().getSimpleName() + " while filling Place " + place.toString()
                            + " into PlaceMap: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void setPlaceClasses() {
        for (int i = 0; i < placeArray.length; i++) {
            Place[] placeRow = placeArray[i];
            for (int j = 0; j < placeRow.length; j++) {
                Place place = null;
                try {
                    place = placeRow[j];
                    if (place == null) {
                        continue;
                    }
                    Grave grave = place.getGrave();
                    boolean isBroached = grave.isBroached();
                    if (isBroached) {
                        place.addClasses(PlaceClass.BROACHED);
                    } else if (!place.isEmpty()) {
                        place.addClasses(PlaceClass.BUSY);
                    }

                    if (grave.isStele()) {
                        place.addClasses(PlaceClass.STELE);
                    }

                    if (place.isRef()) {
                        place.addClasses(PlaceClass.REF);
                    }

                    if (!hasNeighborPlaceEqualGrave(i, j + 1, grave)) {
                        place.addClasses(PlaceClass.W);
                    }

                    if (!hasNeighborPlaceEqualGrave(i, j - 1, grave)) {
                        place.addClasses(PlaceClass.O);
                    }

                    if (!hasNeighborPlaceEqualGrave(i + 1, j, grave)) {
                        place.addClasses(PlaceClass.N);
                    }

                    if (!hasNeighborPlaceEqualGrave(i - 1, j, grave)) {
                        place.addClasses(PlaceClass.S);
                    }

                    if (!isBroached && place.getDeceased() == null) {
                        place.addClasses(PlaceClass.FREE);
                    }

                    addRuntime(place);
                } catch (Exception e) {
                    System.err.println(e.getClass().getSimpleName() + " while initialising Place's classes of " + place + ": "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void addRuntime(Place place) {
        if (place != null && place.getGrave() != null && place.getGrave().getValidFrom() != null) {
            Calendar validTo = Calendar.getInstance(Locale.GERMANY);
            validTo.setTime(place.getGrave().getValidTo());
            Calendar now = Calendar.getInstance(Locale.GERMANY);
            int diff = validTo.get(Calendar.YEAR) - now.get(Calendar.YEAR);
            place.setRuntimeYear(diff);
        }
    }

    private boolean hasNeighborPlaceEqualGrave(int row, int place, Grave grave) {
        Place neighbor = null;
        if (row >= 0 && row < rowCount && place >= 0 && place < placeCount) {
            neighbor = placeArray[row][place];
        }

        return neighbor != null && neighbor.getGrave() == grave;
    }

    public void addGrave(Grave grave) {
        graves.add(grave);

    }

    public String getField() {
        return field;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getPlaceCount() {
        return placeCount;
    }

    public int getDeltaRow() {
        return deltaRow;
    }

    public int getDeltaPlace() {
        return deltaPlace;
    }

    public Place getPlace(int row, int place) {
        return placeArray[row][place];
    }
}
