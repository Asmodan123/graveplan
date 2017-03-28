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
    private final Graphics2D img;
    private final BufferedImage bi;
    // private static final double xFactor = 2;
    // private static final double yFactor = 2;
    // private static final int xDelta = -550;
    // private static final int yDelta = 0;
    // private static double rotation = Math.PI / -2;
    private static final double xFactor = 1;
    private static final double yFactor = 1;
    private static final int xDelta = 0;
    private static final int yDelta = 0;
    private static double rotation = 0;

    public PNGPlaceMapWriter() throws IOException {
        initElements();

        int width = 750, height = 650;
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        img = bi.createGraphics();
    }

    public void drawDefaultMap() throws IOException {
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
        switch (element.getType()) {
            case RASTER:
                break;
            default:
                img.drawPolygon(element.getXs(), element.getYs(), element.getPoints().size());
        }
    }

    //@formatter:off
    /*
    private void drawRaster(PlanElement element){
//        $t_col=$this->getColor(0,0,0);
        int rowCount = element.getMaxRow()-element.getMinRow()+1;
        int placeCount = element.getMaxPlace()-element.getMinPlace()+1;
        double deltaX_Bottom=(points[4]-points[2]) /  rowCount;
        $DeltaX_Top=($pPoints[6]-$pPoints[0]) /  $AnzReihe;
        $DeltaX_Left=($pPoints[2]-$pPoints[0]) / $AnzPlatz;
        $DeltaX_Right=($pPoints[4]-$pPoints[6]) / $AnzPlatz;
        $DeltaY_Left=($pPoints[3]-$pPoints[1]) / $AnzPlatz;
        $DeltaY_Right=($pPoints[5]-$pPoints[7]) / $AnzPlatz;
        $DeltaY_Top=($pPoints[7]-$pPoints[1]) /  $AnzReihe;
        $DeltaY_Bottom=($pPoints[5]-$pPoints[3]) /  $AnzReihe;


        ImageSetThickness($this->im, 1);
        //echo $AnzReihe." - ".$AnzPlatz." - ".$DeltaY_Left." - ".$DeltaY_Right.' - '.$AnzPlaz.'<br>';
        for ($i=0; $i<$AnzReihe+1; $i++)
            imageline($this->im, $pPoints[0]+$DeltaX_Top*$i,
                                 $pPoints[1]+$DeltaY_Top*$i,
                                 $pPoints[2]+$DeltaX_Bottom*$i,
                                 $pPoints[3]+$DeltaY_Bottom*$i,
                                 $t_col  );

        for ($i=0; $i<$AnzPlatz+1; $i++) {
            //echo ($pPoints[0]+$DeltaX_Left*$i).' - '.($pPoints[1]+$DeltaY_Left*$i).' - '.($pPoints[2]+$DeltaX_Right*$i).' - '.($pPoints[3]+$DeltaY_Right*$i).'<br>';
            imageline($this->im, $pPoints[0]+$DeltaX_Left*$i,
                                 $pPoints[1]+$DeltaY_Left*$i,
                                 $pPoints[6]+$DeltaX_Right*$i,
                                 $pPoints[7]+$DeltaY_Right*$i,
                                 $t_col  );
        };
*/
  //@formatter:on

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
