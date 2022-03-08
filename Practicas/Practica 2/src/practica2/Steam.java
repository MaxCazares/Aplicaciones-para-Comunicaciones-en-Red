/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
/**
 *
 * @author Christian Iván
 */
public class Steam {
    public static void main(String[] args) {
        try{
	ServerSocket ss = new ServerSocket(3000);
        ss.setReuseAddress(true);
	System.out.println("Servidor iniciado");
        ArrayList<Producto> catalogo= new ArrayList <>(); 
        ArrayList<Producto> cata;
        Calendar p1i= Calendar.getInstance();
        Calendar p2i= Calendar.getInstance();
        Calendar p3i= Calendar.getInstance();
        Calendar p1f= Calendar.getInstance();
        Calendar p2f= Calendar.getInstance();
        Calendar p3f= Calendar.getInstance();
        p1i.set(2020, 11, 12);
        p2i.set(2020, 10, 7);
        p3i.set(2020, 9, 10);
        p1f.set(2020, 11, 30);
        p2f.set(2020, 11, 10);
        p3f.set(2020, 11, 15);
        Producto j1= new Producto("01","Helltaker",30,"C:\\Users\\Christian Iván\\Pictures\\fanart\\juegos\\hell.jpg",34,20,p1i,p1f);
        Producto j2= new Producto("02","Skullgirls",500,"C:\\Users\\Christian Iván\\Pictures\\fanart\\juegos\\skull.jpg",40,100,p2i,p2f);
        Producto j3= new Producto("03","Among us",50,"C:\\Users\\Christian Iván\\Pictures\\fanart\\juegos\\among.jpg",100,20,p1i,p1f);
        Producto j4= new Producto("04","The witcher 3",1600,"C:\\Users\\Christian Iván\\Pictures\\fanart\\juegos\\witcher.jpg",10,500,p3i,p3f);
        Producto j5= new Producto("05","Undertale",100,"C:\\Users\\Christian Iván\\Pictures\\fanart\\juegos\\undertale.jpg",50,20,p1i,p1f);
        catalogo.add(j1);
        catalogo.add(j2);
        catalogo.add(j3);
        catalogo.add(j4);
        catalogo.add(j5);
        for(;;){
            Socket cl = ss.accept();
            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
            oos.writeObject(catalogo);
            oos.flush();
            System.out.println("Cliente conectado.. Enviando el catalogo\n:");
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            cata = (ArrayList<Producto>)ois.readObject();
            //System.out.println("\nuu\n");
            for(int c=0;c<5;c++)
            {
                catalogo.set(c, cata.get(c));
            }
            System.out.println("Catalogo recibido de vuelta");
            ois.close();
            oos.close();
            cl.close();
        }//for   
    }catch(Exception e){
        e.printStackTrace();
    }
    }
}
