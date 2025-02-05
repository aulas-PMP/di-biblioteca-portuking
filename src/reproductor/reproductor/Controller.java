package reproductor;


import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    /**Lista para almacenar los archivos */
    @FXML
    private ListView<String> videoListView;
    /**Botón para añadir vídeo */
    @FXML
    private Button btnAddVideo;
    /**Botón de play */
    @FXML
    private Button playButton;
    /**Botón de pausa */
    @FXML
    private Button pauseButton;
    /**Botón de stop */
    @FXML
    private Button stopButton;
    /**Contenedor para reproducir el video */
    @FXML
    private MediaView mediaView;
    /**Slider para la barra de sonido */
    @FXML
    private Slider volumeSlider;
    /**Botón para cambiar tamaño del video */
    @FXML
    private Button btnChangeSize;
    /**Contenedor del MediaView */
    @FXML
    private StackPane mediaPlayerContainer;
    /**Botón para cambiar la velocidad */
    @FXML
    private Button btnChangeSpeed;
    /**Slider para el progreso del vídeo */
    @FXML
    private Slider progressSlider;
    /**Imagen de fondo para los audios */
    @FXML
    private ImageView audioBackgroundImage;
    /**Botón de Add del menu superior */
    @FXML
    private MenuItem menuAdd;
    /**Botón de borrar biblioteca */
    @FXML
    private MenuItem menuDelete;
    /**Botón de acerca de */
    @FXML
    private MenuItem menuAbout;
    /** Opciones de velocidad */
    private double[] speedOptions = {0.5, 1.0, 1.5, 2.0}; 
    /**Índice inicial (1.0x velocidad normal) */
    private int currentSpeedIndex = 1;
    /** Estado inicial de la barra izquierda: la barra está visible */
    private boolean isLeftPanelVisible = true;
    /** Estado inicial de la barra derecha: la barra está visible */
    private boolean isRightPanelVisible = true;
    /**Contenedor para la reproducción */
    private MediaPlayer mediaPlayer;
    /**Estado del tamaño del video */
    private boolean isFullSize = false;

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

        progressSlider.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                double newTime = (progressSlider.getValue() / 100) * mediaPlayer.getTotalDuration().toSeconds();
                mediaPlayer.seek(Duration.seconds(newTime));
            }
        });

        Image audioImage = new Image(getClass().getResource("/reproductor/rolando.jpeg").toExternalForm());
        audioBackgroundImage.setImage(audioImage);

        menuAdd.setOnAction(event -> openFileChooser());
        menuDelete.setOnAction(event -> clearLibrary());
        menuAbout.setOnAction(event -> showInfo());
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

    /**
     * Método para abrir el explorador de archivos y seleccionar un video
     */
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
            "Archivos Multimedia", "*.mp4", "*.avi", "*.mkv", "*.mp3", "*.wav"));
    
        File selectedFile = fileChooser.showOpenDialog(null);
    
        if (selectedFile != null) {
            addMediaToLibrary(selectedFile);
        }
    }
    

    /**
     * Método para agregar el video a la lista con título y duración
     * @param mediaFile archivo a agregar
     */
    private void addMediaToLibrary(File mediaFile) {
    if (mediaFile != null) {
        String fileName = mediaFile.getName(); // Obtiene solo el nombre del archivo
        String filePath = mediaFile.getAbsolutePath();
        String durationText = getMediaDuration(filePath); // Obtiene la duración

        // Agrega el video a la biblioteca con formato "Título (Duración)"
        videoListView.getItems().add(fileName + " (" + durationText + ") | " + filePath);
    }
    }

    /**
     * Método para obtener la duración del video en formato "mm:ss"
     * @param mediaPath ruta del archivo
     * @return duración formateada
     */
    private String getMediaDuration(String mediaPath) {
        try {
            Media media = new Media(new File(mediaPath).toURI().toString());
            MediaPlayer tempMediaPlayer = new MediaPlayer(media);

            // Usa un temporizador para esperar a que se cargue la duración
            tempMediaPlayer.setOnReady(() -> {
            double totalSeconds = media.getDuration().toSeconds();
            int minutes = (int) (totalSeconds / 60);
            int seconds = (int) (totalSeconds % 60);

            // Formatea la duración como "mm:ss"
            String formattedDuration = String.format("%02d:%02d", minutes, seconds);
            tempMediaPlayer.dispose(); // Liberar recursos

            // Actualizar el ListView con la duración corregida
            for (int i = 0; i < videoListView.getItems().size(); i++) {
                String item = videoListView.getItems().get(i);
                if (item.contains(mediaPath)) {
                    videoListView.getItems().set(i, item.replace("(00:00)", "(" + formattedDuration + ")"));
                }
            }
        });

        return "00:00"; // Mientras se obtiene la duración real, muestra "00:00"
    } catch (Exception e) {
        return "Desconocido";
    }
}

    

    /**
     * Método para reproducir un video
     * @param mediaPath path del vídeo
     */
    private void playMedia(String mediaPath) {

        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Detener cualquier reproducción en curso
        }
    
        Media media = new Media(new File(mediaPath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    
        if (mediaPath.endsWith(".mp3") || mediaPath.endsWith(".wav")) {
            // Es un archivo de audio → Ocultamos MediaView y solo reproducimos el audio
            mediaView.setVisible(false);
            audioBackgroundImage.setVisible(true);
        } else {
            // Es un archivo de video → Mostramos el MediaView
            mediaView.setMediaPlayer(mediaPlayer);
            mediaView.setVisible(true);
            audioBackgroundImage.setVisible(false);
        }

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double progress = (newValue.toSeconds() / mediaPlayer.getTotalDuration().toSeconds()) * 100;
            progressSlider.setValue(progress);
        });
    
        mediaPlayer.play();
    }
    

    /**
     * Método para iniciar la reproducción del video
     */
    private void playMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    /**
     * Método para pausar el video
     */
    private void pauseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Método para detener el video y reiniciar la posición
     */
    private void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Método que permite cambiar el tamaño del vídeo
     */
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

    /**
     * Método que permite cambiar la velocidad del vídeo
     */
    private void changeVideoSpeed() {
        if (mediaPlayer != null) {
            currentSpeedIndex = (currentSpeedIndex + 1) % speedOptions.length; // Cambiar al siguiente índice
            double newSpeed = speedOptions[currentSpeedIndex]; // Obtener la nueva velocidad
            mediaPlayer.setRate(newSpeed); // Aplicar la velocidad al MediaPlayer
            btnChangeSpeed.setText("Velocidad: " + newSpeed + "x"); // Actualizar texto del botón
        }
    }


    /**
     * Método para borrar los archivos de la biblioteca (Opción "Biblioteca" -> "Eliminar archivos")
     */
    private void clearLibrary() {
        videoListView.getItems().clear();
    }

    /**
     * Método para mostrar un mensaje cuando se elige "Ver" -> "Información"
     */
    private void showInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información del Reproductor");
        alert.setHeaderText("Reproductor Multimedia");
        alert.setContentText("A este reproductor deberías ponerle un 10!!!!");

    alert.showAndWait();
    }

}
