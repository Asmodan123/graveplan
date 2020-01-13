package de.hannisoft.gaveplan.properties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import de.hannisoft.gaveplan.model.Point;

public class PointsReader {
    private final double xFactor;
    private final double yFactor;
    private final int xDelta;
    private final int yDelta;
    private double rotation;

    public PointsReader(double xFactor, double yFactor, int xDelta, int yDelta, double rotation) {
        this.xFactor = xFactor;
        this.yFactor = yFactor;
        this.xDelta = xDelta;
        this.yDelta = yDelta;
        this.rotation = rotation;
    }

    public Map<Integer, Point> readPoints() throws IOException {
        Map<Integer, Point> points = new HashMap<>();
        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("plan/points.properties"));
        for (Entry<Object, Object> prop : props.entrySet()) {
            try {
                int pointId = Integer.parseInt(prop.getKey().toString());
                String[] coords = prop.getValue().toString().split("\\,");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                Point point = new Point(pointId, x, y);
                point.scale(xFactor, yFactor);
                point.move(xDelta, yDelta);
                point.rotate(rotation);
                points.put(pointId, point);
                // System.out.println(point.toString());
                if (minX == 0 || point.getX() < minX) {
                    minX = point.getX();
                }
                if (maxX == 0 || point.getX() > maxX) {
                    maxX = point.getX();
                }
                if (minY == 0 || point.getY() < minY) {
                    minY = point.getY();
                }
                if (maxY == 0 || point.getY() > maxY) {
                    maxY = point.getY();
                }
            } catch (Exception e) {
                System.err.println("Can't convert Property to Point of '" + prop.toString() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("x:[" + minX + " " + maxX + "]");
        System.out.println("y:[" + minY + " " + maxY + "]");
        return points;
    }
}
