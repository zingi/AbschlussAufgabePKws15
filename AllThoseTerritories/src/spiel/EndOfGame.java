package spiel;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;

/**
 * Created by aaronzingerle on 30.01.16.
 */
public class EndOfGame
{
    public EndOfGame(){}

    public static int check(Game game)
    {
        int returnValue = 0; // 0: no win; 1: computer win; 2: user win;

        ArrayList<Integer> ownerships = new ArrayList<Integer>();

        for (Territory t: game.map.getAllTerritories()) { ownerships.add(game.map.getArmeeBesetzungen(t.getName())); }

        if (ownerships.contains(1) && ownerships.contains(2)){ returnValue = 0; }
        else if (ownerships.contains(1)){ returnValue = 1; }
        else if (ownerships.contains(2)){ returnValue = 2; }

        if (returnValue == 1 || returnValue == 2){ handleEnd(game, returnValue); }

        return returnValue;
    }

    private static void handleEnd(Game game, int endStatus)
    {
        Rectangle whiteBoard;
        Label message;
        String messageText = "";

        whiteBoard = new Rectangle(1250, 650, Color.rgb(255,255,255,0.7));
        message = new Label();
        message.setTranslateX(100);
        message.setTranslateY(250);
        message.setFont(new Font(20));


        if (endStatus == 1)
        {
            messageText += "Computer has won.";
        }
        else if (endStatus == 2)
        {
            messageText += "User has won.";
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
                // clear board and start new game
                /*
                game.pane.getChildren().remove(whiteBoard);
                game.pane.getChildren().remove(message);
                game.map.clearAllEffects();
                */
            }
        });
    }
}
