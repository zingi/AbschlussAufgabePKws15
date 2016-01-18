package spiel;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

/**
 * Created by aaronzingerle on 15.01.16.
 */
public class Map
{
    private MapLoader mapLoader;
    private Territory territories[];
    private Continent continents[];
    private AnchorPane pane;

    public Map(String dateiPfad, AnchorPane pane)
    {
        this.pane = pane;
        mapLoader = new MapLoader(dateiPfad);
        mapLoader.execute();

        territories = mapLoader.getTerritories();
        continents = mapLoader.getContinents(territories);

        drawMap();
    }

    public Territory[] getAllTerritories()
    {
        return territories;
    }

    public Continent[] getAllContinents()
    {
        return continents;
    }

    public Territory getTerritoryByName(String territoryName)
    {
        for (int i=0; i<territories.length; i++)
        {
            if (territories[i].getName().equals(territoryName)) return territories[i];
        }
        return null;
    }

    public Continent getContinentByName(String continentName)
    {
        for (int i=0; i<continents.length; i++)
        {
            if (continents[i].getName().equals(continentName)) return continents[i];
        }
        return null;
    }

    public int getContinentBonusByContinentName(String continentName)
    {
        Continent c = getContinentByName(continentName);
        return c.getBonus();
    }

    public int getRecruitment(int user)     // 1 Für Pc und 2 Für Spieler
    {
            int recruits = 0;
            int curr;

            for (Territory t : territories)
            {
                curr = t.getOwnership();
                if (curr == user) { recruits++; }
            }
            recruits /= 3;

            for(Continent c:continents)
            {
                recruits  += c.Bonus(user);
            }
            return recruits;
    }

    public void drawMap()
    {
        // http://www.colourlovers.com/palette/3372733/Raspberry_Wine_RC
        Rectangle background = new Rectangle(0,0,1250,650);
        background.setFill(Color.rgb(89,38,69));
        pane.getChildren().add(background);

        drawCapitalLines(Color.rgb(240,240,240), true);

        for (Territory t: territories)  // only for underlay styling
        {
            Polygon[] dummiePolygons = t.getDummiePolygons(Color.rgb(225,103,114), Color.rgb(225,103,114), 2.0, StrokeType.CENTERED);
            for (Polygon p: dummiePolygons)
            {
                pane.getChildren().add(p);
            }
        }

        for (Territory t: territories)
        {
            t.setMap(this);
            t.setPolygonsColor(225,103,114);
            t.setPolygonsStrokeColor(89,38,69);
            for (Polygon p: t.getPolygons())
            {
                pane.getChildren().add(p);
            }
        }
    }

    public void drawCapitalLines(Color lineColor, boolean worldMode)
    {
        ArrayList<Line> linesList   = new ArrayList<Line>();
        double windowWidth          = pane.getWidth();

        for (Territory t: territories)
        {
            int[] capitalT = t.getCapital();
            for (Territory n: t.getNeighbors())
            {
                int[] capitalN = n.getCapital();
                int tX = capitalT[0];
                int tY = capitalT[1];
                int nX = capitalN[0];
                int nY = capitalN[1];

                if (worldMode)
                {
                    // worldMode:   if X of both points is not (windowWith/4) farther away from the next windowWall
                    //              the line will be created as like the windowWalls would be connected

                    if ((tX < windowWidth / 4 && nX > (windowWidth / 4) * 3) || (nX < windowWidth / 4 && tX > (windowWidth / 4) * 3))
                    {
                        Line l1;
                        Line l2;
                        int lineYshadow = (tY > nY ? tY - nY : nY - tY);
                        int lineXshadow = 0;
                        lineXshadow += (tX < windowWidth / 4 ? tX : windowWidth - tX);
                        lineXshadow += (nX < windowWidth / 4 ? nX : windowWidth - nX);

                        if (tX < windowWidth / 4)
                        {
                            l1 = new Line(tX, tY, tX - lineXshadow, tY + (nY < tY ? -lineYshadow : lineYshadow));
                            l2 = new Line(nX, nY, nX + lineXshadow, nY + (nY < tY ? lineYshadow : -lineYshadow));
                        } else
                        {
                            l1 = new Line(tX, tY, tX + lineXshadow, tY + (nY > tY ? lineYshadow : -lineYshadow));
                            l2 = new Line(nX, nY, nX - lineXshadow, nY + (nY > tY ? -lineYshadow : lineYshadow));
                        }
                        l1.setStroke(lineColor);
                        l2.setStroke(lineColor);
                        linesList.add(l1);
                        linesList.add(l2);
                    }
                    else
                    {
                        Line l = new Line(tX, tY, nX, nY);
                        l.setStroke(lineColor);
                        linesList.add(l);
                    }
                }
                else
                {
                    Line l = new Line(tX, tY, nX, nY);
                    l.setStroke(lineColor);
                    linesList.add(l);
                }
            }
        }
        for (Line l: linesList)
        {
            pane.getChildren().add(l);
        }
    }

    public void territoryMouseEnteredEvent(String territoryName)
    {
        Territory territory = getTerritoryByName(territoryName);
        territory.setPolygonsStrokeColor(240,240,240);

        System.out.println(territoryName);
    }

    public void territoryMouseExitedEvent(String territoryName)
    {
        Territory territory = getTerritoryByName(territoryName);
        territory.setPolygonsStrokeColor(89,38,69);
    }

    public void territoryMouseClickedEvent(String territoryName)
    {
        Territory territory = getTerritoryByName(territoryName);
        territory.setPolygonsColor(180,219,173);
    }
}
