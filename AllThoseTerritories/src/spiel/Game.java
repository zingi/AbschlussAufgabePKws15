package spiel;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Random;

/**
 * Created by aaronzingerle on 23.01.16.
 */
public class Game
{
    private Stage primaryStage;
    private Map map;
    private int phase = 0;
    Random random = new Random();

    public Game(String dateiPfad, AnchorPane pane, Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        map = new Map(dateiPfad, pane, this);
    }

    public void territoryEvent(String territoryName, String event)
    {
        if (phase == 0)
        {
            phaseLanderwerb(territoryName, event);
        }
    }

    public void phaseLanderwerb(String territoryName, String event)
    {
        Territory t = map.getTerritoryByName(territoryName);
        if (event.equals("mouseLeftClicked"))
        {
            if (t.getOwnership() == 0)
            {
                map.setTerritoryOwnership(territoryName, 2);
                map.setArmeeBesetzungen(territoryName, 1);
                t.setPolygonsColor(180,219,173);

                if (!map.isEveryTerritoryOccupied())
                {
                    boolean computerHasOccupied = false;
                    while (!computerHasOccupied)
                    {
                        int territoryNumber = random.nextInt(map.getTerritoriesQuantity()-1);
                        Territory computerT = map.getTerritoryByIndex(territoryNumber);
                        if (computerT.getOwnership() == 0)
                        {
                            map.setTerritoryOwnership(computerT.getName(), 1);
                            map.setArmeeBesetzungen(computerT.getName(), 1);
                            computerT.setPolygonsColor(201,175,139);
                            computerHasOccupied = true;
                        }
                    }
                }
            }
        }
        if (event.equals("mouseEntered") && t.getOwnership() == 0)
        {
            primaryStage.setTitle("occupy " + territoryName);
        }
        if (event.equals("mouseExited"))
        {
            int freeTerritories = map.numberOfFreeTerritories();
            primaryStage.setTitle("territory acquisition - " + freeTerritories + " territories to occupy");
        }

        if (map.isEveryTerritoryOccupied()){ phase = 1; }
    }
}
