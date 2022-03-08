package Mensajeria;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
/**
 *
 * @author Christian
 */
public class Server {
    
    
    public static void main(String[] args){
       int pto=9500;
       int coincidencia=0;
       ArrayList<Users> player=new ArrayList<>();
       try{
          ServerSocketChannel s = ServerSocketChannel.open();
          s.configureBlocking(false);
          s.setOption(StandardSocketOptions.SO_REUSEADDR, true);
          s.socket().bind(new InetSocketAddress(pto));
          Selector sel = Selector.open();
          s.register(sel,SelectionKey.OP_ACCEPT);
           System.out.println("Servicio iniciado..esperando clientes..");
          while(true){
              sel.select();
              Iterator<SelectionKey>it= sel.selectedKeys().iterator();
              while(it.hasNext()){
                  SelectionKey k = (SelectionKey)it.next();
                  it.remove();
                  if(k.isAcceptable()){
                      SocketChannel cl = s.accept();
                      System.out.println("Cliente conectado desde->"+cl.socket().getInetAddress().getHostAddress()+":"+cl.socket().getPort());
                      player.add(new Users("",cl));
                      cl.configureBlocking(false);
                      cl.register(sel,SelectionKey.OP_READ);
                      continue;
                  }//if
                  if(k.isReadable()){
                      SocketChannel ch =(SocketChannel)k.channel();
                      ByteBuffer b = ByteBuffer.allocate(2000);
                      b.clear();
                      ch.read(b);
                      b.flip();
                      //String msj = new String(b.array(),0,n);
                      if(b.hasArray())
                      {
                          ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(b.array()));
                          Mensaje rec=(Mensaje)ois.readObject();
                          if(rec.tipo==0)
                          {
                              for(int i=0; i<player.size();i++)
                              {
                                  Users c0=player.get(i);
                                  if(ch.socket().getInetAddress().getHostAddress().equalsIgnoreCase(c0.cliente.socket().getInetAddress().getHostAddress()) && ch.socket().getPort() == c0.cliente.socket().getPort())
                                  {
                                      player.get(i).nombre=rec.nombre;
                                      System.out.println(""+rec.nombre+" ha sido registrado");
                                  }
                                  
                              }
                              ch.write(b);
                          }
                          if(rec.tipo==1)
                          {
                              if(rec.mensaje.equalsIgnoreCase("SALIR"))
                              {
                                  System.out.println(rec.nombre+" se retira del servidor");
                                  ch.close();
                              }else{
                              for(int i=0; i<player.size();i++)
                              {
                                  Users c0=player.get(i);
                                  if(c0.nombre.equals(rec.destino))
                                  {
                                      player.get(i).cliente.write(b);
                                      System.out.println("Myspace: "+rec.nombre+"->"+rec.destino+": "+rec.mensaje);
                                      coincidencia=1;
                                  }
                                  if(coincidencia==0)
                                  {
                                      System.out.println("Usuario no encontrado");
                                  }
                              }
                             }
                          }
                      }
                      
                  }//if_readable
                  
              }//while
          }//while
       }catch(Exception e){
           e.printStackTrace();
       }//catch
    }//main
}