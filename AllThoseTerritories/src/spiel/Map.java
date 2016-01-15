package spiel;

import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;

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

    public int getRecruitment(int User)             // 1 Für Pc und 2 Für Spieler
    {

            int recruits = 0;
            int curr;

            for (Territory t : territories) {
                curr = t.getOwnership();
                if (curr == User) {
                    recruits++;
                }


            }
            recruits /= 3;

            for(Continent c:continents)
            {
                recruits  += c.Bonus(User);

            }
            return recruits;

    }

    public void drawMap()
    {
        System.out.println(pane);
        Polygon polygon = new Polygon(10,110,120,130,200,10);
        pane.getChildren().add(polygon);
        // TODO: implement
    }



}
