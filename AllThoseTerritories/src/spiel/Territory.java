package spiel;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

/**
 * Created by aaronzingerle on 15.01.16.
 */
public class Territory
{
    private String name;
    private int[] capital;
    private Territory[] neighbors;
    private int[][] patches;
    private int ownership = 0;  //  0 = noch nicht vergeben, 1 = gehoert Computer, 2 = gehoert Spieler;
    private Map map;

    Polygon[] polygons;

    public Territory(String name, int capitalX, int capitalY, int[][] patches)
    {
        capital         = new int[2];
        capital[0]      = capitalX;
        capital[1]      = capitalY;

        this.name       = name;
        this.patches    = patches;

        createPolygons();
    }

    public void setNeighbors(Territory[] neighbors)
    {
        this.neighbors = neighbors;
    }
    public void setMap(Map map) { this.map = map; }

    public Territory[] getNeighbors()   { return neighbors; }
    public String getName()             { return name; }
    public int[][] getPatches()         { return patches; }
    public int getOwnership()           { return ownership; }
    public int[] getCapital()           { return capital; }
    public Polygon[] getPolygons()      { return polygons; }

    public void createPolygons()
    {
        polygons            = new Polygon[patches.length];
        int polygonsIndex   = 0;

        for (int[] patch: patches)
        {
            Double[] doublePatchArray = new Double[patch.length];
            for(int i=0; i<patch.length; i++)
            {
                doublePatchArray[i] = new Double(patch[i]);
            }
            Polygon p = new Polygon();
            p.getPoints().setAll(doublePatchArray);

            // Polygon Initial Styling
            p.setStrokeType(StrokeType.INSIDE);
            p.setStrokeWidth(1.5);

            // add Polygon to local Polygons List
            polygons[polygonsIndex] = p;
            polygonsIndex++;
        }

        addPolygonHandlers();
    }

    public void addPolygonHandlers()
    {
        for (Polygon p: polygons)
        {
            p.setOnMouseEntered(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    map.territoryMouseEnteredEvent(name);
                }
            });
            p.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event)
                {
                    map.territoryMouseExitedEvent(name);
                }
            });
            p.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { map.territoryMouseClickedEvent(name); }
            });
        }
    }

    public void setPolygonsColor(int red, int green, int blue)
    {
        for (Polygon p: polygons)
        {
            p.setFill(Color.rgb(red, green, blue));
        }
    }
    public void setPolygonsStrokeColor(int red, int green, int blue)
    {
        for (Polygon p: polygons)
        {
            p.setStroke(Color.rgb(red, green, blue));
        }
    }

    // returns Polygons of Territory without handling - only for underlay styling
    public Polygon[] getDummiePolygons(Color polygonColor, Color polygonStrokeColor, double strokeWidth, StrokeType strokeType)
    {
        Polygon[] dummiePolygons    = new Polygon[patches.length];
        int polygonsIndex           = 0;

        for (int[] patch: patches)
        {
            Double[] doublePatchArray = new Double[patch.length];
            for(int i=0; i<patch.length; i++)
            {
                doublePatchArray[i] = new Double(patch[i]);
            }
            Polygon p = new Polygon();
            p.getPoints().setAll(doublePatchArray);

            // Polygon Initial Styling
            p.setFill(polygonColor);
            p.setStroke(polygonStrokeColor);
            p.setStrokeType(strokeType);
            p.setStrokeWidth(strokeWidth);

            // add Polygon to Dummie-Polygons List
            dummiePolygons[polygonsIndex] = p;
            polygonsIndex++;
        }

        return  dummiePolygons;
    }
}
