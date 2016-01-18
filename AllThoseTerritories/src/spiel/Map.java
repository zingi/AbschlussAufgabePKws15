package spiel;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

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

    public int getRecruitment(int user)             // 1 Für Pc und 2 Für Spieler
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

        for (Territory t: territories)
        {
            t.setMap(this);
            for (Polygon p: t.getPolygons())
            {
                p.setFill(Color.rgb(225,103,114));
                p.setStroke(Color.rgb(225,103,114));
                pane.getChildren().add(p);
            }
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
        territory.setPolygonsStrokeColor(225,103,114);
    }
}
