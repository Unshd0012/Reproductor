package model;

import java.util.ArrayList;
import java.util.List;

public class ListaReproduccion {
    private String nombre;
    private List<Cancion> canciones;

    public ListaReproduccion(String nombre) {
        this.nombre = nombre;
        this.canciones = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void agregarCancion(Cancion cancion) {
        canciones.add(cancion);
    }

    public void eliminarCancion(Cancion cancion) {
        canciones.remove(cancion);
    }
}
