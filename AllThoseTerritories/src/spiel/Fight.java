package spiel;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by aaronzingerle on 25.01.16.
 */
public class Fight
{
    private Territory friend;
    private Territory enemy;
    private Game game;
    private Random random = new Random();
    private ArrayList<Integer> friendDice = new ArrayList<Integer>();
    private ArrayList<Integer> enemyDice = new ArrayList<Integer>();
    private Rectangle whiteBoard;
    private Label message;
    private boolean win;
    private int friendSurvivors = 0;

    public Fight(Territory friend, Territory enemy, Game game)
    {
        this.friend = friend;
        this.enemy = enemy;
        this.game = game;

        rollDice();
        drawFight();
    }

    public boolean getWin()         { return win; }
    public Territory getEnemy()     { return enemy; }
    public Territory getFriend()    { return friend; }

    private void rollDice()
    {
        if (game.map.getArmeeBesetzungen(enemy.getName()) == 1)
        {
            int friendRandomNumber = getDiceNumber();
            int enemyRandomNumber = getDiceNumber();
            friendDice.add(friendRandomNumber);
            enemyDice.add(enemyRandomNumber);
        }
        else
        {
            int fRN1 = getDiceNumber();
            int fRN2 = getDiceNumber();
            int eRN1 = getDiceNumber();
            int eRN2 = getDiceNumber();

            friendDice.add(fRN1);
            friendDice.add(fRN2);
            enemyDice.add(eRN1);
            enemyDice.add(eRN2);
        }
        Collections.sort(friendDice);
        Collections.sort(enemyDice);
    }

    private void drawFight()
    {
        whiteBoard = new Rectangle(1250, 650, Color.rgb(255,255,255,0.7));
        message = new Label();
        message.setTranslateX(100);
        message.setTranslateY(250);
        message.setFont(new Font(20));

        String messageText = "You have attacked, " + enemy.getName() + " from " + friend.getName() + "\n";
        for (Integer i: friendDice)
        {
            messageText += "You: " + i + "\n";
        }
        for (Integer i: enemyDice)
        {
            messageText += "Enemy: " + i + "\n";
        }
        win = true; // so long as the defense did not win
        for (int i=0; i<friendDice.size(); i++)
        {
            int fD = friendDice.get(i);
            int eD = enemyDice.get(i);

            if (fD < eD)
            {
                win = false;
                game.map.setArmeeBesetzungen(friend.getName(), game.map.getArmeeBesetzungen(friend.getName())-1);
                messageText += fD + " vs. " + eD + ", friendly army died \n";
            }
            else if(fD > eD)
            {
                game.map.setArmeeBesetzungen(enemy.getName(), game.map.getArmeeBesetzungen(enemy.getName())-1);
                friendSurvivors++;
                messageText += fD + " vs. " + eD + ", enemy army killed \n";
            }
            else
            {
                game.map.setArmeeBesetzungen(enemy.getName(), game.map.getArmeeBesetzungen(enemy.getName())-1);
                game.map.setArmeeBesetzungen(friend.getName(), game.map.getArmeeBesetzungen(friend.getName())-1);
                messageText += fD + " vs. " + eD + ", friendly army & enemy army died \n";
            }
        }
        if (win)
        {
            messageText += "\n" + enemy.getName() + " takeover success";
            handleWin();
        }

        message.setText(messageText);
        game.pane.getChildren().add(whiteBoard);
        game.pane.getChildren().add(message);

        game.isWhiteBoardOpened = true;

        whiteBoard.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                finishFight();
            }
        });
    }

    private void handleWin()
    {
        enemy.setOwnership(2);
        enemy.setPolygonsColor(180,219,173);
        game.map.setArmeeBesetzungen(enemy.getName(), friendSurvivors+1);
        game.map.setArmeeBesetzungen(friend.getName(), game.map.getArmeeBesetzungen(friend.getName())-(friendSurvivors+1));
    }

    private void finishFight()
    {
        game.pane.getChildren().remove(whiteBoard);
        game.pane.getChildren().remove(message);
        game.map.clearAllEffects();

        if (win)
        {
            Territory[] group = {friend, enemy};
            game.map.blurExcept(group);
        }

        game.isWhiteBoardOpened = false;
    }

    public static int getDiceNumber()
    {
        return new Random().nextInt(6)+1;
    }
}
