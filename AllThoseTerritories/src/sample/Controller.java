package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class Controller
{
    // Views
    @FXML private Label meinText;   // Achtung auf richtigen Klassen Import - javafx... !
                                    // @FXML zum Zugriff essentiell
    public Main main;
    private int count = 0;

    public void setMain(Main main)
    {
        this.main = main;
    }

    @FXML
    public void meinButtonPressed()
    {
        meinText.setText("Yo " + count);
        count++;
    }
}
