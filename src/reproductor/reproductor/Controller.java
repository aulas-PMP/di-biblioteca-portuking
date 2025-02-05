package reproductor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Controller {
    @FXML
    private Label label;

    @FXML
    private Button toggleSideLeftbarButton;

    @FXML
    private VBox leftPanel;

    @FXML
    private VBox multimediaPanel; // Panel derecho

    @FXML
    private Button toggleSideRigthbarButton; // Botón de minimizar de la barra derecha

    private boolean sidebarVisible = true;
    private boolean isRightPanelVisible = true; // Estado inicial: la barra está visible

    public void initialize() {
        // Evento para minimizar y restaurar la barra izquierda
        toggleSideLeftbarButton.setOnAction(event -> toggleSidebar()); 
        // Evento para minimizar y restaurar la barra derecha
        toggleSideRigthbarButton.setOnAction(event -> toggleRightSidebar());
    }

    private void toggleSidebar() {
        if (sidebarVisible) {
            leftPanel.setPrefWidth(0);  // Reduce el ancho de la barra lateral
            toggleSideLeftbarButton.setText("⏩"); // Cambia el ícono del botón
        } else {
            leftPanel.setPrefWidth(206); // Restaura el ancho original de la barra lateral
            toggleSideLeftbarButton.setText("⏪"); // Cambia el ícono del botón
        }
        sidebarVisible = !sidebarVisible; // Alterna el estado
    }

    private void toggleRightSidebar() {
        if (isRightPanelVisible) {
            multimediaPanel.setPrefWidth(0);  // Reduce el ancho de la barra lateral
            toggleSideRigthbarButton.setText("⏪"); // Cambia el ícono del botón
        } else {
            multimediaPanel.setPrefWidth(206); // Restaura el ancho original de la barra lateral
            toggleSideRigthbarButton.setText("⏩"); // Cambia el ícono del botón
        }
        isRightPanelVisible = !isRightPanelVisible; // Alterna el estado
    }
}

