package view;

import controller.InterReproductor;
import controller.PrincipalController;
import controller.ListaReproduccionController;
import controller.ReproductorController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Cancion;
import model.ListaReproduccion;

import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

public class PrincipalView implements InterReproductor {

    private PrincipalController principalController;
    private ListaReproduccionController listaReproduccionController;
    private final ReproductorController reproductorController;

    private ListView<String> listaVCanciones;   // Lista de canciones
    private ListView<String> listaVReproduccion;   // Lista de listas de reproduccion
    private TextField campoNuevaCancion;   // Campo para agregar una cancion
    private TextField campoNombreLista;   // Campo para agregar una nueva lista de reproduccion

    private Stage ventanaPrincipal;   // Ventana principal

    private final Ecualizador ecu;
    private Label rutaCanciones;
    // Botones de control de reproduccion
    private Button btnPlay;
    private Button btnPause;
    private Button btnNext;
    private Button btnPrevious;
    private Button btnStop;
    private Slider slideTimeReproduction;

    // Ruta temporal del archivo seleccionado
    private String listaSeleccionada;

    // Constructor que recibe la ruta del archivo de canciones
    public PrincipalView(String rutaArchivo) {
        this.principalController = new PrincipalController(rutaArchivo);
        this.listaReproduccionController = new ListaReproduccionController("listas" + File.separator); // Cambia esta ruta según tu estructura
        ecu = new Ecualizador();

        this.reproductorController = new ReproductorController(ecu, this);
    }

    public void inicializarComponentes() {
        ventanaPrincipal = new Stage();
        this.slideTimeReproduction = new Slider();
        slideTimeReproduction.setShowTickLabels(true);
        slideTimeReproduction.setShowTickMarks(true);
        slideTimeReproduction.setBlockIncrement(1);
        slideTimeReproduction.setMajorTickUnit(1);
        slideTimeReproduction.setMaxWidth(800);

        VBox vCanciones = new VBox(10);
        HBox hRutaCanciones = new HBox();
        rutaCanciones = new Label();
        hRutaCanciones.getChildren().addAll(rutaCanciones);

        VBox vContenedorLista = new VBox(10);
        vContenedorLista.setAlignment(Pos.CENTER);
        VBox vContenedorCanciones = new VBox(10);
        VBox vContenedorBotones = new VBox(10);

        Label labelListas = new Label("Listas de Reproduccion");
        labelListas.getStyleClass().add("label-title");
        HBox hSetAgregarLista = new HBox();
        Button btnCrearLista = new Button("Crear Nueva Lista");
        btnCrearLista.setOnAction(e -> crearListaReproduccion());
        campoNombreLista = new TextField();
        campoNombreLista.setPromptText("Nombre Lista");
        Button btnEliminarLista = new Button("Eliminar Lista");

        hSetAgregarLista.getChildren().addAll(btnCrearLista, campoNombreLista);

        Label labelCanciones = new Label("Canciones");
        labelCanciones.getStyleClass().add("label-title");
        HBox hSetAgregarCanciones = new HBox();
        Button btnAgregarCancionesLista = new Button("Agregar Canciones");
        Button btnEliminarCancion = new Button("Eliminar Cancion");
        btnEliminarCancion.setOnAction(event -> eliminarCancion());
        btnAgregarCancionesLista.setOnAction(e
                -> buscarArchivo()
        );
        campoNuevaCancion = new TextField();
        hSetAgregarCanciones.getChildren().addAll(btnAgregarCancionesLista, campoNuevaCancion);

        btnPlay = new Button("Play Lista Reproduccion");
        btnPlay.getStyleClass().add("elevated-button");
        btnPlay.setMinWidth(170);
        btnPlay.setOnAction(ev -> {
            reproducirLista(listaSeleccionada);
        });
        btnPause = new Button("Pause");
        btnPause.setMinWidth(170);
        btnPause.getStyleClass().add("elevated-button");
        btnPause.setOnAction(e -> pausar());
        btnPrevious = new Button("Previous");
        btnPrevious.setMinWidth(170);
        btnPrevious.getStyleClass().add("elevated-button");
        btnPrevious.setOnAction(e -> anterior());
        btnNext = new Button("Next");
        btnNext.setMinWidth(170);
        btnNext.getStyleClass().add("elevated-button");
        btnNext.setOnAction(event -> siguiente());
        btnStop = new Button("Stop");
        btnStop.setMinWidth(170);
        btnStop.getStyleClass().add("elevated-button");
        btnStop.setOnAction(e -> detener());

        listaVReproduccion = new ListView<>();
        listaVReproduccion.setOnMouseClicked(event -> {
            listaVCanciones.getItems().clear();
            String listaSeleccionada = listaVReproduccion.getSelectionModel().getSelectedItem() + ".txt";
            System.out.println("Seleccion : " + listaVReproduccion.getSelectionModel().getSelectedItem());
            this.principalController = new PrincipalController("listas" + File.separator + listaSeleccionada);
            listaVCanciones.getItems().addAll(this.principalController.obtenerNombresCanciones());
            this.listaReproduccionController = new ListaReproduccionController("listas" + File.separator);
            this.listaSeleccionada = listaVReproduccion.getSelectionModel().getSelectedItem();

        });
        listaVReproduccion.getItems().addAll(listaReproduccionController.obtenerNombresListasReproduccion());
        vContenedorLista.getChildren().addAll(labelListas, listaVReproduccion, hSetAgregarLista, btnEliminarLista);

        listaVCanciones = new ListView<>();
        listaVCanciones.setOnMouseClicked(event -> {
            String cancion = listaVCanciones.getSelectionModel().getSelectedItem();
            if (cancion != null) {
                reproducirCancion(cancion);

            }
        });

        vContenedorCanciones.getChildren().addAll(labelCanciones, listaVCanciones, hSetAgregarCanciones, btnEliminarCancion);

        vContenedorBotones.getChildren().addAll(btnPlay, btnPause, btnPrevious, btnNext, btnStop);

        HBox hboxListas = new HBox(10);
        hboxListas.getChildren().addAll(vContenedorLista, vContenedorCanciones, vContenedorBotones);

        vCanciones.getChildren().addAll(hboxListas);

        VBox vboxEcu = new VBox();
        vboxEcu.setAlignment(Pos.CENTER_LEFT);
        vboxEcu.getChildren().addAll(ecu, slideTimeReproduction);

        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(vCanciones);
        borderPane.setTop(vboxEcu);
        borderPane.setBottom(hRutaCanciones);

        // Configuración de la escena y la ventana
        Scene escena = new Scene(borderPane, 800, 600);
        escena.getStylesheets().add(PrincipalView.class.getResource("/res/style.css").toExternalForm());
        ventanaPrincipal.setScene(escena);
        ventanaPrincipal.setResizable(false);
        ventanaPrincipal.setTitle("Reproductor de Musica");

    }

