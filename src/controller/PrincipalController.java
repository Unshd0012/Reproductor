
package controller;



import model.Cancion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PrincipalController {

    private List<Cancion> canciones;
    private String rutaArchivo;

    public PrincipalController(String rutaArchivo) {
        this.canciones = new ArrayList<>();
        this.rutaArchivo = rutaArchivo;
        cargarCancionesDesdeArchivo();
    }

    // Metodo para leer las canciones desde un archivo de texto
    private void cargarCancionesDesdeArchivo() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.err.println("El archivo de canciones no existe: " + rutaArchivo);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String nombre = linea.trim();
                String ruta = br.readLine();
                if (ruta != null) {
                    ruta = ruta.trim();
                    Cancion cancion = new Cancion(nombre, ruta);
                    canciones.add(cancion);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de canciones: " + e.getMessage());
        }
    }

    // Metodo para guardar todas las canciones en el archivo
    private void guardarCancionesEnArchivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (Cancion cancion : canciones) {
                bw.write(cancion.getNombre());
                bw.newLine();
                bw.write(cancion.getRuta());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de canciones: " + e.getMessage());
        }
    }

    // Metodo para obtener los nombres de las canciones para mostrar en la vista
    public List<String> obtenerNombresCanciones() {
        List<String> nombres = new ArrayList<>();
        for (Cancion cancion : canciones) {
            nombres.add(cancion.getNombre());
        }
        return nombres;
    }

    // Metodo para agregar una nueva cancion
    public void agregarCancion(String nombreCancion, String rutaCancion) {
        if (nombreCancion != null && !nombreCancion.trim().isEmpty() &&
            rutaCancion != null && !rutaCancion.trim().isEmpty()) {

            Cancion nuevaCancion = new Cancion(nombreCancion, rutaCancion);
            canciones.add(nuevaCancion);
            guardarCancionesEnArchivo();
        } else {
            System.err.println("Nombre o ruta de la cancion no validos.");
        }
    }

    // Metodo para eliminar una cancion por su nombre
    public void eliminarCancion(String nombreCancion) {
        boolean eliminada = canciones.removeIf(c -> c.getNombre().equals(nombreCancion));
        if (eliminada) {
            guardarCancionesEnArchivo();
        } else {
            System.err.println("Cancion no encontrada: " + nombreCancion);
        }
    }

    // Metodo para obtener una cancion por su nombre
    public Cancion obtenerCancionPorNombre(String nombreCancion) {
        for (Cancion cancion : canciones) {
            if (cancion.getNombre().equals(nombreCancion)) {
                return cancion;
            }
        }
        return null;
    }
}
