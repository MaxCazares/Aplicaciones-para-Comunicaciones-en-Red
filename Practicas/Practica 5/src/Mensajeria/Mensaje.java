/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mensajeria;

import java.io.Serializable;

/**
 *
 * @author Christian
 */
public class Mensaje implements Serializable{
    String nombre;
    int tipo;
    String mensaje;
    String destino;
    
    Mensaje(String n, int t, String m,String Des)
    {
        nombre=n;
        tipo=t;
        mensaje=m;
        destino=Des;
    }
    
    public String getnombre()
    {
        return nombre;
    }
    
    
    public int gettipo()
    {
        return tipo;
    }
    
    public String getmensaje()
    {
        return mensaje;
    }
    
    public String getDes()
    {
        return destino;
    }
}
