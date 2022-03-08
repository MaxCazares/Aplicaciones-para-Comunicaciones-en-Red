/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mensajeria;

import java.nio.channels.SocketChannel;

/**
 *
 * @author Christian
 */
public class Users {
    String nombre;
    SocketChannel cliente;
    
    Users(String nom, SocketChannel host)
    {
        nombre=nom;
        cliente=host;
    }
}
