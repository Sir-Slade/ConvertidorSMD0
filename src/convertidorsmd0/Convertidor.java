/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package convertidorsmd0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jeshua
 */
public class Convertidor {

    HashMap<String, String[]> funciones;
    HashMap<String, String[]> funcionesAHex;

    public Convertidor(){
        //Abre el archivo "instrucciones.txt" que contiene los nombres de las funciones
        File archivo = new File("instrucciones.txt");
        try{
            FileInputStream lector = new FileInputStream(archivo);
            StringBuffer cad = new StringBuffer();
            int aux;
            while((aux = lector.read()) != -1){
                cad.append((char) aux);

            }

            String res = cad.toString();
            //res = res.replace("\n", "");
            String [] split1 = res.split(";");
            String [] split2;

            //Guarda los nombres de las funciones en un Hashmap
            funciones = new HashMap<String, String[]>();//La llave de este es la función en Hex
            funcionesAHex = new HashMap<String, String[]>();//La llave de este es el nombre de la función
            for(int i = 0; i<split1.length; i++){
                split2 = split1[i].split(",");
                String key = split2[0].toLowerCase();

                //System.out.println(Charset.defaultCharset());
                funciones.put(key, split2);
                //int as = Integer.parseInt(key, 16);
                funcionesAHex.put(split2[1],split2);
                

            }


        }catch(Exception e){
            System.out.println(e.fillInStackTrace());
        }


    }

    //Decompila un archivo .SMD y lo muestra en la TextArea
    public String abrirSMD(String path){
        File archivo = new File(path);
        int aux, numChan, offData, offFile;
        StringBuffer res = new StringBuffer();
        //res.append(path + "\n");
        //res.append("\n");
        ArrayList<Integer> file = new ArrayList<Integer>();
        ArrayList<Integer> scraps = new ArrayList<Integer>();


        try{
            FileInputStream f = new FileInputStream(archivo);

            //El header
            int k;
            int cod = 0;
            res.append("Identificador: ");
            for(k = 0 ;k<4; k++){ //4 bytes
                cod = f.read();
                char c = (char)cod;
                res.append(c);

            }
            cod = 0;
            res.append("\n");

            res.append("Desconocido1: ");
            for(k = 0 ;k<4; k++){ //4 bytes
                res.append(f.read() +",");

            }
            res.append("\n");
            res.append("Tamaño en Bytes: ");
            for(k = 0 ;k<4; k++){ //4 bytes
                int a = f.read();
                cod += (a * ((int)Math.pow(16, (k*2))));
                
            }
            res.append(cod + "\n");
            cod = 0;

            res.append("Desconocido2: ");
            for(k = 0 ;k<8; k++){ //8 bytes
                res.append(f.read() +",");

            }
            res.append("\n");
            numChan = f.read();

            res.append("No. de Canales: " + numChan + "\n");

            res.append("Desconocido3: ");
            for(k = 0 ;k<9; k++){ //9 bytes
                res.append(f.read() +",");

            }
            res.append("\n");

            res.append("Offset de nombre de Archivo: ");
            for(k = 0 ;k<2; k++){ //2 bytes
                cod += (f.read() * ((int)Math.pow(16, (k*2))));

            }
            offFile = cod;
            res.append(cod + "\n");
            cod = 0;

            res.append("Offset de \"DataChunk\": ");
            for(k = 0 ;k<2; k++){ //2 bytes
                cod += (f.read() * ((int)Math.pow(16, (k*2))));

            }
            offData = cod;
            res.append(cod + "\n");
            cod = 0;

            for(int a = 0; a<numChan; a++){

                res.append("Offset de Canal "+a+": ");
                for(k = 0 ;k<2; k++){ //2 bytes
                    cod += (f.read() * ((int)Math.pow(16, (k*2))));

                }
                res.append(cod + "\n");
                cod = 0;

            }

            res.append("Fin de los Offsets (Sup. igual a 0): ");
            for(k = 0 ;k<2; k++){ //2 bytes
                cod += (f.read() * ((int)Math.pow(16, (k*2))));

            }
            res.append(cod + "\n");
            cod = 0;


            while((aux = f.read()) != 0){
                scraps.add(aux);

            }
            scraps.add(aux);
            
            res.append("Nombre del Archivo ("+scraps.size()+" bytes) : ");
            
            for(k = 0; k<scraps.size(); k++){
                int c1 = scraps.get(k);
                char c = (char) c1;
                res.append(c);
                
                
            }
            res.append("\n");

            //file.add(aux);
            while((aux = f.read()) != -1){
                file.add(aux);

            }

            int aux2;
            for(int i = 0; i< file.size(); i++){
                aux2 = file.get(i);
                if(aux2 <= 127){
                    res.append("TocarNota" + aux2 + "(");
                    i++;
                    aux2 = file.get(i);
                    String nota;

                    switch(aux2/19){
                        case 0:
                            nota = "Do";
                            break;

                        case 1:
                            nota = "Do#";
                            break;

                        case 2:
                            nota = "Re";
                            break;

                        case 3:
                            nota = "Re#";                            
                            break;

                        case 4:
                            nota = "Mi";
                            break;

                        case 5:
                            nota = "Fa";
                            break;

                        case 6:
                            nota = "Fa#";
                            break;

                        case 7:
                            nota = "Sol";
                            break;

                        case 8:
                            nota = "Sol#";
                            break;

                        case 9:
                            nota = "La";
                            break;

                        case 10:
                            nota = "La#";
                            break;

                        case 11:
                            nota = "Si";
                            break;

                        default:
                            nota = Integer.toHexString(aux2);
                            break;

                    }
                    res.append(nota);

                    if(aux2%19 != 0 || (i+1 == file.size())){
                        res.append(", s" + aux2%19+ ");\n");
                    }else{
                        i++;
                        aux2 = file.get(i);
                        res.append(", ");
                        res.append(aux2 + ");\n");

                    }
                }else{
                    
                    String [] auxstring3 = funciones.get(Integer.toHexString(aux2));
                    //System.out.println(Integer.toHexString(aux2));
                    //System.out.println(funciones.containsKey("ba"));
                    res.append(auxstring3[1] + "(");
                    int contParam = Integer.parseInt(auxstring3[2]);
                    for(int j = 0; j<contParam; j++){
                        i++;
                        aux2 = file.get(i);
                        res.append(aux2);
                        if(j != (contParam-1)){
                            res.append(",");

                        }
                    }
                    res.append(");\n");

                }
            }
            //System.out.println(res.toString().length());
            return res.toString();

        }catch(IOException e){
            String msg = e.getMessage();
            return msg;
        }

    }

