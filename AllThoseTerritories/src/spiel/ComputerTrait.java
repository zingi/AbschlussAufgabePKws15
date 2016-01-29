package spiel;

/*
*
*   // Class for Computer Attack & Computer - movement of armies
* */

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class ComputerTrait
{
    private Game game;
    private Rectangle whiteBoard;
    private Label message;
    private String messageText = "";
    private boolean win;
    private int friendSurvivors = 0;

    private Random random = new Random();
    private HashMap<Territory, ArrayList<Territory>> possibleAttacks = new HashMap<Territory, ArrayList<Territory>>();
    private HashMap<Territory, ArrayList<Territory>> possibleMove = new HashMap<Territory, ArrayList<Territory>>();
    private ArrayList<Integer> friendDice = new ArrayList<Integer>();
    private ArrayList<Integer> enemyDice = new ArrayList<Integer>();

    public ComputerTrait(Game game)
    {
        this.game = game;

        trait();
    }

    private void trait()
    {
        if (canComputerAttack()) { computerAttack(); }
        else { messageText += "Computer could not attack. \n\n"; }

        if (canComputerMove()) { computerMove(); }
        else { messageText += "Computer could not move. \n"; }

        drawComputerTrait();
    }

    private void computerAttack()
    {
        int attackStartTerritoryIndex;
        int attackGoalTerritoryIndex;
        int startTerritoryOccupation;   // occupation - how many armies are in this Territory
        int goalTerritoryOccupation;

        Territory attackStartTerritory;
        Territory attackGoalTerritory;

        Territory[] possibleAttackStartTerritories = new Territory[possibleAttacks.size()];
        Territory[] possibleAttackGoalTerritories;
        possibleAttackStartTerritories  = possibleAttacks.keySet().toArray(possibleAttackStartTerritories);

        attackStartTerritoryIndex       = random.nextInt(possibleAttackStartTerritories.length);
        attackStartTerritory            = possibleAttackStartTerritories[attackStartTerritoryIndex];

        possibleAttackGoalTerritories   = new Territory[possibleAttacks.get(attackStartTerritory).size()];
        possibleAttackGoalTerritories   = possibleAttacks.get(attackStartTerritory).toArray(possibleAttackGoalTerritories);

        attackGoalTerritoryIndex        = random.nextInt(possibleAttackGoalTerritories.length);
        attackGoalTerritory             = possibleAttackGoalTerritories[attackGoalTerritoryIndex];

        startTerritoryOccupation        = game.map.getArmeeBesetzungen(attackStartTerritory.getName());
        goalTerritoryOccupation         = game.map.getArmeeBesetzungen(attackGoalTerritory.getName());

        Territory friend = attackStartTerritory;  // duplicate Variable using for remaining compliant with linelength
        Territory enemy = attackGoalTerritory;

        if (game.map.getArmeeBesetzungen(enemy.getName()) == 1)
        {
            friendDice.add(Fight.getDiceNumber());
            enemyDice.add(Fight.getDiceNumber());
        }
        else
        {
            for (int i=0; i<2; i++)
            {
                friendDice.add(Fight.getDiceNumber());
                enemyDice.add(Fight.getDiceNumber());
            }
        }

        Collections.sort(friendDice);
        Collections.sort(enemyDice);

        for (Integer i: friendDice) { messageText += "Computer: " + i + "\n"; }
        for (Integer i: enemyDice) { messageText += "User: " + i + "\n"; }

        win = true;
        for (int i=0; i<friendDice.size(); i++)
        {
            int fD = friendDice.get(i);
            int eD = enemyDice.get(i);

            if (fD < eD)
            {
                win = false;
                game.map.setArmeeBesetzungen(friend.getName(), game.map.getArmeeBesetzungen(friend.getName())-1);
                messageText += fD + " vs. " + eD + ", computer army died \n";
            }
            else if(fD > eD)
            {
                game.map.setArmeeBesetzungen(enemy.getName(), game.map.getArmeeBesetzungen(enemy.getName())-1);
                friendSurvivors++;
                messageText += fD + " vs. " + eD + ", user army killed \n";
            }
            else
            {
                game.map.setArmeeBesetzungen(enemy.getName(), game.map.getArmeeBesetzungen(enemy.getName())-1);
                game.map.setArmeeBesetzungen(friend.getName(), game.map.getArmeeBesetzungen(friend.getName())-1);
                messageText += fD + " vs. " + eD + ", computer army & user army died \n";
            }
        }
        if (win)
        {
            messageText += "\n" + enemy.getName() + " computer - takeover success \n";

            enemy.setOwnership(1);
            enemy.setPolygonsColor(201,175,139);
            game.map.setArmeeBesetzungen(enemy.getName(), friendSurvivors+1);
            game.map.setArmeeBesetzungen(friend.getName(), game.map.getArmeeBesetzungen(friend.getName())-(friendSurvivors+1));

            //let armies follow to new acquired land
            int transferQuantity = 0;
            if (game.map.getArmeeBesetzungen(friend.getName()) > 1)
            {
                int availableArmies = game.map.getArmeeBesetzungen(friend.getName());
                transferQuantity = random.nextInt(availableArmies);   // so one army always remain at home

                game.map.setArmeeBesetzungen(enemy.getName(), game.map.getArmeeBesetzungen(enemy.getName()) + transferQuantity);
                game.map.setArmeeBesetzungen(friend.getName(), game.map.getArmeeBesetzungen(friend.getName()) - transferQuantity);
            }
            messageText += "transferred: " + transferQuantity + " to " + enemy.getName();
        }
        else
        {
            messageText += "\n" + enemy.getName() + " computer - takeover fail \n";
        }
    }

    private void computerMove()
    {
        int moveOriginIndex = random.nextInt(possibleMove.size());
        int moveGoalIndex;
        int moveQuantity = 0;
        int originQuantityBefore;
        int goalQuantityBefore;

        Territory moveOrigin    = possibleMove.keySet().toArray(new Territory[possibleMove.size()])[moveOriginIndex];
        moveGoalIndex           = random.nextInt(possibleMove.get(moveOrigin).size());
        Territory moveGoal      = possibleMove.get(moveOrigin).get(moveGoalIndex);
        originQuantityBefore    = game.map.getArmeeBesetzungen(moveOrigin.getName());
        goalQuantityBefore      = game.map.getArmeeBesetzungen(moveGoal.getName());

        moveQuantity = random.nextInt(originQuantityBefore);

        game.map.setArmeeBesetzungen(moveOrigin.getName(), originQuantityBefore - moveQuantity);
        game.map.setArmeeBesetzungen(moveGoal.getName(), goalQuantityBefore + moveQuantity);

        messageText += "\n Computer moved from " + moveOrigin.getName() + " ";
        messageText += moveQuantity + " armies to " + moveGoal.getName() + "\n";
    }

    private void drawComputerTrait()
    {
        whiteBoard = new Rectangle(1250, 650, Color.rgb(255,255,255,0.7));
        message = new Label();
        message.setTranslateX(100);
        message.setTranslateY(100);
        message.setFont(new Font(20));


        message.setText(messageText);
        game.pane.getChildren().add(whiteBoard);
        game.pane.getChildren().add(message);

        whiteBoard.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                finishComputerTrait();
            }
        });
    }

    private void finishComputerTrait()
    {
        game.pane.getChildren().remove(whiteBoard);
        game.pane.getChildren().remove(message);
        game.map.clearAllEffects();
    }

    private boolean canComputerAttack()
    {
        boolean isPossible = false;
        for (Territory t: game.map.getAllTerritories())
        {
            if (t.getOwnership() == 1)
            {
                Territory[] neighbors = t.getNeighbors();
                for (Territory n: neighbors)
                {
                    if (n.getOwnership() == 2)
                    {
                        int friends = game.map.getArmeeBesetzungen(t.getName());
                        int enemies = game.map.getArmeeBesetzungen(n.getName());

                        if ((enemies <= 2 && friends >= 3) || (enemies > 2 && friends >=4))
                        {
                            if (possibleAttacks.keySet().contains(t))
                            {
                                possibleAttacks.get(t).add(n);
                            }
                            else
                            {
                                ArrayList<Territory> possibleAttackTerritories = new ArrayList<Territory>();
                                possibleAttackTerritories.add(n);
                                possibleAttacks.put(t, possibleAttackTerritories);
                            }
                            isPossible = true;
                        }
                    }
                }
            }
        }
        return isPossible;
    }
    private boolean canComputerMove()
    {
        boolean isPossible = false;
        for (Territory t: game.map.getAllTerritories())
        {
            if (t.getOwnership() == 1)
            {
                Territory[] neighbors = t.getNeighbors();
                for (Territory n: neighbors)
                {
                    if (n.getOwnership() == 1)
                    {
                        int tArmies = game.map.getArmeeBesetzungen(t.getName());
                        int nArmies = game.map.getArmeeBesetzungen(n.getName());
                        if (tArmies > 1 || nArmies > 1)
                        {
                            if (possibleMove.keySet().contains(t))
                            {
                                possibleMove.get(t).add(n);
                            }
                            else
                            {
                                ArrayList<Territory> canTransferTerritories = new ArrayList<Territory>();
                                canTransferTerritories.add(n);
                                possibleMove.put(t, canTransferTerritories);
                            }
                            isPossible = true;
                        }
                    }
                }
            }
        }
        return isPossible;
    }
}
