package spiel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by aaronzingerle on 11.01.16.
 */
public class Main extends Application
{
    private Stage primaryStage;
    private MapLoader mapLoader;

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
            AnchorPane pane = loader.load();

            primaryStage.setWidth(1250.00);
            primaryStage.setHeight(650.00);
            primaryStage.setResizable(false);

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setMain(this);

            Scene scene = new Scene(pane);

            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Main m = new Main();
        m.mapLoader = new MapLoader("src/spiel/world.map");
        m.mapLoader.execute();

        m.mapLoader.inhaltAusgeben();   // dient nur zur Kontrolle

        launch(args);
    }
}
