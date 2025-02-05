package reproductor;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.scene.control.ListView;

public class Controller {

    @FXML
    private Label libraryTitleLabel;

    /**Botón de minimizar de la barra izquierda */
    @FXML
    private Button toggleSideLeftbarButton;
    /**Panel izquierdo */
    @FXML
    private VBox leftPanel;
    /**Panel derecho */
    @FXML
    private VBox multimediaPanel; 
    /**Botón de minimizar de la barra derecha */
    @FXML
    private Button toggleSideRigthbarButton;

    @FXML
    private ListView<String> videoListView;

    @FXML
    private Button btnAddVideo;

    @FXML
    private MediaView mediaView; // Para reproducir el video

    /**Estado inicial de la barra izquierda: la barra está visible */
    private boolean isLeftPanelVisible = true;
    /**Estado inicial de la barra derecha: la barra está visible */
    private boolean isRightPanelVisible = true;
    private MediaPlayer mediaPlayer;

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
                 String selectedVideo = videoListView.getSelectionModel().getSelectedItem();
                 if (selectedVideo != null) {
                     playVideo(selectedVideo);
                 }
             }
         });
    }

    /**
     * Método que permite realizar la lógica de minimizar y máximizar el panel izquierdo
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
     * Método que permite realizar la lógica de minimizar y máximizar el panel derecho
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de video", "*.mp4", "*.avi", "*.mkv"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            addVideoToLibrary(selectedFile);
        }
    }

    // Método para agregar el video a la lista
    private void addVideoToLibrary(File videoFile) {
        if (videoFile != null) {
            videoListView.getItems().add(videoFile.getAbsolutePath()); // Guardamos la ruta del video
        }
    }

    // Método para reproducir un video
    private void playVideo(String videoPath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Detener cualquier video en reproducción
        }

        Media media = new Media(new File(videoPath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.play(); // Iniciar la reproducción
    }

}

