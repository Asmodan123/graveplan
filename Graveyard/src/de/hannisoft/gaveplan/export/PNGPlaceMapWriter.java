package de.hannisoft.gaveplan.export;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
import de.hannisoft.gaveplan.model.PlanElement.CornerPointType;
import de.hannisoft.gaveplan.model.PlanElement.DeltaType;
import de.hannisoft.gaveplan.model.PlanElement.EdgeType;
import de.hannisoft.gaveplan.model.Point;

public class PNGPlaceMapWriter {
    private Map<Integer, PlanElement> elements = null;
    private final Graphics2D img;
    private final BufferedImage bi;
    // private static final double xFactor = 2;
    // private static final double yFactor = 2;
    // private static final int xDelta = -550;
    // private static final int yDelta = 0;
    // private static double rotation = Math.PI / -2;
    private static final double xFactor = 3;
    private static final double yFactor = 3;
    private static final int xDelta = 0;
    private static final int yDelta = 0;
    private static double rotation = 0;

    public PNGPlaceMapWriter() throws IOException {
        initElements();

        int width = 2000, height = 2000;
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        img = bi.createGraphics();
    }

    public void drawDefaultMap() throws IOException {
        for (PlanElement element : elements.values()) {
            drawElement(element);
            drawPoints(element);
        }
        // drawElement(elements.get(1));

        ImageIO.write(bi, "PNG", new File("/home/johannes/tmp/plan/Plan.PNG"));
    }

    private void drawPoints(PlanElement element) {
        img.setFont(new Font("Arial", Font.PLAIN, 20));
        img.setColor(Color.BLUE);
        for (Point p : element.getPoints()) {
            img.fillArc(p.getX(), p.getY(), 5, 5, 0, 360);
            img.drawString(p.toString(), p.getX(), p.getY());
        }
    }

    private void initElements() throws IOException {
        elements = readElements();
        readElementPoints();

    }

    public void drawElement(PlanElement element) {
        prepareImage(element);
        switch (element.getType()) {
            case FELD:
                drawFeld(element);
                break;
            case RASTER:
                drawRaster(element);
                break;
            default:
                img.drawPolygon(element.getXs(), element.getYs(), element.getPoints().size());
        }
    }

    private void drawFeld(PlanElement element) {
        img.drawPolygon(element.getXs(), element.getYs(), element.getPoints().size());
        Point refPoint = element.getCornerPoint(CornerPointType.BOTTOM_LEFT);
        if (refPoint != null) {
            img.setFont(new Font("Arial", Font.PLAIN, 20));

            int x = refPoint.getX() + 10;
            int y = refPoint.getY() - 10;
            img.drawString(element.getName(), x, y);
        }
    }

    private void drawRaster(PlanElement element) {
        img.setColor(Color.BLACK);
        img.setStroke(new BasicStroke(1));

        float deltaX_Bottom = element.getDelta(DeltaType.X, EdgeType.BOTTOM);
        float deltaX_Left = element.getDelta(DeltaType.X, EdgeType.LEFT);
        float deltaX_Right = element.getDelta(DeltaType.X, EdgeType.RIGHT);
        float deltaX_Top = element.getDelta(DeltaType.X, EdgeType.TOP);
        float deltaY_Bottom = element.getDelta(DeltaType.Y, EdgeType.BOTTOM);
        float deltaY_Left = element.getDelta(DeltaType.Y, EdgeType.LEFT);
        float deltaY_Right = element.getDelta(DeltaType.Y, EdgeType.RIGHT);
        float deltaY_Top = element.getDelta(DeltaType.Y, EdgeType.TOP);
        Point p1 = element.getCornerPoint(CornerPointType.TOP_LEFT);
        Point p2 = element.getCornerPoint(CornerPointType.BOTTOM_LEFT);
        int refx1 = p1.getX();
        int refy1 = p1.getY();
        int refx2 = p2.getX();
        int refy2 = p2.getY();
        for (int i = 0; i < element.getPlaceCount() + 1; i++) {
            int x1 = refx1 + Math.round(i * deltaX_Top);
            int y1 = refy1 + Math.round(i * deltaY_Top);
            int x2 = refx2 + Math.round(i * deltaX_Bottom);
            int y2 = refy2 + Math.round(i * deltaY_Bottom);
            img.drawLine(x1, y1, x2, y2);
        }

        p1 = element.getCornerPoint(CornerPointType.TOP_LEFT);
        p2 = element.getCornerPoint(CornerPointType.TOP_RIGHT);
        refx1 = p1.getX();
        refy1 = p1.getY();
        refx2 = p2.getX();
        refy2 = p2.getY();
        for (int i = 0; i <= element.getRowCount(); i++) {
            int x1 = refx1 + Math.round(i * deltaX_Left);
            int y1 = refy1 + Math.round(i * deltaY_Left);
            int x2 = refx2 + Math.round(i * deltaX_Right);
            int y2 = refy2 + Math.round(i * deltaY_Right);
            img.drawLine(x1, y1, x2, y2);
        }
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
