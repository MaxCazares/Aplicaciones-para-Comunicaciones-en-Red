package practica2;

import java.util.Date;
import java.io.*;
import java.util.Calendar;

class Producto implements Serializable{
    String ID;
    String nombre;
    float precio;
    int cantidadExistente;
    float promocion;
    Calendar inicioPromo;
    Calendar finPromo;
    String imagen;
    
    Producto(String id, String nombre, float precio, String imagen, int cantExis, float promocion, Calendar inicio, Calendar fin){
        ID = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        cantidadExistente = cantExis;
        this.promocion = promocion;
        inicioPromo = inicio;
        finPromo = fin;
    }
    
    public String getID() {
        return ID;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public float getPrecio() {
        return precio;
    }
    
    public float getPromo() {
        return promocion;
    }
    
    public Calendar getinicio() {
        return inicioPromo;
    }
    
    public Calendar getfinal() {
        return finPromo;
    }
    
    public String getImagen(){
        return imagen;
    }
}
