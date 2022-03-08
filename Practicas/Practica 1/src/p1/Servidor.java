package p1;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static String carpetaServidor;
    public int puerto;
    public Socket cl;
    public ServerSocket servidor;
    public String rutaArchivos;
    public DataInputStream dis;
    public DataOutputStream dos;
    
    public Servidor(int pto){
        puerto = pto;
    }
    public static void main(String[] args) throws IOException{   
        Servidor s = new Servidor(8000);
        s.iniciarServidor();
    }     
    public void crearCarpeta(String url){
        System.out.println("ruta: "+url);
        rutaArchivos = url;
        File f2 = new File(rutaArchivos);
        f2.mkdirs();
        f2.setWritable(true);
    }
    public void iniciarServidor(){
        try{
            servidor = new ServerSocket(puerto);
            servidor.setReuseAddress(true);
            System.out.println("Servidor iniciado.\n\n");
            servidorEsperando();
        }catch(IOException e){
            System.err.println(e);
        }
    }
    public void servidorEsperando(){
        try {
            for (;;) {
                cl = servidor.accept();
                System.out.println("Cliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());
                dis = new DataInputStream(cl.getInputStream());
                crearCarpeta(dis.readUTF());
                String nombre = dis.readUTF();
                long tam = dis.readLong();
                System.out.println("Comienza subida del archivo: " + nombre + " de tamaño: " + tam);
                dos = new DataOutputStream(new FileOutputStream(rutaArchivos+"\\"+nombre));
                escribirArchivos(tam);
            }
        } catch (IOException e) {
            System.err.println("Fallo en el servidor "+e);
        }
    }
    public void escribirArchivos(long tam) {
        long recibidos = 0;
        int l = 0, porcentaje = 0;
        try {
            while (recibidos < tam) {
                byte[] b = new byte[1500];
                l = dis.read(b);
                System.out.println("leidos: " + l);
                dos.write(b, 0, l);
                dos.flush();
                recibidos = recibidos + l;
                porcentaje = (int) ((recibidos * 100) / tam);
                System.out.println("Recibido el " + porcentaje + " % del archivo");
            }
            System.out.println("Archivo recibido.\n\n");
            dis.close();
            dos.close();
            cl.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public void enviarArchivos(File[] archivos, String ruta){
        CCliente c = new CCliente(ruta);
        try{
            for (File archivo : archivos) {
                String nombreFichero = archivo.getName();
                if (archivo.isDirectory()) {
                    System.out.println("El fichero: " + archivo.getName() + " es una carpeta");
                    File[] documentosCarpeta = archivo.listFiles();
                    c.enviarCarpeta(documentosCarpeta, nombreFichero);
                    System.out.println("");
                } else {
                    System.out.println("El fichero: " + archivo.getName() + " es un archivo");
                    c.iniciarCliente();
                    String path = archivo.getAbsolutePath();
                    long tam = archivo.length();
                    System.out.println("Enviando archivo de: "+path+" de tamaño: "+tam);
                    c.enviarDatos(nombreFichero, path, tam);
                    c.escribirFichero(tam);
                    System.out.println("");
                }
            }
        }catch(Exception e){
            System.out.println("Algo pasa: "+e);
        }
    }
}