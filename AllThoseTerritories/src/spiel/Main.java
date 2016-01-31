package spiel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by aaronzingerle on 11.01.16.
 */
public class Main extends Application
{
    public static String[] arguments;
    private Stage primaryStage;
    private AnchorPane pane;

    private Game game;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;
        mainWindow();
    }

    public void mainWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
            pane = loader.load();

            primaryStage.setWidth(1250.00);
            primaryStage.setHeight(650.00);
            primaryStage.setResizable(false);

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setMain(this);

            Scene scene = new Scene(pane);

            primaryStage.setScene(scene);
            primaryStage.show();

            if (arguments.length > 0)
            {
                if (MapLoader.checkIfFileExists(arguments[0]))
                {
                    String mapFile = arguments[0];

                    System.out.println("[" + mapFile + "] loaded");
                    game = new Game(mapFile, pane, primaryStage);
                }
                else
                {
                    System.err.println("passed file not valid");
                    System.exit(0);
                }
            }
            else
            {
                System.err.println("no file passed");
                System.exit(0);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        arguments = args;

        launch(args);
    }
}
