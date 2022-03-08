
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Manejador implements Runnable {

    protected ArrayList<URL> urls;
    protected URL url;
    protected ExecutorService pool = Executors.newFixedThreadPool(3);
    protected String path;
    protected String location;
    protected String position;

    public Manejador(ExecutorService pool, String url, String path, String position) {
        try {
            this.pool = pool;
            this.url = new URL(url);
            this.path = path;
            this.position = position;
        } catch (Exception ex) {
        }
    }

    public void run() {
        try {
            String name = (path + "/" + recursoRuta(url)).replaceAll("%20", " ");
            File file = new File(name);
            if (!file.exists()) {
                crearRuta(url);
                file.createNewFile();
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();
                OutputStream out = new FileOutputStream(file);
                int b = 0;
                while (b != -1) {
                    b = in.read();
                    if (b != -1) {
                        out.write(b);
                    }
                }
                out.close();
                in.close();
                if (name.endsWith(".html") || name.endsWith(".php")) {
                    urls = obtenerURL(name, url.getHost(), location);
                    for (int i = 0; i < urls.size(); i++) {
                        this.pool.execute(new Manejador(pool, urls.get(i).toString(), path, position));
                    }
                    borrarDominio(name, url.getHost(), location);
                }
            }
            System.out.println("Descarga de: " + name + " terminada");
        } catch (IOException e) {
        }
    }

    public String nuevaRuta(URL url) {
        String ret = recursoRuta(url);
        int aux = url.getFile().indexOf("?");
        if (aux > -1) {
            ret = ret + url.getFile().substring(aux);
        }
        ret = ret.replaceAll("%20", " ");
        ret = position + "/" + path + "/" + ret;
        return ret;
    }

    public String recursoRuta(URL url) {
        String aux = url.getPath();
        boolean ext = true;
        for (int i = aux.length() - 1; i > -1; i--) {
            if (aux.charAt(i) == '.') {
                ext = false;
            } else if (aux.charAt(i) == '/') {
                i = -1;
            }
        }
        if (aux.equals("")) {
            aux = "index.html";
        } else if (aux.charAt(aux.length() - 1) == '/') {
            aux = aux + "index.html";
        } else if (ext) {
            aux = aux + ".html";
        }
        if (aux.charAt(0) == '/') {
            aux = aux.substring(1, aux.length());
        }
        return aux;
    }

    public String rutaRelativa(URL url) {
        String aux = url.getPath();
        int i, aux2 = aux.length();
        for (i = aux2 - 1; i > -1; i--) {
            if (aux.charAt(i) == '/') {
                aux2 = i + 1;
                i = -1;
            }
        }
        if (aux.length() > 1) {
            aux = aux.substring(1, aux2);
        }
        if (aux.length() == 1) {
            if (aux.charAt(0) == '/') {
                aux = "";
            }
        }
        return aux;
    }

    public void crearRuta(URL url) {
        String route = this.path + '/' + rutaRelativa(url);
        File dir = new File(route);
        this.location = route.substring(this.path.length());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public boolean compararDominio(String dom1, String dom2) {
        boolean b = true;
        String aux1, aux2;
        try {
            aux1 = dom1.replaceFirst("www.", "");
            aux2 = new URL(dom2).getHost();
            aux2 = aux2.replaceFirst("www.", "");
            b = !aux1.equals(aux2);
        } catch (IOException ex) {
        }
        return b;
    }

    public ArrayList<URL> obtenerURL(String name, String dom, String ub) {
        ArrayList<URL> ret = new ArrayList<URL>();
        String string = "";
        try {
            FileReader f = new FileReader(name);
            BufferedReader b = new BufferedReader(f);
            while ((string = b.readLine()) != null) {
                for (int i = 0; i < string.length(); i++) {
                    if (i + 7 < string.length()) {
                        if ((string.substring(i, i + 4).equals("href")) || (string.substring(i, i + 3).equals("src"))) {
                            if (string.substring(i, i + 3).equals("src")) {
                                i = i + 3;
                            } else {
                                i = i + 4;
                            }
                            boolean fol = true, fol2 = true;
                            while (fol2) {
                                if (string.charAt(i) == '=') {
                                    fol2 = false;
                                } else if (string.charAt(i) != ' ') {
                                    fol = false;
                                    fol2 = false;
                                }
                                if (i >= string.length()) {
                                    fol = false;
                                    fol2 = false;
                                }
                                i += 1;
                            }
                            //i+=1;
                            if (fol) {
                                fol2 = true;
                                while (fol2) {
                                    if (string.charAt(i) == '"') {
                                        fol2 = false;
                                    } else if (string.charAt(i) != ' ') {
                                        fol = false;
                                        fol2 = false;
                                    }
                                    if (i >= string.length()) {
                                        fol = false;
                                        fol2 = false;
                                    }
                                    i += 1;
                                }
                                //i+=1;
                                if (fol) {
                                    String aux = "";
                                    fol = true;
                                    fol2 = true;
                                    while (fol2) {
                                        if (string.charAt(i) == '"') {
                                            fol2 = false;
                                        } else {
                                            aux = aux + String.valueOf(string.charAt(i));
                                        }
                                        if (i >= string.length()) {
                                            fol = false;
                                            fol2 = false;
                                        }
                                        i += 1;
                                    }
                                    if (fol) {
                                        if (aux.startsWith("http")) {
                                            if (!compararDominio(dom, aux)) {
                                                ret.add(new URL(aux));
                                            }
                                        } else {
                                            //Agregarle el dominio
                                            if (!aux.equals("")) {
                                                if (aux.charAt(0) == '/') {
                                                    ret.add(new URL("http://" + dom + aux));
                                                } else if (aux.charAt(0) != '?') {
                                                    ret.add(new URL("http://" + dom + ub + aux));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            b.close();
        } catch (IOException e) {
            return ret;
        }
        return ret;
    }

    public void borrarDominio(String name, String dom, String ub) {
        String string, string2 = "";
        boolean fol = true;
        try {
            File temporal = new File("temp.txt");
            FileWriter write;
            temporal.createNewFile();
            write = new FileWriter(temporal);
            File arch = new File(name);
            FileReader f = new FileReader(arch);
            BufferedReader b = new BufferedReader(f);
            while (fol) {
                string = b.readLine();
                if (string == null) {
                    fol = false;
                } else {
                    string2 = string;
                    for (int i = 0; i < string.length(); i++) {
                        if (i + 7 < string.length()) {
                            if ((string.substring(i, i + 4).equals("href")) || (string.substring(i, i + 3).equals("src"))) {
                                if (string.substring(i, i + 3).equals("src")) {
                                    i = i + 3;
                                } else {
                                    i = i + 4;
                                }
                                boolean fol2 = true, fol3 = true;
                                while (fol3) {
                                    if (string.charAt(i) == '=') {
                                        fol3 = false;
                                    } else if (string.charAt(i) != ' ') {
                                        fol2 = false;
                                        fol3 = false;
                                    }
                                    if (i >= string.length()) {
                                        fol2 = false;
                                        fol3 = false;
                                    }
                                    i += 1;
                                }
                                //i+=1;
                                if (fol2) {
                                    fol3 = true;
                                    while (fol3) {
                                        if (string.charAt(i) == '"') {
                                            fol3 = false;
                                        } else if (string.charAt(i) != ' ') {
                                            fol2 = false;
                                            fol3 = false;
                                        }
                                        if (i >= string.length()) {
                                            fol2 = false;
                                            fol3 = false;
                                        }
                                        i += 1;
                                    }
                                    //i+=1;
                                    if (fol2) {
                                        String aux = "";
                                        fol2 = true;
                                        fol3 = true;
                                        while (fol3) {
                                            if (string.charAt(i) == '"') {
                                                fol3 = false;
                                            } else {
                                                aux = aux + String.valueOf(string.charAt(i));
                                            }
                                            if (i >= string.length()) {
                                                fol2 = false;
                                                fol3 = false;
                                            }
                                            i += 1;
                                        }
                                        if (fol2) {
                                            if (aux.startsWith("http")) {
                                                if (!compararDominio(dom, aux)) {
                                                    string2 = string2.replaceFirst(aux, nuevaRuta(new URL(aux)));
                                                }
                                            } else {
                                                //Agregarle el dominio
                                                if (!aux.equals("")) {
                                                    if (aux.charAt(0) == '/') {
                                                        string2 = string2.replaceFirst(aux, nuevaRuta(new URL("http://" + dom + aux)));
                                                    } else if (aux.charAt(0) != '?') {
                                                        string2 = string2.replaceFirst(aux, nuevaRuta(new URL("http://" + dom + ub + aux)));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    write.write(string2 + "\n");
                    string2 = "";
                }
            }
            b.close();
            arch.delete();
            write.close();
            temporal.renameTo(new File(name));
        } catch (IOException e) {
        }
    }
}
