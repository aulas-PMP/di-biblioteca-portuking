package reproductor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Controller {
    @FXML
    private Label label;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    private VBox leftPanel;

    private boolean sidebarVisible = true;

    public void initialize() {
        toggleSidebarButton.setOnAction(event -> toggleSidebar()); 
    }

    private void toggleSidebar() {
        if (sidebarVisible) {
            leftPanel.setPrefWidth(0);  // Reduce el ancho de la barra lateral
            toggleSidebarButton.setText("⏩"); // Cambia el ícono del botón
        } else {
            leftPanel.setPrefWidth(206); // Restaura el ancho original de la barra lateral
            toggleSidebarButton.setText("⏪"); // Cambia el ícono del botón
        }
        sidebarVisible = !sidebarVisible; // Alterna el estado
    }
}

