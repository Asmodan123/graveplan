package de.hannisoft.graveplan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanElement {
    public enum DeltaType {
        X, Y
    };

    public enum EdgeType {
        TOP, LEFT, BOTTOM, RIGHT
    };

    public enum CornerPointType {
        TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT
    };

    private final int id;
    private final ElementType type;
    private int minRow = 0;
    private int maxRow = 0;
    private int minPlace = 0;
    private int maxPlace = 0;
    private String name = null;
    private String title = null;
    private ArrayList<Point> points = new ArrayList<>();
    private Map<CornerPointType, Point> cornerPoints = new HashMap<>();

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

    public String getTitle() {
		return title;
	}
    
    public void setTitle(String title) {
		this.title = title;
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

    public Point getCornerPoint(CornerPointType cornerPoint) {
        if (cornerPoints.isEmpty() && !points.isEmpty()) {
            initCornerPoints();
        }
        return cornerPoints.get(cornerPoint);
    }

    private void initCornerPoints() {
        cornerPoints.clear();

        Collections.sort(points, horizontalSorter);
        Point p1 = points.get(0);
        Point p2 = points.get(1);
        cornerPoints.put(CornerPointType.BOTTOM_LEFT, p1.getY() > p2.getY() ? p1 : p2);
        cornerPoints.put(CornerPointType.TOP_LEFT, p1.getY() > p2.getY() ? p2 : p1);

        int size = points.size();
        p1 = points.get(size - 1);
        p2 = points.get(size - 2);
        cornerPoints.put(CornerPointType.BOTTOM_RIGHT, p1.getY() > p2.getY() ? p1 : p2);
        cornerPoints.put(CornerPointType.TOP_RIGHT, p1.getY() > p2.getY() ? p2 : p1);

        System.out.println("TOP_LEFT: " + cornerPoints.get(CornerPointType.TOP_LEFT));
        System.out.println("BOTTOM_LEFT: " + cornerPoints.get(CornerPointType.BOTTOM_LEFT));
        System.out.println("TOP_RIGHT: " + cornerPoints.get(CornerPointType.TOP_RIGHT));
        System.out.println("BOTTOM_RIGHT: " + cornerPoints.get(CornerPointType.BOTTOM_RIGHT));
    }

    private Comparator<Point> horizontalSorter = new Comparator<Point>() {
        @Override
        public int compare(Point p1, Point p2) {
            return p1.getX() - p2.getX();
        }
    };

    public float getDelta(DeltaType deltaType, EdgeType edge) {
        CornerPointType cp1 = null, cp2 = null;
        switch (edge) {
            case TOP:
                cp1 = CornerPointType.TOP_RIGHT;
                cp2 = CornerPointType.TOP_LEFT;
                break;
            case BOTTOM:
                cp1 = CornerPointType.BOTTOM_RIGHT;
                cp2 = CornerPointType.BOTTOM_LEFT;
                break;
            case LEFT:
                cp1 = CornerPointType.BOTTOM_LEFT;
                cp2 = CornerPointType.TOP_LEFT;
                break;
            case RIGHT:
                cp1 = CornerPointType.BOTTOM_RIGHT;
                cp2 = CornerPointType.TOP_RIGHT;
                break;
        }
        Point p1 = getCornerPoint(cp1);
        Point p2 = getCornerPoint(cp2);
        float devisor = 1;
        float delta = 0;
        if (deltaType == DeltaType.X) {
            delta = p1.getX() - p2.getX();
            devisor = getPlaceCount();
        } else {
            delta = p1.getY() - p2.getY();
            devisor = getRowCount();
        }
        return delta / devisor;
    }

    public int getPlaceCount() {
        return getMaxPlace() - getMinPlace() + 1;
    }

    public int getRowCount() {
        return getMaxRow() - getMinRow() + 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType()).append(" ").append(getName()).append(" [").append(minRow).append(", ").append(maxRow).append("] / [")
                .append(minPlace).append(", ").append(maxPlace).append("]");
        return sb.toString();
    }

	public String getLabel() {
        String label = getName();
        if (getTitle() != null) {
        	label += " " +getTitle();
        }
		return label;
	}
}
