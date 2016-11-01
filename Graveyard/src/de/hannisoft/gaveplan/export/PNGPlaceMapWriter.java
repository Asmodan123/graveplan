package de.hannisoft.gaveplan.export;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;

import de.hannisoft.gaveplan.model.PlanElement;
import de.hannisoft.gaveplan.model.Point;

public class PNGPlaceMapWriter {
    private Map<Integer, PlanElement> elements = null;
    private Graphics2D img;
    private static final double xFactor = 1;
    private static final double yFactor = 2;
    private static final int xDelta = 0;
    private static final int yDelta = 800;
    // private static final double rotation = 0.085;
    // private static final double rotation = 0.545;
    private static final double rotation = -0.3;

    public PNGPlaceMapWriter() throws IOException {
        initElements();

        int width = 2000, height = 1000;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        img = bi.createGraphics();

        for (PlanElement element : elements.values()) {
            drawElement(element);
        }
        // drawElement(elements.get(1));

        ImageIO.write(bi, "PNG", new File("/home/johannes/tmp/plan/Plan.PNG"));
    }

    private void initElements() throws IOException {
        elements = readElements();
        readElementPoints();

    }

    public void drawElement(PlanElement element) {
        prepareImage(element);
        img.drawPolygon(element.getXs(), element.getYs(), element.getPoints().size());
    }

    private void prepareImage(PlanElement element) {
        switch (element.getType()) {
            case RAND:
                img.setStroke(new BasicStroke(4));
                img.setColor(Color.red);
                break;
            case FELD:
                img.setStroke(new BasicStroke(2));
                img.setColor(Color.green);
                break;
            default:
                break;
        }
    }

    private void readElementPoints() throws IOException {
        Map<Integer, Point> points = readPoints();

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

    private Map<Integer, Point> readPoints() throws IOException {
        Map<Integer, Point> points = new HashMap<>();
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
            } catch (Exception e) {
                System.err.println("Can't convert Property to Point of '" + prop.toString() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        return points;
    }

    private Map<Integer, PlanElement> readElements() throws IOException {
        Map<Integer, PlanElement> elements = new HashMap<>();
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("plan/elements.properties"));
        for (Entry<Object, Object> prop : props.entrySet()) {
            try {
                int elementId = Integer.parseInt(prop.getKey().toString());
                String[] values = (prop.getValue().toString()).split("\\,");
                String type = values[0];
                PlanElement pEelement = new PlanElement(elementId, type);
                pEelement.setMinRow(Integer.parseInt(values[1]));
                pEelement.setMaxRow(Integer.parseInt(values[2]));
                pEelement.setMinPlace(Integer.parseInt(values[3]));
                pEelement.setMaxPlace(Integer.parseInt(values[4]));
                if (values.length > 5) {
                    pEelement.setName(values[5]);
                }

                elements.put(elementId, pEelement);
            } catch (Exception e) {
                System.err.println("Can't read ElementProperty: '" + prop.toString() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        return elements;
    }

}
