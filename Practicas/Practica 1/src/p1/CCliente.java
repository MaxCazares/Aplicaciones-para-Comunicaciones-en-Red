package p1;
import java.io.*;
import java.net.Socket;
import javax.swing.JFileChooser;
public class CCliente {
    public int puerto;
    public String localhost;
    public String ruta;
    public File[] archivos;
    public Socket cl;
    public DataOutputStream dos; 
    public DataInputStream dis;
    
    public CCliente(String ruta){
        puerto = 8000;
        localhost = "127.0.0.1";
        this.ruta = ruta;
    }
    public File[] getFicheros(){
        return archivos;
    }
    public void iniciarCliente(){
        try{
            cl = new Socket(localhost, puerto);
            System.out.println("\nCliente conectado con el servidor.");
            cl.setReuseAddress(true);
        }catch(IOException e){
            System.out.println("Fallo al iniciar el cliente: "+e);
        }
    }
    public void seleccionaArchivos(){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setMultiSelectionEnabled(true);
        int r = fc.showOpenDialog(null);
        if(r == JFileChooser.APPROVE_OPTION){
            archivos = fc.getSelectedFiles();
        }
        
    }
    public void enviarArchivos(){
        try{
            for (File archivo : archivos) {
                String nombreFichero = archivo.getName();
                if (archivo.isDirectory()) {
                    System.out.println("El fichero: " + archivo.getName() + " es una carpeta");
                    File[] documentosCarpeta = archivo.listFiles();
                    enviarCarpeta(documentosCarpeta, nombreFichero);
                    System.out.println("");
                } else {
                    System.out.println("El fichero: " + archivo.getName() + " es un archivo");
                    iniciarCliente();
                    String path = archivo.getAbsolutePath();
                    long tam = archivo.length();
                    System.out.println("Enviando archivo de: "+path+" de tamaño: "+tam);
                    enviarDatos(nombreFichero, path, tam);
                    escribirFichero(tam);
                    System.out.println("");
                }
            }
        }catch(Exception e){
            System.out.println("Algo pasa al enviar los archivos: "+e);
        }
    }
    public void enviarCarpeta(File[] documentos, String nombreCarpeta){
        for (File documento : documentos) {
            if (documento.isDirectory()) {
                enviarCarpeta(documento.listFiles(), nombreCarpeta+"\\"+documento.getName());
                System.out.println("Carpeta: " + documento.getName() + " dento de: " + nombreCarpeta + " se ha enviado");
            } else {
                iniciarCliente();
                System.out.println("Preparandose para enviar: " + documento.getName() + " de la ruta: " + documento.getAbsolutePath() + " de tamaño: " + documento.length());
                enviarDatosCarpeta(documento.getName(), documento.getAbsolutePath(), nombreCarpeta, documento.length());
                escribirFichero(documento.length());
            }
        }
    }
    public void enviarDatos(String nombre, String path, long tam){
        try{
            dos = new DataOutputStream(cl.getOutputStream());
            dis = new DataInputStream(new FileInputStream(path));
            dos.writeUTF(ruta);
            dos.flush();
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
        }catch(IOException e){
            System.err.println("Fallo en enviarDatos: "+e);
        }
    }
    public void enviarDatosCarpeta(String nombreArchivo, String path, String nombreCarpeta, long tam){
        try{
            dos = new DataOutputStream(cl.getOutputStream());
            dis = new DataInputStream(new FileInputStream(path));
            dos.writeUTF(ruta+"\\"+nombreCarpeta);
            dos.flush();
            dos.writeUTF(nombreArchivo);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
        }catch(IOException e){
            System.err.println("Fallo en EnviarDatosCarpeta: "+e);
        }
    }
    public void escribirFichero(long tam){
        try{
            long enviados = 0;
            int l=0,porcentaje=0;
            while(enviados<tam){
                byte[] b = new byte[1500];
                l = dis.read(b);
                System.out.println("enviados: "+l);
                dos.write(b,0,l);
                dos.flush();
                enviados += l;
                porcentaje = (int)((enviados*100)/tam);
                System.out.print("\rEnviado el "+porcentaje+" % del archivo");
            }
            System.out.println("\nArchivo enviado.");
            dis.close();
            dos.close();
            cl.close();
        }catch(IOException e){
            System.err.println(e);
        }   
    }
    
    public void borrarDirectorio(File directorio){
        File[] ficheros = directorio.listFiles();
        for(int i=0; i<ficheros.length; i++){
            if (ficheros[i].isDirectory()){
                borrarDirectorio(ficheros[i]);
                ficheros[i].delete();
            }            
            else{
                ficheros[i].delete();
            }
        }
    }
    public void Borrar(){
        for (File archivo : archivos) {
            System.out.println("Archivo: " + archivo.getName() + " borrado");
            if (archivo.isDirectory()) {
                borrarDirectorio(archivo);
                archivo.delete();
            } else {
                archivo.delete();
            }
        }
    }
}