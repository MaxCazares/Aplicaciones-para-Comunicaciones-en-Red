package socketdatagrama;
import java.net.*;
import java.io.*;
public class Servidor {
    public static void main(String[] args) throws IOException{
        DatagramSocket s = new DatagramSocket(5555);
        System.out.println("Servidor esperando datagrama.");
        for (;;) {
            DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
            s.receive(p);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(p.getData()));
            int n = dis.readInt();
            int tam = dis.readInt();                
            byte[] b = new byte[tam];
            int x = dis.read(b);
            String cadena = new String(b);
            System.out.println("Paquete: " + n + " con longitud: "+x+" con datos: " + cadena);
            dis.close();
        }
    }    
}