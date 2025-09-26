package de.hannisoft.gaveplan.properties;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import de.hannisoft.gaveplan.model.PlanElement;
import de.hannisoft.gaveplan.model.Point;

public class ElementPointsReader {
    public void readElementPoints(Map<Integer, PlanElement> elements, Map<Integer, Point> points) throws IOException {
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("plan/elementPoints.properties"));
        for (Entry<Object, Object> prop : props.entrySet()) {
            try {
                int elementId = Integer.parseInt(prop.getKey().toString());
                PlanElement pElement = elements.get(elementId);
                if (pElement != null) {
                    String[] elementPoints = prop.getValue().toString().split("\\,");
                    for (String ePoint : elementPoints) {
                        int pointId = Integer.parseInt(ePoint);
                        Point point = points.get(pointId);
                        if (point != null) {
                            pElement.addPoint(point);
                        } else {
                            System.err.println("Pint with id " + pointId + " not found");
                        }
                    }
                } else {
                    System.err.println("Element with id " + elementId + " not found");
                }
            } catch (Exception e) {
                System.err.println("Can't read ElementPoints of '" + prop.toString() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }

    }
}
