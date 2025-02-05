package reproductor;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

public class Controller {

    @FXML
    private Label libraryTitleLabel;

    /** Botón de minimizar de la barra izquierda */
    @FXML
    private Button toggleSideLeftbarButton;
    /** Panel izquierdo */
    @FXML
    private VBox leftPanel;
    /** Panel derecho */
    @FXML
    private VBox multimediaPanel;
    /** Botón de minimizar de la barra derecha */
    @FXML
    private Button toggleSideRigthbarButton;

    @FXML
    private ListView<String> videoListView;

    @FXML
    private Button btnAddVideo;

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private MediaView mediaView; // Para reproducir el video

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button btnChangeSize; // Botón para cambiar tamaño del video

    @FXML
    private StackPane mediaPlayerContainer; // Contenedor del MediaView

    @FXML
    private Button btnChangeSpeed; // Botón para cambiar la velocidad

    private double[] speedOptions = {0.5, 1.0, 1.5, 2.0}; // Opciones de velocidad
    private int currentSpeedIndex = 1; // Índice inicial (1.0x velocidad normal)

    /** Estado inicial de la barra izquierda: la barra está visible */
    private boolean isLeftPanelVisible = true;
    /** Estado inicial de la barra derecha: la barra está visible */
    private boolean isRightPanelVisible = true;
    private MediaPlayer mediaPlayer;
    private boolean isFullSize = false; // Estado del tamaño del video

    /**
     * Método con la lógica del reproductor
     */
    public void initialize() {
        toggleSideLeftbarButton.setOnAction(event -> toggleSidebar());
        toggleSideRigthbarButton.setOnAction(event -> toggleRightSidebar());
        // Evento para añadir videos a la lista
        btnAddVideo.setOnAction(event -> openFileChooser());

        // Evento de doble clic para reproducir el video seleccionado
        videoListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedMedia = videoListView.getSelectionModel().getSelectedItem();
                if (selectedMedia != null) {
                    // Extraemos la ruta del archivo después del separador "|"
                    String mediaPath = selectedMedia.split("\\|")[1].trim();
                    playMedia(mediaPath);
                }
            }
        });
        

        // Configurar acciones para los botones
        playButton.setOnAction(event -> playMedia());
        pauseButton.setOnAction(event -> pauseMedia());
        stopButton.setOnAction(event -> stopMedia());

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    
        // Establecer el volumen inicial en 50%
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0.5);
        }
        btnChangeSize.setOnAction(event -> toggleVideoSize());
        btnChangeSpeed.setOnAction(event -> changeVideoSpeed());
    }

    /**
     * Método que permite realizar la lógica de minimizar y máximizar el panel
     * izquierdo
     */
    private void toggleSidebar() {
        if (isLeftPanelVisible) {
            leftPanel.setPrefWidth(0);
            toggleSideLeftbarButton.setText("⏩");
        } else {
            leftPanel.setPrefWidth(206);
            toggleSideLeftbarButton.setText("⏪");
        }
        isLeftPanelVisible = !isLeftPanelVisible;
    }

    /**
     * Método que permite realizar la lógica de minimizar y máximizar el panel
     * derecho
     */
    private void toggleRightSidebar() {
        if (isRightPanelVisible) {
            multimediaPanel.setPrefWidth(0);
            toggleSideRigthbarButton.setText("⏪");
        } else {
            multimediaPanel.setPrefWidth(206);
            toggleSideRigthbarButton.setText("⏩");
        }
        isRightPanelVisible = !isRightPanelVisible;
    }

    // Método para abrir el explorador de archivos y seleccionar un video
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
            "Archivos Multimedia", "*.mp4", "*.avi", "*.mkv", "*.mp3", "*.wav"));
    
        File selectedFile = fileChooser.showOpenDialog(null);
    
        if (selectedFile != null) {
            addMediaToLibrary(selectedFile);
        }
    }
    

    // Método para agregar el video a la lista
    private void addMediaToLibrary(File mediaFile) {
        if (mediaFile != null) {
            String fileName = mediaFile.getName();
            String filePath = mediaFile.getAbsolutePath();
    
            if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")) {
                videoListView.getItems().add("[AUDIO] " + fileName + " | " + filePath);
            } else {
                videoListView.getItems().add("[VIDEO] " + fileName + " | " + filePath);
            }
        }
    }
    

    // Método para reproducir un video
    private void playMedia(String mediaPath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Detener cualquier reproducción en curso
        }
    
        Media media = new Media(new File(mediaPath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    
        if (mediaPath.endsWith(".mp3") || mediaPath.endsWith(".wav")) {
            // Es un archivo de audio → Ocultamos MediaView y solo reproducimos el audio
            mediaView.setVisible(false);
        } else {
            // Es un archivo de video → Mostramos el MediaView
            mediaView.setMediaPlayer(mediaPlayer);
            mediaView.setVisible(true);
        }
    
        mediaPlayer.play();
    }
    

    // Método para iniciar la reproducción del video
    private void playMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    // Método para pausar el video
    private void pauseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // Método para detener el video y reiniciar la posición
    private void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void toggleVideoSize() {
        if (isFullSize) {
            // Volver al tamaño normal
            mediaView.setFitWidth(700);
            mediaView.setFitHeight(400);
            mediaPlayerContainer.setStyle("-fx-background-color: black;"); // Mantiene el fondo negro
            btnChangeSize.setText("Cambiar Tamaño");
        } else {
            // Expandir dentro del panel
            mediaView.setFitWidth(mediaPlayerContainer.getWidth());
            mediaView.setFitHeight(mediaPlayerContainer.getHeight());
            mediaPlayerContainer.setStyle("-fx-background-color: black; -fx-padding: 0;");
            btnChangeSize.setText("Restaurar Tamaño");
        }
        isFullSize = !isFullSize; // Alternar estado
    }

    private void changeVideoSpeed() {
        if (mediaPlayer != null) {
            currentSpeedIndex = (currentSpeedIndex + 1) % speedOptions.length; // Cambiar al siguiente índice
            double newSpeed = speedOptions[currentSpeedIndex]; // Obtener la nueva velocidad
            mediaPlayer.setRate(newSpeed); // Aplicar la velocidad al MediaPlayer
            btnChangeSpeed.setText("Velocidad: " + newSpeed + "x"); // Actualizar texto del botón
        }
    }

}
