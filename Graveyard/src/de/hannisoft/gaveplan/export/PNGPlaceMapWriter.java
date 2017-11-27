package de.hannisoft.gaveplan.export;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import de.hannisoft.gaveplan.model.PlanElement;
import de.hannisoft.gaveplan.model.PlanElement.CornerPointType;
import de.hannisoft.gaveplan.model.PlanElement.DeltaType;
import de.hannisoft.gaveplan.model.PlanElement.EdgeType;
import de.hannisoft.gaveplan.model.Point;
import de.hannisoft.gaveplan.properties.ElementPointsReader;
import de.hannisoft.gaveplan.properties.ElementsReader;
import de.hannisoft.gaveplan.properties.PointsReader;

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

    private ElementsReader elementsReader = new ElementsReader();
    private PointsReader pointsReader = new PointsReader(xFactor, yFactor, xDelta, yDelta, rotation);

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
        elements = elementsReader.readElements();
        Map<Integer, Point> points = pointsReader.readPoints();
        ElementPointsReader elementPointsReader = new ElementPointsReader();
        elementPointsReader.readElementPoints(elements, points);

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

}
