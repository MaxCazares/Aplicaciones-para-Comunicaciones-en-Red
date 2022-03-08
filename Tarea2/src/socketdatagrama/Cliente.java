package socketdatagrama;
import java.net.*;
import java.io.*;
import java.util.Scanner;
public class Cliente {
    public static void main(String[] args)  throws IOException{
        Scanner keyboard = new Scanner(System.in);
        System.out.printf("\nEscribe un mensaje: ");
        String mensaje = keyboard.nextLine();
        byte[] tmp = mensaje.getBytes();
        int particion = 7;
        if(tmp.length <= particion){
            enviarDatagrama(tmp, 1);
        }else{
            int i, j;
            int lim = (int)(tmp.length / particion);
            for (i = 0, j = 1; j <= lim; i = i + particion, j++) {
                int b = j * particion;
                System.out.println(mensaje.substring(i, b));
                byte[] a = mensaje.substring(i , b).getBytes();
                enviarDatagrama(a,j);            
                System.out.println("");
            }
            if(tmp.length % particion != 0){             
                byte[] c = mensaje.substring(i, tmp.length).getBytes();
                System.out.println(mensaje.substring(i, tmp.length));
                enviarDatagrama(c,j);
            }
        }
    }
    public static void enviarDatagrama(byte[] tmp, int numPaquete) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(numPaquete); //escribe el numero de paquete
        dos.writeInt(tmp.length); //escribe la longitud del paquete     
        dos.write(tmp, 0, tmp.length); //escribe el paquete
        dos.flush(); 
        byte[] b = baos.toByteArray();
        DatagramSocket cl = new DatagramSocket();
        InetAddress dir = InetAddress.getByName("127.0.0.1");
        DatagramPacket p = new DatagramPacket(b, b.length, dir, 5555);
        cl.send(p);
        System.out.println("mensaje "+numPaquete+" enviado.");
        dos.close();
    }
}