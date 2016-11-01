package de.hannisoft.gaveplan.model;

import java.util.LinkedList;
import java.util.List;

public class PlanElement {
    private final int id;
    private final ElementType type;
    private int minRow = 0;
    private int maxRow = 0;
    private int minPlace = 0;
    private int maxPlace = 0;
    private String name = null;
    private List<Point> points = new LinkedList<>();

    public PlanElement(int id, String elementType) {
        this.id = id;
        this.type = ElementType.getTypeByName(elementType);
    }

    public int getId() {
        return id;
    }

    public int getMinRow() {
        return minRow;
    }

    public void setMinRow(int minRow) {
        this.minRow = minRow;
    }

    public int getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public int getMinPlace() {
        return minPlace;
    }

    public void setMinPlace(int minPlace) {
        this.minPlace = minPlace;
    }

    public int getMaxPlace() {
        return maxPlace;
    }

    public void setMaxPlace(int maxPlace) {
        this.maxPlace = maxPlace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Point> getPoints() {
        return points;
    }

    public int[] getXs() {
        int[] xs = new int[points.size()];
        for (int i = 0; i < xs.length; i++) {
            xs[i] = points.get(i).getX();
        }
        return xs;
    }

    public int[] getYs() {
        int[] ys = new int[points.size()];
        for (int i = 0; i < ys.length; i++) {
            ys[i] = points.get(i).getY();
        }
        return ys;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public ElementType getType() {
        return type;
    }
}
