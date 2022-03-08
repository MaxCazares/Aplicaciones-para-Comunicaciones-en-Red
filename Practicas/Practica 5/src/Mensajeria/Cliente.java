
package Mensajeria;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.util.Iterator;
import java.util.Scanner;


/**
 *
 * @author Christian
 */
public class Cliente {
    
    public static String nombresito;
    public static int tipo=0;
    public static int ml=0;
    public static int me=0;
    
    public static void main(String[] args) {
        String host="127.0.0.1";
        int pto=9500;
        String nombre="";
        Scanner nom=new Scanner(System.in);
        System.out.println("Escribe tu nombre");
        nombre=nom.nextLine();
        nombresito=nombre;
        try
        {
           BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
           SocketChannel cl = SocketChannel.open();
           cl.configureBlocking(false);
           Selector sel = Selector.open();
           cl.connect(new InetSocketAddress(host,pto));
           cl.register(sel,SelectionKey.OP_CONNECT);
           while(true)
           {
               sel.select();
               Iterator<SelectionKey>it =sel.selectedKeys().iterator();
               while(it.hasNext()){
                   SelectionKey k = (SelectionKey)it.next();
                   it.remove();
                   if(k.isConnectable()){
                       SocketChannel ch = (SocketChannel)k.channel();
                       if(ch.isConnectionPending()){
                           try{
                               ch.finishConnect();
                               System.out.println("Conexion establecida.. Escribe un mensaje cualquiera para ser registrado con tu nombre <ENTER> para enviar \"SALIR\" para teminar");
                           }catch(Exception e){
                               e.printStackTrace();
                           }//catch
                       }//if_conectionpending
                       ch.configureBlocking(false);
                       ch.register(sel, SelectionKey.OP_WRITE);
                       me=1;
                       continue;
                   }//if
                   if(k.isWritable()){
                       String mes = br.readLine();
                       if(me==0)
                       {
                           k.interestOps(SelectionKey.OP_WRITE);
                           me=1;
                           ml=0;
                       }
                       SocketChannel ch2 = (SocketChannel)k.channel();
                       String destino="";
                       Mensaje msj=new Mensaje(nombresito,tipo,mes,"");
                       
                       if(tipo==1)
                       {
                          System.out.println("Ingresa el nombre del usuario al que se lo deseas mandar");
                          destino=nom.nextLine();
                          msj.destino=destino;
                       }
                       if(tipo==0)
                       {
                           tipo=1;
                           System.out.println("Registrandose");
                       }
                       
                       ByteArrayOutputStream baos=new ByteArrayOutputStream();
                       ObjectOutputStream oos= new ObjectOutputStream(baos);
                       oos.writeObject(msj);
                       oos.flush();
                       ByteBuffer b= ByteBuffer.wrap(baos.toByteArray());
                       ch2.write(b);
                       if(msj.mensaje.equalsIgnoreCase("SALIR")){
                           System.out.println("Termina aplicacion...");
                           ch2.close();
                           System.exit(0);
                       }else{
                       k.interestOps(SelectionKey.OP_READ);
                       me=0;
                       ml=1;
                       continue;
                       }//else
                   } else if(k.isReadable()){
                       if(ml==0)
                       {
                           k.interestOps(SelectionKey.OP_READ);
                           me=0;
                           ml=1;
                       }
                       SocketChannel ch2 = (SocketChannel)k.channel();
                       ByteBuffer b = ByteBuffer.allocate(2000);
                       b.clear();
                       ch2.read(b);
                       b.flip();
                       if(b.hasArray())
                       {
                           ObjectInputStream leo= new ObjectInputStream(new ByteArrayInputStream(b.array()));
                           Mensaje msg=(Mensaje) leo.readObject();
                           if(msg.tipo==0)
                           {
                               System.out.println("Has sido registrado por tu nombre");
                           }
                           if(msg.tipo==1)
                           {
                               System.out.println(msg.nombre+": "+msg.mensaje);
                           }
                       }
                       k.interestOps(SelectionKey.OP_WRITE);
                       me=1;
                       ml=0;
                       continue;
                   }//if
               }//while
           }//while
        }catch(Exception e)
        {
          e.printStackTrace();
        }
    }
}
