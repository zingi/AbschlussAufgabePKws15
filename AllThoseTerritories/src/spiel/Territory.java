package spiel;

import javafx.scene.shape.Polygon;

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

    Polygon[] polygons;

    public Territory(String name, int capitalX, int capitalY, int[][] patches)
    {
        capital         = new int[2];
        capital[0]      = capitalX;
        capital[1]      = capitalY;

        this.name       = name;
        this.patches    = patches;
    }

    public void setNeighbors(Territory[] neighbors)
    {
        this.neighbors = neighbors;
    }

    public Territory[] getNeighbors()   { return neighbors; }
    public String getName()             { return name; }
    public int[][] getPatches()         { return patches; }
    public int getOwnership()           { return ownership; }

    //  TODO: create polygonBuilder+EventHandler, write getterMethod

}
