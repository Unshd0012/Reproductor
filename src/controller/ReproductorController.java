package controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.Cancion;
import model.ListaReproduccion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Duration;
import view.Ecualizador;

public class ReproductorController {

    private ListaReproduccion listaActual;
    private int indiceActual;
    private MediaPlayer mediaPlayer;
    private boolean estaPausado;
    private final Ecualizador ecu;
    private final InterReproductor interfazReproductor;

    public ReproductorController(Ecualizador ecu, InterReproductor interReproductor) {
        this.listaActual = null;
        this.indiceActual = 0;
        this.mediaPlayer = null;
        this.estaPausado = false;
        this.ecu = ecu;
        this.interfazReproductor = interReproductor;
    }

    // Metodo para reproducir una lista de reproduccion
    public void reproducirLista(ListaReproduccion lista) {
        if (lista == null || lista.getCanciones().isEmpty()) {
            System.err.println("La lista de reproduccion esta vacia o es nula.");
            return;
        }

        detenerReproduccionActual();

        this.listaActual = lista;

        reproducirCancionActual();
    }

    public List<String> obtenerNombresListasReproduccion() {
        List<String> canciones = new ArrayList<>();

        for (Cancion cancion : listaActual.getCanciones()) {
            canciones.add(cancion.getRuta());
        }
        return canciones;
    }

    // Metodo para reproducir la cancion actual
    private void reproducirCancionActual() {
        if (listaActual == null || listaActual.getCanciones().isEmpty()) {
            return;
        }

        Cancion cancion = listaActual.getCanciones().get(indiceActual);
        String ruta = cancion.getRuta();
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            System.err.println("Archivo de cancion no encontrado: " + ruta);
            siguienteCancion(); // Intentar reproducir la siguiente cancion
            return;
        }

        try {
            Media media = new Media(archivo.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();

            mediaPlayer.currentTimeProperty().addListener((obs, oldValue, newValue) -> {

                this.interfazReproductor.timeReproduction(newValue.toSeconds());
            });

            mediaPlayer.setOnReady(() -> {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                this.interfazReproductor.timeDuration(totalDuration.toSeconds());
            });
            mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
                ecu.updateBars(magnitudes); // Usa un arreglo que almacena la ultima magnitud detectada
            });
            ecu.reload();
            // Listener para cuando la cancion termine
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Reproduccion finalizada para: " + cancion.getNombre());
                this.interfazReproductor.finishAudio();
            });

            System.out.println("Reproduciendo: " + cancion.getNombre());
            this.interfazReproductor.getCancion(cancion);
            // Listener para cuando la cancion termine
            mediaPlayer.setOnEndOfMedia(() -> siguienteCancion());

        } catch (Exception e) {
            System.err.println("Error al reproducir la cancion: " + e.getMessage());
            siguienteCancion(); // Intentar reproducir la siguiente cancion
        }
    }

    public void reproducirCancion(Cancion cancion) {
        if (cancion == null) {
            System.err.println("La cancion es nula, no se puede reproducir.");
            return;
        }

        detenerReproduccionActual();  // Detener cualquier reproduccion en curso antes de iniciar una nueva

        String ruta = cancion.getRuta();
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            System.err.println("Archivo de cancion no encontrado: " + ruta);
            return;
        }

        try {
            Media media = new Media(archivo.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            mediaPlayer.setAudioSpectrumInterval(0.2); // Cada 0.2 segundos
            mediaPlayer.setAudioSpectrumNumBands(100);
            mediaPlayer.setVolume(10);
            System.out.println("Reproduciendo: " + cancion.getNombre());
            this.interfazReproductor.getCancion(cancion);
            mediaPlayer.currentTimeProperty().addListener((obs, oldValue, newValue) -> {
                //System.out.println("Tiempo actual de reproducción: " + newValue.toSeconds() + " segundos");

                this.interfazReproductor.timeReproduction(newValue.toSeconds());
            });

            mediaPlayer.setOnReady(() -> {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                this.interfazReproductor.timeDuration(totalDuration.toSeconds());
            });

            mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
                ecu.updateBars(magnitudes);
            });

            ecu.reload();

            // Listener para cuando la canción termine
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Reproducción finalizada para: " + cancion.getNombre());
                /* ecu.setPlay(false);
            ecu.limpiar();*/
                this.interfazReproductor.finishAudio();
            });

        } catch (Exception e) {
            System.err.println("Error al reproducir la cancion: " + e.getMessage());

        }
    }

    // Metodo para pausar la reproducción
    public void pausarReproduccion() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            estaPausado = true;
            System.out.println("Reproduccion pausada.");

        }
    }

    // Metodo para reanudar la reproducción
    public void reanudarReproduccion() {
        if (mediaPlayer != null && estaPausado) {
            mediaPlayer.play();
            estaPausado = false;
            System.out.println("Reproduccion reanudada.");

        }
    }

    // Metodo para detener la reproduccion actual
    public void detenerReproduccionActual() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            System.out.println("Reproduccion detenida.");
            this.interfazReproductor.finishAudio();

        }
        estaPausado = false;
        this.indiceActual = 0;
    }

    // Metodo para reproducir la siguiente cancion
    public void siguienteCancion() {
        if (listaActual == null || listaActual.getCanciones().isEmpty()) {
            return;
        }

        indiceActual++;
        if (indiceActual >= listaActual.getCanciones().size()) {
            // indiceActual = 0;
            detenerReproduccionActual();
        } else {
            reproducirCancionActual();
        }
    }

    // Metodo para reproducir la canción anterior
    public void anteriorCancion() {
        if (listaActual == null || listaActual.getCanciones().isEmpty()) {
            return;
        }

        indiceActual--;
        if (indiceActual < 0) {
            indiceActual = listaActual.getCanciones().size() - 1; // Ciclar al final
        }
        reproducirCancionActual();
    }

    // Metodo para reproducir o reanudar la reproduccion
    public void reproducir() {
        if (mediaPlayer != null) {
            if (estaPausado) {
                reanudarReproduccion();
            } else {
                // Si ya esta reproduciendo, no hacer nada o reiniciar
                System.out.println("Ya hay una canción en reproduccion.");
            }
        } else if (listaActual != null) {
            reproducirCancionActual();
        } else {
            System.err.println("No hay ninguna lista de reproduccion seleccionada.");
        }
    }
}
