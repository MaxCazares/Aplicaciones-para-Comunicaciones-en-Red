package practica3;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorWeb {
    public static int puerto = 8000;
    public static void main(String[] args) throws IOException, Exception{
        ExecutorService pool = Executors.newFixedThreadPool(3);
        ServerSocket server = new ServerSocket(puerto);
        System.out.println("Escuchando por el puerto "+puerto);
        for(;;){
            try{
                Socket cliente = server.accept();
                Analizador  analisis = new Analizador(cliente);
                pool.execute(analisis);
            }catch(Exception e){
                System.err.println("Error en el pool: "+e);
            }
        }
    }    
}