    public Stage getStage() {
        return ventanaPrincipal;
    }

    // Metodo para buscar la ruta de la canción usando FileChooser
    private void buscarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo de Canción");

        // Configurar filtros para mostrar solo archivos de musica
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Archivos de Musica", "*.mp3", "*.wav");
        fileChooser.getExtensionFilters().add(filter);

        // Establecer carpeta inicial en la carpeta de musica del usuario
        String carpetaMusica = System.getProperty("user.home") + "/Music";
        File directorioInicial = new File(carpetaMusica);
        if (directorioInicial.exists()) {
            fileChooser.setInitialDirectory(directorioInicial);
        }

        // Mostrar el diálogo de selecció¿on de archivo
        File archivoSeleccionado = fileChooser.showOpenDialog(ventanaPrincipal);
        if (archivoSeleccionado != null) {
            campoNuevaCancion.setText(archivoSeleccionado.getAbsolutePath());  // Muestra la ruta en el campo de texto
            agregarCancion();
        }
    }

    // Metodo para agregar una nueva cancion (nombre y ruta)
    public void agregarCancion() {
        String rutaCancion = campoNuevaCancion.getText();
        System.out.println("Ruta de la cancion: " + rutaCancion);
        if (rutaCancion != null && !rutaCancion.trim().isEmpty()) {
            File archivoCancion = new File(rutaCancion);
            String nombreCancion = archivoCancion.getName();
            if (nombreCancion.contains(".")) {
                nombreCancion = nombreCancion.substring(0, nombreCancion.lastIndexOf('.'));
            }

            principalController.agregarCancion(nombreCancion, rutaCancion);
            cargarCancionesEnLista();  // Actualizar la vista de la lista
            campoNuevaCancion.clear(); // Limpiar el campo de texto
        }
    }

    // Metodo para eliminar una cancion seleccionada de la lista
    public void eliminarCancion() {
        String cancionSeleccionada = listaVCanciones.getSelectionModel().getSelectedItem();
        if (cancionSeleccionada != null) {
            principalController.eliminarCancion(cancionSeleccionada);
            cargarCancionesEnLista();  // Actualizar la vista de la lista
        }
    }

    // Metodo para cargar las canciones en la ListView
    private void cargarCancionesEnLista() {
        listaVCanciones.getItems().clear();
        listaVCanciones.getItems().addAll(principalController.obtenerNombresCanciones());
    }

    // Metodos para gestionar listas de reproduccion
    // Crear una nueva lista de reproduccion
    public void crearListaReproduccion() {
        String nombreLista = campoNombreLista.getText();
        if (nombreLista != null && !nombreLista.trim().isEmpty()) {
            listaReproduccionController.crearLista(nombreLista);
            cargarListasReproduccionEnVista();  // Actualizar la vista de listas
            campoNombreLista.clear();           // Limpiar el campo de texto
        }
    }

    // Eliminar una lista de reproduccion seleccionada
    public void eliminarListaReproduccion() {
        String listaSeleccionada = listaVReproduccion.getSelectionModel().getSelectedItem();
        if (listaSeleccionada != null) {
            listaReproduccionController.eliminarLista(listaSeleccionada);
            cargarListasReproduccionEnVista();  // Actualizar la vista de listas
        }
    }

    // Metodo para cargar las listas de reproduccion en la ListView
    private void cargarListasReproduccionEnVista() {
        listaVReproduccion.getItems().clear();
        listaVReproduccion.getItems().addAll(listaReproduccionController.obtenerNombresListasReproduccion());
    }

    // Metodos de control de reproduccion
    // Reproducir la lista de reproduccion seleccionada
    private void reproducirLista(String listaSeleccionada) {
        System.out.println("La lista tiene : " + listaSeleccionada);

        if (listaSeleccionada != null) {
            ListaReproduccion lista = listaReproduccionController.obtenerListaPorNombre(listaSeleccionada);

            if (lista != null) {
                System.out.println("Reproducciendo lista"
                );
                reproductorController.reproducirLista(lista);
            }
        }
    }

    private void reproducirCancion(String cancionSeleccionada) {
        // Obtener la cancion seleccionada de la lista de canciones

        if (cancionSeleccionada != null) {
            // Obtener la cancion por su nombre desde el controlador de canciones
            Cancion cancion = principalController.obtenerCancionPorNombre(cancionSeleccionada);

            if (cancion != null) {

                reproductorController.reproducirCancion(cancion);
                System.out.println("Reproduciendo cancion: " + cancion.getNombre());

                rutaCanciones.setText(cancion.getRuta());
            } else {
                System.err.println("Cancion no encontrada en la lista.");

            }
        } else {
            System.err.println("No se ha seleccionado ninguna cancion.");

        }
    }

    // Pausar la reproduccion actual
    private void pausar() {
        reproductorController.pausarReproduccion();
    }

    private void reproducir() {
        finishAudio();
        reproductorController.reproducir();
    }

    // Reproducir la siguiente cancion
    private void siguiente() {
        finishAudio();
        reproductorController.siguienteCancion();
    }

    // Reproducir la canción anterior
    private void anterior() {
        finishAudio();
        reproductorController.anteriorCancion();
    }

    private void detener() {
        reproductorController.detenerReproduccionActual();
        finishAudio();
    }

    
    public void mostrarListaCanciones() {
        // Metodo que se llamará cuando sea necesario mostrar la lista
        cargarCancionesEnLista();
    }

    @Override
    public void timeReproduction(double time) {
        slideTimeReproduction.setValue(time);
    }

    @Override
    public void timeDuration(double duration) {
        slideTimeReproduction.setMin(0);
        slideTimeReproduction.setMax(Math.round(duration));
    }

    @Override
    public void finishAudio() {
        slideTimeReproduction.setValue(0);
        ecu.limpiar();

    }

    @Override
    public void getCancion(Cancion cancion) {
        listaVCanciones.getSelectionModel().select(cancion.getNombre());
    }

}
