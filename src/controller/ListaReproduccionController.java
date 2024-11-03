package controller;

import model.Cancion;
import model.ListaReproduccion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ListaReproduccionController {

    private List<ListaReproduccion> listasReproduccion;
    private String directorioListas;  // Directorio donde se guardarán las listas

    public ListaReproduccionController(String directorioListas) {
        this.listasReproduccion = new ArrayList<>();
        this.directorioListas = directorioListas;
        crearDirectorioSiNoExiste();
        cargarListasDesdeDirectorio();
    }

    // Metodo para crear el directorio si no existe
    private void crearDirectorioSiNoExiste() {
        File carpeta = new File(directorioListas);
        if (!carpeta.exists()) {
            if (carpeta.mkdirs()) {
                System.out.println("Directorio de listas creado: " + directorioListas);
            } else {
                System.err.println("Error al crear el directorio de listas: " + directorioListas);
            }
        }
    }

    // Metodo para leer las listas de reproducción desde archivos individuales en el directorio
    private void cargarListasDesdeDirectorio() {
        File carpeta = new File(directorioListas);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.err.println("El directorio de listas de reproducción no existe o no es valido: " + directorioListas);
            return;
        }

        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".txt"));  // Solo archivos .txt
        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron listas de reproduccion.");
            return;
        }

        for (File archivo : archivos) {
            cargarListaDesdeArchivo(archivo);
        }
    }

    // Metodo para cargar una lista de reproducción desde un archivo de texto
    private void cargarListaDesdeArchivo(File archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String nombreLista = archivo.getName().replace(".txt", "");
            ListaReproduccion listaActual = new ListaReproduccion(nombreLista);

            String linea;
            while ((linea = br.readLine()) != null) {
                String nombreCancion = linea.trim();
                String rutaCancion = br.readLine();
                if (rutaCancion != null) {
                    Cancion cancion = new Cancion(nombreCancion, rutaCancion.trim());
                    listaActual.agregarCancion(cancion);
                }
            }
            listasReproduccion.add(listaActual);

        } catch (IOException e) {
            System.err.println("Error al leer la lista de reproduccion: " + archivo.getName());
        }
    }

    // Metodo para guardar una lista de reproducción en un archivo independiente
    private void guardarListaEnArchivo(ListaReproduccion lista) {
        File archivoLista = new File(directorioListas, lista.getNombre() + ".txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoLista))) {
            for (Cancion cancion : lista.getCanciones()) {
                bw.write(cancion.getNombre());  // Escribimos el nombre de la cancion
                bw.newLine();
                bw.write(cancion.getRuta());    // Escribimos la ruta de la cancion
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir la lista de reproduccion: " + lista.getNombre());
        }
    }

    // Metodo para crear una nueva lista de reproduccion y guardarla en un archivo
    public void crearLista(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            ListaReproduccion nuevaLista = new ListaReproduccion(nombre);
            listasReproduccion.add(nuevaLista);
            guardarListaEnArchivo(nuevaLista);
        } else {
            System.err.println("El nombre de la lista de reproduccion no es valido.");
        }
    }

    // Metodo para eliminar una lista de reproduccion por su nombre (y su archivo correspondiente)
    public void eliminarLista(String nombre) {
        ListaReproduccion listaAEliminar = obtenerListaPorNombre(nombre);
        if (listaAEliminar != null) {
            listasReproduccion.remove(listaAEliminar);

            // Eliminar el archivo asociado
            File archivoLista = new File(directorioListas, nombre + ".txt");
            if (archivoLista.exists()) {
                if (archivoLista.delete()) {
                    System.out.println("Archivo de la lista de reproduccion eliminado: " + nombre);
                } else {
                    System.err.println("Error al eliminar el archivo de la lista de reproduccion: " + nombre);
                }
            }
        } else {
            System.err.println("Lista de reproduccion no encontrada: " + nombre);
        }
    }

    // Metodo para agregar una cancion a una lista de reproduccion y actualizar su archivo
    public void agregarCancionALista(Cancion cancion, String nombreLista) {
        ListaReproduccion lista = obtenerListaPorNombre(nombreLista);
        if (lista != null) {
            lista.agregarCancion(cancion);
            guardarListaEnArchivo(lista);  // Actualizar el archivo de la lista
        } else {
            System.err.println("Lista de reproduccion no encontrada: " + nombreLista);
        }
    }

    // Metodo para eliminar una cancion de una lista de reproduccion y actualizar su archivo
    public void eliminarCancionDeLista(Cancion cancion, String nombreLista) {
        ListaReproduccion lista = obtenerListaPorNombre(nombreLista);
        if (lista != null) {
            lista.eliminarCancion(cancion);
            guardarListaEnArchivo(lista);  // Actualizar el archivo de la lista
        } else {
            System.err.println("Lista de reproduccion no encontrada: " + nombreLista);
        }
    }

    // Metodo para obtener los nombres de las listas de reproducción
    public List<String> obtenerNombresListasReproduccion() {
        List<String> nombres = new ArrayList<>();
        for (ListaReproduccion lista : listasReproduccion) {
            nombres.add(lista.getNombre());
        }
        return nombres;
    }

    // Metodo para obtener una lista de reproducción por su nombre
    public ListaReproduccion obtenerListaPorNombre(String nombre) {
        for (ListaReproduccion lista : listasReproduccion) {
            if (lista.getNombre().equals(nombre)) {
                return lista;
            }
        }
        return null;
    }
}
