import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class p4 implements Runnable {
    protected int port = 8000;
    protected boolean stop = false;
    protected Thread runningThread = null;
    protected ExecutorService pool = Executors.newFixedThreadPool(100);
    protected String dominio;
    protected String path;
    protected String posicion;
    
    public p4(int port, String dominio, String path, String posicion){
        this.port = port;
        this.dominio = dominio;
        this.path = path;
        this.posicion = posicion;
    }
    
    @Override
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        //Boolean terminar = false;
        if(dominio.endsWith("/"))
            obtenerFavico(dominio,path);
        else
            obtenerFavico(dominio+"/",path);
        this.pool.execute(new Manejador(pool,dominio,path,posicion));
    }
    
    public void obtenerFavico(String dominio, String path){
        try {
            URL url = new URL(dominio+"favicon.ico");
            File file = new File(path+"/favicon.ico");
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.createNewFile();
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();
            OutputStream out = new FileOutputStream(file);
            int b = 0;
            while (b != -1) {
                b = in.read();
                if (b != -1)
                    out.write(b);
            }
            out.close();
            in.close();
        } catch (IOException ex) {}
    }
    
    public static void main(String[] args) {
        System.out.println("Dirección dominio: ");
        String dominio = "";
        Scanner scan = new Scanner (System.in);
        dominio = scan.nextLine ();
        System.out.println("Direccion guardado: ");
        String carpeta = "";
        scan = new Scanner (System.in);
        carpeta = scan.nextLine ();
        File fileClass = new File("");
        String posicion = fileClass.getAbsolutePath();
        posicion = posicion.replace('\\','/');
        p4 des = new p4(9000,dominio,carpeta,posicion);
        new Thread(des,dominio).start();
    }   
}