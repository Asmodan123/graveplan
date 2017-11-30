package de.hannisoft.gaveplan.model;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GraveMap {
    private final String field;
    private Set<GraveSite> graveSites = new HashSet<>();

    private int rowCount = -1;
    private int placeCount = -1;
    private int deltaRow = 0;
    private int deltaPlace = 0;
    private boolean finished = false;

    private Grave[][] graveArray = null;

    public GraveMap(String field) {
        this.field = field;
    }

    public void finishEdit(Map<String, PlanElement> fieldElements) {
        if (finished) {
            return;
        }
        fillupMissingGraves();
        initMinMaxValues(fieldElements);
        fillGraveArray();
        setGraveClasses();
        finished = true;
    }

    private void fillupMissingGraves() {
        for (GraveSite grave : graveSites) {
            HashSet<String> places = new HashSet<>();
            for (Grave plc : grave.getGraves()) {
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
                            grave.getGraves().add(new Grave(grave, rowStr, placeStr));
                        }
                    } catch (Exception e) {
                        System.err.println(e.getClass().getSimpleName() + " while filling missing places of " + grave.toString()
                                + " into GraveMap: " + e.getMessage());
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
            System.out.println("Found FieldElement of GraveMap " + field + " " + fe.toString());
            rowCount = fe.getMaxRow() - fe.getMinRow() + (fe.getMinRow() < 0 ? 0 : 1);
            placeCount = fe.getMaxPlace() - fe.getMinPlace() + (fe.getMinPlace() < 0 ? 0 : 1);
            deltaRow = fe.getMinRow() < 0 ? Math.abs(fe.getMinRow()) : 0;
            deltaPlace = fe.getMinPlace() < 0 ? Math.abs(fe.getMinPlace()) : 0;
            System.out.println("Initialized MinMaxValues of GraveMap " + field + ": rowCount=" + rowCount + " / placeCount="
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
        for (GraveSite graveSite : graveSites) {
            for (Grave grave : graveSite.getGraves()) {
                int row = grave.getRowInt();
                if (row < minRow) {
                    minRow = row;
                }
                if (row > maxRow) {
                    maxRow = row;
                }

                int plc = grave.getPlaceInt();
                if (plc < minPlace) {
                    minPlace = plc;
                }
                if (plc > maxPlace) {
                    maxPlace = plc;
                }
            }
        }
        System.out.println("Found MinMaxValues of GraveMap " + field + ": Rows=[" + minRow + ", " + maxRow + "] Grave=["
                + minPlace + ", " + maxPlace + "]");
        rowCount = maxRow - minRow + (minRow < 0 ? 0 : 1);
        placeCount = maxPlace - minPlace + (minPlace < 0 ? 0 : 1);
        deltaRow = minRow < 0 ? Math.abs(minRow) : 0;
        deltaPlace = minPlace < 0 ? Math.abs(minPlace) : 0;
        System.out.println("Initialized MinMaxValues of GraveMap " + field + ": rowCount=" + rowCount + " / placeCount="
                + placeCount + " / " + deltaRow + " / deltaPlace=" + deltaPlace);
    }

    private void fillGraveArray() {
        graveArray = new Grave[rowCount][placeCount];
        for (GraveSite graveSite : graveSites) {
            for (Grave grave : graveSite.getGraves()) {
                try {
                    int row = deltaRow + grave.getRowInt() - (grave.getRowInt() > 0 ? 1 : 0);
                    int plc = deltaPlace + grave.getPlaceInt() - (grave.getPlaceInt() > 0 ? 1 : 0);
                    graveArray[row][plc] = grave;
                } catch (Exception e) {
                    System.err.println(e.getClass().getSimpleName() + " while filling Grave " + grave.toString()
                            + " into GraveMap: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void setGraveClasses() {
        for (int i = 0; i < graveArray.length; i++) {
            Grave[] graveRow = graveArray[i];
            for (int j = 0; j < graveRow.length; j++) {
                Grave grave = null;
                try {
                    grave = graveRow[j];
                    if (grave == null) {
                        continue;
                    }
                    GraveSite graveSite = grave.getGraveSite();
                    boolean isBroached = graveSite.isBroached();
                    if (isBroached) {
                        grave.addClasses(GraveClass.BROACHED);
                    } else if (!grave.isEmpty()) {
                        grave.addClasses(GraveClass.BUSY);
                    }

                    if (graveSite.isStele()) {
                        grave.addClasses(GraveClass.STELE);
                    }

                    if (grave.isRef()) {
                        grave.addClasses(GraveClass.REF);
                    }

                    if (!hasNeighborGraveInEqualGraveSite(i, j + 1, graveSite)) {
                        grave.addClasses(GraveClass.W);
                    }

                    if (!hasNeighborGraveInEqualGraveSite(i, j - 1, graveSite)) {
                        grave.addClasses(GraveClass.O);
                    }

                    if (!hasNeighborGraveInEqualGraveSite(i + 1, j, graveSite)) {
                        grave.addClasses(GraveClass.N);
                    }

                    if (!hasNeighborGraveInEqualGraveSite(i - 1, j, graveSite)) {
                        grave.addClasses(GraveClass.S);
                    }

                    if (!isBroached && grave.getDeceased() == null) {
                        grave.addClasses(GraveClass.FREE);
                    }

                    addRuntime(grave);
                } catch (Exception e) {
                    System.err.println(e.getClass().getSimpleName() + " while initialising Grave's classes of " + grave + ": "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void addRuntime(Grave grave) {
        if (grave != null && grave.getGraveSite() != null && grave.getGraveSite().getValidFrom() != null) {
            Calendar validTo = Calendar.getInstance(Locale.GERMANY);
            validTo.setTime(grave.getGraveSite().getValidTo());
            Calendar now = Calendar.getInstance(Locale.GERMANY);
            int diff = validTo.get(Calendar.YEAR) - now.get(Calendar.YEAR);
            grave.setRuntimeYear(diff);
        }
    }

    private boolean hasNeighborGraveInEqualGraveSite(int row, int place, GraveSite graveSite) {
        Grave neighbor = null;
        if (row >= 0 && row < rowCount && place >= 0 && place < placeCount) {
            neighbor = graveArray[row][place];
        }

        return neighbor != null && neighbor.getGraveSite() == graveSite;
    }

    public void addGraveSite(GraveSite graveSite) {
        graveSites.add(graveSite);
    }

    public Set<GraveSite> graveSites() {
        return graveSites;
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

    public Grave getGrave(int row, int place) {
        return graveArray[row][place];
    }
}