    //Abre un archivo .smde
    public String abrirTexto(String archivo){
        try{
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String e;
            StringBuffer sb = new StringBuffer();

            while((e = br.readLine()) != null){
                sb.append(e +"\n");

            }

            return sb.toString();
        }catch(Exception e){
            return e.getMessage();
        }


    }

    //compila el archivo .SMD a partir de un archivo .smde
    public String compilar(String archivo){
        int numDeLineaActual = 0;
        String funcionActual = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String e;
            String [] aux;
            StringBuffer strB = new StringBuffer();
            ArrayList<Integer> ultimo = new ArrayList<Integer>();

            int numChan = 9; //hay 8 lineas antes de llegar a los canales y
            //1 linea despues para el fin de los offsets
            //La linea del nombre del archivo se lee después

            //Proceso para la primera línea
            e = br.readLine();
            aux = e.split(":");
            aux[1] = aux[1].trim();
            for(int b = 0; b< aux[1].length(); b++){
                ultimo.add(Integer.valueOf(aux[1].charAt(b)));
                System.out.println(aux[1].charAt(b));

            }
            numDeLineaActual++;
            //k es el número de lineas que tiene el header
            for(int k = 1; k<numChan; k++){
                System.out.println(k);
                e = br.readLine();                
                aux = e.split(":");
                aux[1] = aux[1].trim();
                System.out.println("Num Chan: " +numChan);
                aux = aux[1].split(",");
                int x;//auxiliar par convertir
                int y;//contador
                int z;//num de bits a leer
                if(aux.length == 1){//Si no tiene comas osea es 1 solo valor

                    
                    switch(k){
                        case 2:
                            z = 4;
                            break;

                        case 4:
                            z = 1;
                            numChan += Integer.parseInt(aux[0]);
                            System.out.println("Num Chan: " +numChan);
                            break;

                        default:
                            z = 2;
                            break;

                    }


                    x = Integer.parseInt(aux[0]);
                    //System.out.println(x);
                    int[] cifras = new int[z];

                    for(y = z-1; y>=0; y--){//4 veces en reversa
                        cifras[y] = x / (int)(Math.pow(16, (y*2)));
                        x = x%(int)(Math.pow(16,(y*2)));
                        
                    }
                    for(y = 0; y<z; y++){
                        ultimo.add(cifras[y]);

                    }
                }else{
                    for(y = 0; y<aux.length ; y++){
                        x = Integer.parseInt(aux[y]);
                        ultimo.add(x);

                    }
                }
                numDeLineaActual++;
            }

            //Proceso para la última linea (nombre del archivo)
            e = br.readLine();
            aux = e.split(":");
            aux[1] = aux[1].trim();
            for(int b = 0; b< aux[1].length(); b++){
                ultimo.add(Integer.valueOf(aux[1].charAt(b)));
                System.out.println(aux[1].charAt(b));

            }
            ultimo.add(0); //Byte que indica el fin del nombre del archivo
            numDeLineaActual++;
            System.out.println("Header compilado. Empezando linea " + numDeLineaActual);


            while((e = br.readLine()) != null){
                System.out.println(e.length() + " " + e + (!e.startsWith("//") && (e.length() > 0)));
                if(!e.startsWith("//") && (e.length() > 0)){
                    e = e.split("//")[0];
                    e = e.trim();
                    System.out.println("Parametros");
                    aux = e.split("\\(");//Primer Split para identificar funcion
                    funcionActual = (aux[0] + " con parámetros " + aux[1]);
                    aux[1] = aux[1].substring(0, aux[1].length() -2);
                    System.out.println(aux[1]);//Muestra los parámetros de la funcion de arriba
                    int auxInt;

                    if(aux[0].contains("TocarNota")){//Si es nota
                        aux[0] = aux[0].substring(9);                        
                        auxInt = Integer.parseInt(aux[0]);
                        ultimo.add(auxInt);
                        aux = aux[1].split(",");
                        aux[0] = aux[0].trim();
                        aux[1] = aux[1].trim();
                        aux[0] = aux[0].toLowerCase();
                        auxInt = 19;
                        if(aux[0].equals("do")){
                            auxInt *= 0;

                        }else if(aux[0].equals("do#")){
                            auxInt *= 1;

                        }else if(aux[0].equals("re")){
                            auxInt *= 2;
                            
                        }else if(aux[0].equals("re#")){
                            auxInt *= 3;

                        }else if(aux[0].equals("mi")){
                            auxInt *= 4;

                        }else if(aux[0].equals("fa")){
                            auxInt *= 5;

                        }else if(aux[0].equals("fa#")){
                            auxInt *= 6;

                        }else if(aux[0].equals("sol")){
                            auxInt *= 7;

                        }else if(aux[0].equals("sol#")){
                            auxInt *= 8;

                        }else if(aux[0].equals("la")){
                            auxInt *= 9;

                        }else if(aux[0].equals("la#")){
                            auxInt *= 10;

                        }else{
                            auxInt *= 11;

                        }

                        if(aux[1].contains("s")){//Si es nota de un solo parametro
                            aux[1] = aux[1].substring(1);
                            auxInt += Integer.parseInt(aux[1]);
                            ultimo.add(auxInt);

                        }else{
                            ultimo.add(auxInt);
                            ultimo.add(Integer.parseInt(aux[1]));

                        }

                    }else{ //Si es funcion normal
                        System.out.println("Compilando...");
                        String[] funcion = funcionesAHex.get(aux[0]);
                        auxInt = Integer.parseInt(funcion[0], 16);
                        //System.out.println("El caballero de junini ha hablado");
                        ultimo.add(auxInt);
                        if(Integer.parseInt(funcion[2]) > 0){//Si tiene parametros
                            aux = aux[1].split(",");
                            for(int i = 0; i<aux.length; i++){//Por cada parametro que tenga
                                auxInt = Integer.parseInt(aux[i]);
                                ultimo.add(auxInt);
                            }
                        }
                        System.out.println("Funcion Compilada");
                    }
                }
                numDeLineaActual++;
                System.out.println("Línea: " + numDeLineaActual);
            }

            String arch = archivo.substring(0, archivo.length() - 5);

            FileOutputStream fo = new FileOutputStream(arch + ".SMD");

            byte[]b = new byte[ultimo.size()];
            for(int j = 0; j<b.length; j++){
                b[j] = (byte)(ultimo.get(j) & 0xff);

            }
            fo.write(b);
            fo.close();
            System.out.println(numDeLineaActual + " lineas compiladas!");
            String resultado = "OK: " + numDeLineaActual + " lineas compiladas!";

            return resultado;
            
        }catch(Exception e){
            System.out.println(e.fillInStackTrace());
            String resultado = "Hubo un error al compilar: " + e.fillInStackTrace().getMessage() +"\n";
            resultado += "Linea: " + numDeLineaActual + "\n";
            resultado += funcionActual;
            return resultado;

        }

    }

    //Método para crear el archivo .smde
    public String crearArchivoTexto(String titulo, String archivo, String ruta){
        try{
            PrintWriter p = new PrintWriter(ruta + titulo, "UTF-8");
            //System.out.println(archivo.contains("\n"));
            for(int i = 0; i<archivo.length(); i++){
                p.print(archivo.charAt(i));

            }
            //System.out.println(archivo.length());
            p.close();

            return titulo;
        }catch(Exception e){
            String res = "No fue posible guardar el archivo de testo: \n";
            res = res + e.getMessage();
            return res;
        }



    }

}
