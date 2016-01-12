package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.events.MouseEvent;

import java.beans.EventHandler;
import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private AnchorPane pane;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;
        hauptFenster();
    }

    public void hauptFenster()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("sample.fxml"));
            pane = loader.load();

            primaryStage.setWidth(500.00);
            primaryStage.setHeight(500.00);

            Controller controller = loader.getController();
            controller.setMain(this);

            Scene scene = new Scene(pane);

            primaryStage.setTitle("sample");
            primaryStage.setScene(scene);
            primaryStage.show();

            drawSomething();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void drawSomething()
    {
        Polygon polygon = new Polygon(10,110,120,130,200,10);
        pane.getChildren().add(polygon);

        Rectangle rectangle = new Rectangle(200,200,100,100);
        rectangle.setFill(Color.BROWN);
        pane.getChildren().add(rectangle);

        polygon.setOnMouseEntered(new javafx.event.EventHandler<javafx.scene.input.MouseEvent>()
        {
            @Override
            public void handle(javafx.scene.input.MouseEvent event)
            {
                polygon.setFill(Color.BROWN);
                rectangle.setFill(Color.BLACK);
            }
        });

        rectangle.setOnMouseEntered(new javafx.event.EventHandler<javafx.scene.input.MouseEvent>()
        {
            @Override
            public void handle(javafx.scene.input.MouseEvent event)
            {
                polygon.setFill(Color.BLACK);
                rectangle.setFill(Color.BROWN);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
