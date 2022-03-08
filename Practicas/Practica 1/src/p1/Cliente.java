package p1;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener{
    public JButton Borrar = new JButton("Borrar"), Enviar = new JButton("Enviar"), Descargar = new JButton("Descargar");
    public static Cliente menu = new Cliente();
    public Cliente(){
        super("Practica 1");
        this.setSize(310, 100);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Enviar.setBounds(10, 10, 80, 50);
        Borrar.setBounds(100, 10, 80, 50);        
        Descargar.setBounds(190, 10, 100, 50);
        
        Borrar.addActionListener(this);
        Enviar.addActionListener(this);
        Descargar.addActionListener(this);
        
        this.add(Enviar);
        this.add(Borrar);        
        this.add(Descargar);
    }
    public static void main(String[] args)throws IOException{
        menu.setVisible(true);
        Servidor c = new Servidor(9000);
        c.iniciarServidor();
    } 
    public static String crearCarpeta(String url){
        File f = new File("");
        String ruta = f.getAbsolutePath();        
        String rutaArchivos = ruta + "\\" + url;
        File f2 = new File(rutaArchivos);
        f2.mkdirs();
        f2.setWritable(true);
        return rutaArchivos;
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == Enviar){
            String ruta = crearCarpeta("CarpetaServidor");
            CCliente c1 = new CCliente(ruta);
            c1.seleccionaArchivos();
            c1.enviarArchivos();        
        }else
            if(e.getSource() == Borrar){
                CCliente c2 = new CCliente("");
                c2.seleccionaArchivos();
                c2.Borrar();
            }else
                if(e.getSource() == Descargar){
                    String ruta = crearCarpeta("Descargas");
                    CCliente c3 = new CCliente("");
                    c3.seleccionaArchivos();
                    Servidor ser = new Servidor(0);
                    ser.enviarArchivos(c3.getFicheros(), ruta);
                }
    }
}