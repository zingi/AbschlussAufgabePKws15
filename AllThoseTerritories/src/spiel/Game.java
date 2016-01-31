package spiel;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by aaronzingerle on 23.01.16.
 */
public class Game
{
    private Stage primaryStage;
    public AnchorPane pane;
    public Map map;     // public access needed in Fight Class
    private int phase = 0;
    boolean recruitmentDistribution = true;
    int recruitmentNumber = 0;
    Territory firstTerritoryChoice = null;
    Territory enemyTerritoryChoice = null;
    Territory transferTerritoryChoice = null;
    Random random = new Random();
    private boolean winInThisRound = false; // to move armys to new adopted land
    private boolean canFight = true;
    public boolean isWhiteBoardOpened = false;
    Fight fight;

    public Game(String dateiPfad, AnchorPane pane, Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.pane = pane;
        map = new Map(dateiPfad, pane, this);
        setKeyPressedEvent();
    }

    public void setKeyPressedEvent()
    {
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event ->
        {
            if (event.getCode() == KeyCode.ENTER)
            {
                territoryEvent(null, "enterKeyPressed");
            }
        });
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event ->
        {
            if (event.getCode() == KeyCode.ESCAPE)
            {
                territoryEvent(null, "escapeKeyPressed");
            }
        });
    }

    public void territoryEvent(String territoryName, String event)
    {
        if (isWhiteBoardOpened){return;}
        EndOfGame.check(this);

        if (phase == 1 && territoryName!= null)
        {
            phaseEroberung(territoryName, event);
        }
        if (phase == 0 && territoryName != null)
        {
            phaseLanderwerb(territoryName, event);
        }
        if (event.equals("escapeKeyPressed"))
        {
            if ((firstTerritoryChoice != null && enemyTerritoryChoice == null && canFight == true) ||
                    (enemyTerritoryChoice != null && !winInThisRound) || (canFight == false))
            {
                if (firstTerritoryChoice != null)
                {
                    firstTerritoryChoice.setPolygonsStrokeColor(89,38,69);
                }
                firstTerritoryChoice = null;
                map.clearAllEffects();
            }
        }
        if (event.equals("enterKeyPressed"))
        {
            if ((enemyTerritoryChoice != null && !winInThisRound) || (canFight == false))
            {
                map.clearAllEffects();
                if (firstTerritoryChoice != null)
                {
                    firstTerritoryChoice.setPolygonsStrokeColor(89,38,69);
                }

                // end this round
                // computer an der Reihe

                ComputerTrait computerTrait = new ComputerTrait(this);
                EndOfGame.check(this);

                // new Round after Computer Trait
                newRound();
            }
            else if (winInThisRound)    // finished moving armies to new adopted land
            {
                winInThisRound = false;
                firstTerritoryChoice = null;

                map.clearAllEffects();
                primaryStage.setTitle("now you can transfer armies between two territories [finish: press enter]");
            }
        }
    }

    public void newRound()
    {
        recruitmentNumber = 0;
        recruitmentDistribution = true;
        phase = 0;

        firstTerritoryChoice = null;
        enemyTerritoryChoice = null;
        transferTerritoryChoice = null;
        winInThisRound = false;
        canFight = true;
    }

    public void phaseLanderwerb(String territoryName, String event)
    {
        Territory t = map.getTerritoryByName(territoryName);
        if (event.equals("mouseLeftClicked"))
        {
            if (t.getOwnership() == 0)      // player choose a territory (receives it if it's free)
            {
                map.setTerritoryOwnership(territoryName, 2);
                map.setArmeeBesetzungen(territoryName, 1);
                t.setPolygonsColor(180,219,173);

                if (!map.isEveryTerritoryOccupied())    // computer choose a territory if at least one is free
                {
                    boolean computerHasOccupied = false;
                    while (!computerHasOccupied)
                    {
                        int territoryNumber = random.nextInt(map.getTerritoriesQuantity());
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

        if (map.isEveryTerritoryOccupied())
        {
            phase = 1;
            recruitmentNumber = map.getRecruitment(2);
        }
    }

    public void phaseEroberung(String territoryName, String event)
    {
        Territory t = map.getTerritoryByName(territoryName);
        if (recruitmentDistribution)
        {
            sendRecruits(t, event);
            if (!map.canUserAttack()) { canFight = false; }
            else { canFight = true; }
        }
        else
        {
            if (firstTerritoryChoice != null)
            {
                firstTerritoryChoice.setPolygonsStrokeColor(0,255,0);
            }
            if (event.equals("mouseLeftClicked"))   // select homeland to start action
            {
                if (firstTerritoryChoice == null && t.getOwnership() == 2)
                {
                    firstTerritoryChoice = t;
                    Territory[] neighbors = firstTerritoryChoice.getNeighbors();
                    Territory[] group = new Territory[neighbors.length+1];
                    for (int i=0; i<firstTerritoryChoice.getNeighbors().length; i++)
                    {
                        group[i] = neighbors[i];
                    }
                    group[group.length-1] = firstTerritoryChoice;
                    map.blurExcept(group);
                }

            }
            if (event.equals("mouseRightClicked"))
            {
                if (firstTerritoryChoice != null && enemyTerritoryChoice == null)   // select fight partner
                {
                    if (map.canUserAttackFrom(t, firstTerritoryChoice))
                    {
                        enemyTerritoryChoice = t;
                        fight = new Fight(firstTerritoryChoice, enemyTerritoryChoice, this);
                        if (fight.getWin())
                        {
                            EndOfGame.check(this);

                            winInThisRound = true;
                            Territory[] group = new Territory[2];
                            group[0] = fight.getEnemy();
                            group[1] = fight.getFriend();

                            primaryStage.setTitle("right click - exchange armies to " + fight.getEnemy().getName()
                            + " [finish: press enter]");
                        }
                        else    // handle fail attack
                        {
                            primaryStage.setTitle("now you can transfer armies between two territories [finish: press enter]");
                            firstTerritoryChoice.setPolygonsStrokeColor(89,38,69);
                            firstTerritoryChoice = null;
                            map.clearAllEffects();
                        }
                    }
                }
                if (firstTerritoryChoice != null && enemyTerritoryChoice != null && winInThisRound) // move armies to adopted territory
                {
                    if (t.equals(fight.getEnemy()) || t.equals(fight.getFriend()))
                    {
                        Territory t1 = fight.getFriend();
                        Territory t2 = fight.getEnemy();

                        if (t.equals(t1) && map.getArmeeBesetzungen(t1.getName()) > 1)
                        {
                            map.setArmeeBesetzungen(t1.getName(), map.getArmeeBesetzungen(t1.getName())-1);
                            map.setArmeeBesetzungen(t2.getName(), map.getArmeeBesetzungen(t2.getName())+1);
                        }
                        else if (t.equals(t2) && map.getArmeeBesetzungen(t2.getName()) > 1)
                        {
                            map.setArmeeBesetzungen(t2.getName(), map.getArmeeBesetzungen(t2.getName())-1);
                            map.setArmeeBesetzungen(t1.getName(), map.getArmeeBesetzungen(t1.getName())+1);
                        }
                    }
                }
                if ((firstTerritoryChoice != null && enemyTerritoryChoice != null && !winInThisRound) ||
                        (firstTerritoryChoice!= null && canFight == false))     // select movement destination
                {
                    if (!t.equals(firstTerritoryChoice) && t.getOwnership() == 2)
                    {
                        if (Arrays.asList(firstTerritoryChoice.getNeighbors()).contains(t))
                        {
                            transferTerritoryChoice = t;
                            Territory[] group = {transferTerritoryChoice, firstTerritoryChoice};
                            map.blurExcept(group);
                        }
                    }
                }
                if (firstTerritoryChoice != null && transferTerritoryChoice != null) // move armies
                {
                    if (t.equals(firstTerritoryChoice) || t.equals(transferTerritoryChoice))
                    {
                        Territory t1 = firstTerritoryChoice;
                        Territory t2 = transferTerritoryChoice;

                        if (t.equals(t1) && map.getArmeeBesetzungen(t1.getName()) > 1)
                        {
                            map.setArmeeBesetzungen(t1.getName(), map.getArmeeBesetzungen(t1.getName())-1);
                            map.setArmeeBesetzungen(t2.getName(), map.getArmeeBesetzungen(t2.getName())+1);
                        }
                        else if (t.equals(t2) && map.getArmeeBesetzungen(t2.getName()) > 1)
                        {
                            map.setArmeeBesetzungen(t2.getName(), map.getArmeeBesetzungen(t2.getName())-1);
                            map.setArmeeBesetzungen(t1.getName(), map.getArmeeBesetzungen(t1.getName())+1);
                        }
                    }
                }
            }
            if (event.equals("mouseEntered"))
            {
                primaryStage.setTitle("-" + t.getName() + "-");
            }
            if (event.equals("mouseExited"))    // show possible actions during phaseEroberung
            {
                if (firstTerritoryChoice == null)
                {
                    primaryStage.setTitle("[click] friendly territory to start an action");
                }
                else if (firstTerritoryChoice != null && enemyTerritoryChoice == null && canFight == true)
                {
                    primaryStage.setTitle("[r.click] valid enemy territory, to start attack");
                }
                else if (firstTerritoryChoice != null && transferTerritoryChoice != null)
                {
                    primaryStage.setTitle("to end moving, press [enter]");
                }
                else if ((firstTerritoryChoice != null && canFight == false) ||
                (firstTerritoryChoice != null && enemyTerritoryChoice != null && !winInThisRound))
                {
                    primaryStage.setTitle("[r.click] second friendly territory to move armies, or press [enter] to end");
                }
            }
        }
    }

    public void sendRecruits(Territory t, String event)
    {
        boolean computerDistribution = false;
        if (t.getOwnership() == 2)
        {
            if (event.equals("mouseLeftClicked") && recruitmentNumber > 0)
            {
                map.setArmeeBesetzungen(t.getName(), map.getArmeeBesetzungen(t.getName())+1);
                recruitmentNumber--;
                if (recruitmentNumber == 0) { computerDistribution = true; }
            }
            if (event.equals("mouseEntered"))
            {
                primaryStage.setTitle("send Army to - " + t.getName());
            }
        }
        if (event.equals("mouseExited"))
        {
            primaryStage.setTitle("recruitment distribution - " + recruitmentNumber + " new available armies");
        }

        if (computerDistribution)
        {
            int computerRecruitmentNumber = map.getRecruitment(1);
            while (computerRecruitmentNumber > 0)
            {
                int territoryNumber = random.nextInt(map.getTerritoriesQuantity()-1);
                Territory computerT = map.getTerritoryByIndex(territoryNumber);
                if (computerT.getOwnership() == 1)
                {
                    map.setArmeeBesetzungen(computerT.getName(), map.getArmeeBesetzungen(computerT.getName())+1);
                    computerRecruitmentNumber--;
                }
            }
            recruitmentDistribution = false;
        }
    }
}
