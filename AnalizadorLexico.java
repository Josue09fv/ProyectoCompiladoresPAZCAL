/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pazcal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Josué
 */
public class AnalizadorLexico {
    String nombreArchivo;
    String nombreArchivoPazcal;
    String nombreArchivoPas;
    int nLinea=0;
    String typeValor="";
    //int n1=0;
    boolean encontrarBegin=false;
    boolean encontrarEND=false;
    boolean resolver=false;
    boolean abierto=false;
    boolean cerrado=false;
    boolean permitir1=false;
    int contadorAbiertos=0;
    int contadorcerrados=0;
    boolean abierto2=false;
    boolean cerrado2=false;
    boolean permitir2=false;
    int contadorAbiertos2=0;
    int contadorcerrados2=0;
    boolean analizarLinea=false;
    boolean program=false;
    boolean var=false;
    boolean identDefinido=false;
    boolean errorComentario=false;
    boolean antesdeEnd=true;
    String lineaAntesEnd="";
    String identificador="";
    String erroresComentarios="";
    List<String> listaLineasPazcal=new ArrayList();
    List<String> tokens=new ArrayList();
    List<String>listaLineasErrores=new ArrayList();
    List<String>Reservadas=new ArrayList();
    List<String>t1=new ArrayList();
    List<String>identDeclarados=new ArrayList();
    List <String>identificadoresActuales=new ArrayList<>();
    String Resultado="";
    boolean mismoTipo=false;
    public AnalizadorLexico(String nombreArchivo) {
        this.nombreArchivo=nombreArchivo;
    }
    public void iniciarAnalizadorLexico() throws FileNotFoundException, IOException{
        validarNombreArchivo();
        File archivo=new File(this.nombreArchivo);
        String ruta=archivo.getAbsolutePath();
        FileReader archivoPazcal=new FileReader(ruta);
        FileWriter crearArchivoErrores=new FileWriter("INFLACION-errores.txt");//para crear archivo errores
        crearArchivoErrores.close();
        BufferedReader bf=new BufferedReader(archivoPazcal);
        String lineaPazcal;
        while ((lineaPazcal=bf.readLine())!=null) {            
            listaLineasPazcal.add(lineaPazcal);
        }
        archivoPazcal.close();
        
        obtenerTokens();
        
        crearArchivoPas();
    }
    public void validarNombreArchivo(){
         String nombreArchivo=this.nombreArchivo;
         char []caracteres=nombreArchivo.toCharArray();
         String []delimitarExtension;
         String extension="";
         int contarpuntos=0;
         boolean nombreValido=false;
         for(int i=0; i<caracteres.length;i++){
             if(caracteres[i]=='.')
                 contarpuntos++;
         }
         if(contarpuntos==1){
             delimitarExtension=nombreArchivo.split("[\\.]");
             nombreArchivo=delimitarExtension[0];
             extension=delimitarExtension[1];
             this.nombreArchivoPazcal=nombreArchivo;
         }
         boolean extencionCorrecta=false;
         extension=extension.toLowerCase();
         if(contarpuntos==1){
             if(extension.matches("pazcal")==true)
                 extencionCorrecta=true;
             if(extencionCorrecta==true&&nombreArchivo.matches("[A-Za-z][A-Za-z0-9]*")){
                nombreArchivo+="."+"PAZCAL";
                nombreValido=true; 
            }else{
                 //agregar un error
                 
                 System.exit(1);
             } 
         }else{
                if(nombreArchivo.matches("[A-Za-z][A-Za-z0-9]*")&&contarpuntos==0){
                    nombreArchivoPazcal=nombreArchivo;
                    nombreArchivo+="."+"PAZCAL";
                    nombreValido=true;
               }else{
                    //agregar un error
                    System.exit(1);
                }
         }//hasta aquí
         if(nombreValido)
             this.nombreArchivo=nombreArchivo;
    }
    public void obtenerTokens() throws FileNotFoundException, IOException{
        FileReader archivoLectura=new FileReader("INFLACION-errores.txt");//para leer el archivo de errores
        BufferedReader br=new BufferedReader(archivoLectura);//para obtener cada linea del archivo errores
        String lineaConerrores="";//para almacenar la linea de código que se vaya obteniendo del archivo errores
        
        while((lineaConerrores=br.readLine())!=null){//se obtienen las líneas del archivo errores
            listaLineasErrores.add(lineaConerrores);// se guardan en una lista en la cual van a estar enumeradas cada una de las líneas
        }
        archivoLectura.close();
        FileWriter crearArchivoErrores=new FileWriter("INFLACION-errores.txt");//para crear archivo errores
        
        //String lineaEnumerada=lineaConerrores;//para ir formando la línea con los posibles errores
        for (String linea : listaLineasPazcal) {
           //lineaEnumerada="00001";
            if(linea.matches("[ ]*[{].*")&&linea.matches("[ ]*[{].*[}][ ]*")!=true){
                    contadorAbiertos++;
                }else{
                if(linea.matches(".*[}][ ]*")&&linea.matches("[ ]*[{].*[}][ ]*")!=true){     
                        contadorcerrados++;
                    }
            }
        }
        
        if(contadorAbiertos==contadorcerrados){
            permitir1=true;
        }else{
            if (contadorAbiertos>contadorcerrados) {
                errorComentario=true;
                erroresComentarios+="\n        error 0001 falta } de cierre";
                
                
            }else{
                errorComentario=true;
                erroresComentarios+="\n        error 0002 falta { de apertura";
            }
        }
        //para el otro tipo de comentarios
        for (String linea : listaLineasPazcal) {
            if(linea.matches("[ ]*[(][*].*")&&linea.matches("[ ]*[(][*].*[*][)][ ]*")!=true){
                    
                    contadorAbiertos2++;
                }else{
                if(linea.matches(".*[*][)][ ]*")&&linea.matches("[ ]*[(][*].*[*][)][ ]*")!=true){
                        contadorcerrados2++;
                    }
            }
        }
        if(contadorAbiertos2==contadorcerrados2){
            permitir2=true;
        }else{
            if (contadorAbiertos2>contadorcerrados2) {
                errorComentario=true;
                erroresComentarios+="\n        error 0003 falta *) de apertura";
            }else{
                errorComentario=true;
                erroresComentarios+="\n        error 0004 falta (* de apertura";
                
                
            }
        }
         
        //aqui empieza a separar comentarios
        if(true){
            
            //listaLineasErrores.add(lineaEnumerada);
            nLinea=0;
            for (String linea : listaLineasPazcal) {
                int numeroLinea=100001;//para enumerar las líneas
                String lineasEnumerada="";
                numeroLinea+=nLinea++;
                lineasEnumerada=descomponerNumeroLinea(numeroLinea);
                lineasEnumerada+=" ";
                lineasEnumerada+=linea;
                listaLineasErrores.add(lineasEnumerada);
            }
            nLinea=0;
            for (String linea : listaLineasPazcal) {
                nLinea++;
                String aux="";
                if(listaLineasErrores.size()>=0)
                    aux=listaLineasErrores.get(nLinea-1);
                
                
                if (linea.matches("[ ]*[{].*[}][ ]*")) {
                    abierto=true;
                    cerrado=true;
                   
                }else{
                    if(linea.matches("[ ]*[{].*")){
                        abierto=true;
                    }else{
                        if(linea.matches(".*[}][ ]*")){
                            cerrado=true;
                        }
                    }
                }
                if (abierto==true&&cerrado==true) {
                    abierto=false;
                    cerrado=false;
                    analizarLinea=false;//no se analiza porque  es comentario
                }else{
                    if(contadorAbiertos!=contadorcerrados){
                            if((abierto==true&&cerrado==false)||(abierto==false&&cerrado==true)){
                             
                                analizarLinea=true;
                           }else{
                                if (abierto==false&&cerrado==false){
                                    if(linea.matches("[ ]*[(][*].*")!=true&&linea.matches(".*[*][)][ ]*")!=true&&linea.matches("[ ]*[(][*].*[*][)][ ]*")!=true&&abierto2!=true&&cerrado2!=true){
                                        analizarLinea=true;
                                    }
                                }
                            }
                    }
                    
                    
                }
                if(abierto==false&&cerrado==false){
                    if(linea.matches("[ ]*[(][*].*")!=true&&linea.matches(".*[*][)][ ]*")!=true&&linea.matches("[ ]*[(][*].*[*][)][ ]*")!=true&&abierto2!=true&&cerrado2!=true){
                        analizarLinea=true;
                    }
                }
                //para el otro tipo de comentario
                if (linea.matches("[ ]*[(][*].*[*][)][ ]*")) {//observar
                
                    abierto2=true;
                    cerrado2=true;
                }else{
                    if(linea.matches("[ ]*[(][*].*")){
                        abierto2=true;
                    }else{
                        if(linea.matches(".*[*][)][ ]*")){
                            cerrado2=true;   
                        }
                    }
                }
                if (abierto2==true&&cerrado2==true) {
                    
                    abierto2=false;
                    cerrado2=false;
                    analizarLinea=false;
                }else{
                    if(contadorAbiertos2!=contadorcerrados2){
                        if((abierto2==true&&cerrado2==false)==true||(abierto2==false&&cerrado2==true)==true){
                            analizarLinea=true;// esto es para cuando falta (* ó *), entonces se precesan las líneas que son parte del comentario 
                        }else{
                                if (abierto2==false&&cerrado2==false){
                                    if(linea.matches("[ ]*[{].*")!=true&&linea.matches(".*[}][ ]*")!=true&&linea.matches("[ ]*[{].*[}][ ]*")!=true&&abierto!=true&&cerrado!=true){
                                        analizarLinea=true;
                                    }
                                } 
                            }
                    }
                    
                }
                if(abierto2==false&&cerrado2==false){
                    if(linea.matches("[ ]*[{].*")!=true&&linea.matches(".*[}][ ]*")!=true&&linea.matches("[ ]*[{].*[}][ ]*")!=true&&abierto!=true&&cerrado!=true){
                        analizarLinea=true;
                    }
                }
                if(analizarLinea==false){
                   
                }
                
                if(analizarLinea==true&&linea.toLowerCase().matches("[ ]*[{].*[}][ ]*")!=true&&linea.toLowerCase().matches("[ ]*[(][*].*[*][)][ ]*")!=true){
                    
                    //System.out.println(linea);
                    if(errorComentario==true){
                        aux+=erroresComentarios;
                        errorComentario=false;
                        erroresComentarios="";
                    }
                    if (linea.length()>150) {
                        
                        
                        aux+="\n        error 0005 la linea supera el límite de caracteres permitidos por línea";
                    }
                    if (linea.matches("")!=true&&linea.matches("[ ]*")!=true) {
                       
                        Pattern pattern=Pattern.compile("([A-Za-z][A-Za-z0-9]*)|([-]{0,1}[0-9]+[.]{1,1}[0-9]+)|"
                        + "([.])|([(]|[)])|([+]|[-]|[*]|[/])|([:][=])|([;])|([-]{0,1}[0-9]+)|([<][=]|[<][>]|[>][=]|[=]|[<]|[>])|(['].*['])|([']|['])|(:)");
                        Matcher encontrar=pattern.matcher(linea);
                        String tipo1="";
                        String tipo2="";
                        String tipo3="";
                        String tipo4="";
                        String tipo5="";
                        String tipo6="";
                        String tipo7="";
                        String tipo8="";
                        String tipo9="";
                        String tipo10="";
                        String tipo11="";
                        String tipo12="";
                        //String tipo13="";
                        while (encontrar.find()) {// while para dividir la línea actual en tokens, cada token que se obtiene pertene a uno de los patrones de la expresión regular                
                            tipo1=encontrar.group(1);//cada grupo del 1 al 8 son los patrones de la expresion regular
                            if (encontrar.group(1)!=null&&IdentificarReservadas(tipo1)==false) {//es identificador
                                
                                tokens.add(tipo1);
                                identificador=tipo1;
                                
                                if(identificadoresActuales.contains(tipo1)!=true){
                                    identificadoresActuales.add(tipo1);//para agregar los id de lsa expresiones
                                }
                                
                            }else{
                                if (encontrar.group(1)!=null&&IdentificarReservadas(tipo1)==true) {
                                    
                                    tokens.add(tipo1);
                                }
                            }
                            tipo2=encontrar.group(2);
                            if (encontrar.group(2)!=null) {
                                
                                tokens.add(tipo2);
                            }
                
                            tipo3=encontrar.group(3);
                            if (encontrar.group(3)!=null) {
                                
                                tokens.add(tipo3);
                            }
                
                            tipo4=encontrar.group(4);
                            if (encontrar.group(4)!=null) {
                                
                                tokens.add(tipo4);
                            }
                
                
                            tipo5=encontrar.group(5);
                            if (encontrar.group(5)!=null) {
                                
                                tokens.add(tipo5);
                            }
                
                            tipo6=encontrar.group(6);
                            if (encontrar.group(6)!=null) {
                                
                                tokens.add(tipo6);
                            }
                
                            tipo7=encontrar.group(7);
                            if (encontrar.group(7)!=null) {
                                
                                tokens.add(tipo7);
                            }
                
                            tipo8=encontrar.group(8);
                            if (encontrar.group(8)!=null) {
                                
                                tokens.add(tipo8);
                            }
                
                            tipo9=encontrar.group(9);
                            if (encontrar.group(9)!=null) {
                                
                                tokens.add(tipo9);
                            }
                            tipo10=encontrar.group(10);
                            if (encontrar.group(10)!=null) {
                                
                                tokens.add(tipo10);
                            }
                            tipo11=encontrar.group(11);
                            if (encontrar.group(11)!=null) {
                                
                                tokens.add(tipo11);
                            }
                            tipo12=encontrar.group(12);
                            if (encontrar.group(12)!=null) {
                                
                                tokens.add(tipo12);
                            }
                
                        }
                        erroresComentarios="";
                       
                        aux=analisisSintacticoSemantico(linea,aux, nLinea);
                        tokens.clear();
                        identificadoresActuales.clear();
                        typeValor=null;
                        typeValor="";
                    }
                    if(listaLineasErrores.size()>0){
                        listaLineasErrores.set(nLinea-1, aux);
                    }
                    //aux="";
                    identificador="";
                    identDefinido=false;
                    analizarLinea=false;
                    Resultado="";
                    resolver=false;
                    
                }
            }
            
            for (String linea : listaLineasErrores) {
                crearArchivoErrores.write(linea+="\r\n");
            }
            crearArchivoErrores.close();
        }//hasta aquí
    }
    public boolean IdentificarReservadas(String token) throws FileNotFoundException, IOException{
        FileReader lecturaReservadas=new FileReader("reservadas.txt");
        BufferedReader bf=new BufferedReader(lecturaReservadas);
        String leertoken="";
        boolean esReservada=false;
        Reservadas.clear();
        while ((leertoken=bf.readLine())!=null) {
            Reservadas.add(leertoken);
            if (token.equalsIgnoreCase(leertoken)) {
                esReservada=true;
            }
        }
        return esReservada;
    }
    public String analisisSintacticoSemantico(String lineaEvaluar, String lineasEnumerada, int Linea){
        boolean instruccionPermitida=true;
        boolean noesReservada=false;
        boolean palabraNopermitida=false;
        String palabraNovalida="";
        
        //Aquí va readln
        if(lineaEvaluar.toLowerCase().matches("[ ]*readln[ ]*[^(]*")){
            lineasEnumerada+="\n        error 0006 falta ( de apertura";
            
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*readln[ ]*[(][ ]*[,].*")){
            lineasEnumerada+="\n        error 0007 falta no debe venir ,";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*readln[^)]*")){
            lineasEnumerada+="\n        error 0008 no viene ) de cierre";
        }
        
        
        
        
        //aquí hacer writeln
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[(][^)]*")){
            lineasEnumerada+="\n        error 0009 falta )";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[^(]*[)].*")){      
            lineasEnumerada+="\n        error 0010 falta (";
            
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[(][ ]*[,][ ]*[)]*")){
            lineasEnumerada+="\n        error 0011 no viene OUTPUT por tanto no debe venir ,";
            
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[(][ ]*[^']*['][^'][)].*")){
            lineasEnumerada+="\n        error 0012 falta ' izquierda";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[(][ ]*['][^']*[^']*")){
            lineasEnumerada+="\n        error 0013 falta ' derecha";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[(][ ]*['].*['][,][ ]*[)].*")){
            lineasEnumerada+="\n        error 0014 no debe venir la , despues del texto porque no hay más parámetros que separar";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln.*['].*['][^, ]+.*[)].*")){
            lineasEnumerada+="\n        error 0015 debe venir la , despues del texto porque hay parámetros que separar";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[(][ ]*(integer|char)[^:][ ]*[0-9].*")){
            lineasEnumerada+="\n        error 0016 : que viene despues de la variable";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*[(][ ]*(integer|char):[^0-9]+[^0-9].*")){
            lineasEnumerada+="\n        error 0017 : no viene el ancho de la variable";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln.*(real[^:][^:]).*")){
            lineasEnumerada+="\n        error 0018 no viene : despues de la variable tipo real";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln.*real[:]*[ ]*[^0-9]+[:][0-9].*")){
            lineasEnumerada+="\n        error 0019 no viene el ancho de la variable tipo real";
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*writeln[ ]*.*real[:]*[ ]*[0-9][:][ ]*[^0-9 ].*")){
            lineasEnumerada+="\n        error 0020 no viene decimales";
            
        }
        
        if(lineaEvaluar.toUpperCase().contains("}")!=true&&lineaEvaluar.toUpperCase().contains("BEGIN")!=true&&lineaEvaluar.toUpperCase().contains("VAR")!=true&&lineaEvaluar.toUpperCase().contains("PROGRAM")!=true
          &&lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}.*[:][ ]*(integer|real|char)[ ]*[;][ ]*")
           !=true&&encontrarBegin==false){
            lineasEnumerada+="\n        "+"error 0021 los comando deben venir despues de begin";
            
        }
        if(lineaEvaluar.toUpperCase().matches("[ ]*END[ ]*[.]{1,1}[ ]*")){
            encontrarEND=true;
        }
        if(lineaEvaluar.toUpperCase().matches("[ ]*")!=true&&encontrarEND==true&&lineaEvaluar.toUpperCase().contains("END")!=true){
            lineasEnumerada+="\n        "+"error 0022 no debe venir nada despues de END";
            
        }
        if(lineaEvaluar.toLowerCase().contains("end")){
            if(listaLineasPazcal.get(nLinea-2).contains(";")&&listaLineasPazcal.get(nLinea-2).toLowerCase().contains("end")!=true){
                lineasEnumerada+="\n        error 0055 no debe venir ; porque es la última línea antes de END";
                
            }
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*end[^.]*[ ]*")){
            lineasEnumerada+="\n        error 0056 debe venir . despues de END";
            
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*end[.][^;]*")){
            lineasEnumerada+="\n        error 0057 no debe venir ; al final del end";
            
        }
        
        if(lineaEvaluar.toUpperCase().contains("BEGIN"))
            encontrarBegin=true;
        for (String reservada: Reservadas) {
            for (String token : tokens) {
                if(token.toLowerCase().equalsIgnoreCase(reservada.toLowerCase())){
                    if(token.toLowerCase().matches("(var)|(program)|(input)|(output)|(begin)|(end)|(writeln)|(readln)|(repeat)|(until)|(integer)|(real)|(char)")!=true){
                        lineasEnumerada+="\n        "+"instrucción "+token+" no es soportada por esta versión";
                        instruccionPermitida=false;
                    }
                }else{
                    if(token.toLowerCase().equalsIgnoreCase(reservada.toLowerCase())!=true)
                        noesReservada=true;
                }
            }
        }
        for (String token : tokens) {
            if(token.toLowerCase().matches("[A-Za-z][A-Za-z0-9]{0,14}")==true){
               
                if(identDeclarados.contains(token)!=true&&Reservadas.contains(token)!=true
                   &&lineaEvaluar.toLowerCase().matches("[ ]*(program).*")!=true
                   &&lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[:][ ]*(integer|real|char)[ ]*[;][ ]*")!=true
                   &&lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[:][^=]*")!=true){
                    
                    palabraNopermitida=true;//para ir haciendo todas las validaciones
                    
                    palabraNovalida=token;
                    break;
                    
                }
            }
        }
        
        if(palabraNopermitida){
            lineasEnumerada+="\n        error 0023"+palabraNovalida+" no es una instrucción válida de PAZCAL ni de PASCAL";
        }
        
      
        for (int i=0;i<listaLineasPazcal.size();i++) {
            if(listaLineasPazcal.get(i).toLowerCase().matches("[ ]*end[ ]*[.][ ]*")){
                     lineaAntesEnd=listaLineasPazcal.get(i-1);
                    
                    if (lineaEvaluar.toLowerCase().equalsIgnoreCase(lineaAntesEnd)) {
                        
                        lineaEvaluar=lineaAntesEnd;
                        
                    }
                
                
            }
                
        }
        if (lineaEvaluar.toLowerCase().equalsIgnoreCase(lineaAntesEnd)) {
            antesdeEnd=false;
            
        }
        if(lineaEvaluar.toLowerCase().matches(".*[;][ ]*")!=true&&lineaEvaluar.toLowerCase().contains("begin")!=true
           &&lineaEvaluar.toLowerCase().contains("end")!=true&&lineaEvaluar.toLowerCase().contains("var")
            !=true&&lineaEvaluar.toLowerCase().contains("repeat")!=true&&lineaEvaluar.toLowerCase().contains("}")!=true){//definir cuales comandos no llevan ;
            //if(listaLineasPazcal.size()==1&&listaLineasPazcal.get(listaLineasPazcal.size()-1)){ 
            //para no validar línea antes de end
            if(antesdeEnd){
                lineasEnumerada+="\n        error 0007 falta ; al final de la línea";
            }    
            
                
            
            
        }   
        if(instruccionPermitida==true&&palabraNopermitida!=true){
        if(lineaEvaluar.toLowerCase().matches("[ ]*(program).*")){
            
            if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]{0,14}[ ]*[(][ ]*(input)[ ]*[,][ ]*(output)[ ]*[)][ ]*[;][ ]*")){
                
                program=true;
            }else{
                if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]{0,14}[ ]*[(][ ]*(input|output)[ ]*[)][ ]*[;][ ]*")){
                    
                    program=true;
                }else{
                    if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*[;][ ]*")){
                        
                        program=true;
                    }else{
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]{0,14}.*")!=true){
                            lineasEnumerada+="\n        error 0024 falta identificador";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]{0,15}.*")==true){
                            lineasEnumerada+="\n        error 0025 identificador supera el tamaño permitido";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*(input)[ ]*[,][ ]*(output)[ ]*[)][ ]*[;][ ]*")==true){
                            
                            lineasEnumerada+="\n        error 0026 falta (";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*[(][ ]*(input)[ ]*[,][ ]*(output)[ ]*[;][ ]*")==true){
                            lineasEnumerada+="\n        error 0027 falta )";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*[(][ ]*(input)[ ]*(output)[ ]*[)][ ]*[;][ ]*")){
                            lineasEnumerada+="\n        error 0028 falta ,";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*[(][ ]*(input|output)[ ]*[,][ ]*[)][ ]*[;][ ]*")==true){                      
                            lineasEnumerada+="\n        error 0029 falta,";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*[(][ ]*[)][ ]*[;][ ]*")==true){
                            
                            lineasEnumerada+="\n        error 0030 no deben venir los paréntesis ( )";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*")==true){
                            lineasEnumerada+="\n        error 0031 falta ;";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*[(][ ]*(input)[ ]*[,][ ]*(output)[ ]*[)][ ]*")==true){
                            lineasEnumerada+="\n        error 0032 falta ;";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*(program)[ ]+[A-Za-z][A-Za-z0-9]*[ ]*[(][ ]*(input|output)[ ]*[)][ ]*")==true){ 
                            lineasEnumerada+="\n        error 0033 falta ;";
                        }
                    }
                }
            }
        }
        if(lineaEvaluar.toLowerCase().matches("[ ]*(var)[ ]*")){
            var=true;
            
        }else{
            if(lineaEvaluar.toLowerCase().matches(".*(var).*")){
                lineasEnumerada+="\n        error 0034 VAR debe estar solo";
            }
        }
        if(var==true){
            if(t1.size()>0){
                
                for (String definido : t1) {
                    String tk[]=definido.split(",");
                    if(tk[1].toLowerCase().equalsIgnoreCase(identificador)){
                        if(tk[1].toLowerCase().equalsIgnoreCase(identificador.toLowerCase())==true
                        &&lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[:][ ]*(integer|real|char)[ ]*[;][ ]*")==true){
                            lineasEnumerada+="\n        error 0035 este identificador ya fue definido";
                        }
                        identDefinido=true;
                    }
                }
                //hasta aqui
               
            }
            if (identDefinido!=true) {
                    
                    if(lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[:][ ]*(integer|real|char)[ ]*[;][ ]*")){
                         if(lineaEvaluar.toLowerCase().contains("integer")){
                             t1.add(nLinea+","+identificador+","+"INTEGER,"+"null,");
                             identDefinido=true;
                             identDeclarados.add(identificador);
                         }else{
                             if(lineaEvaluar.toLowerCase().contains("real")){
                                 t1.add(nLinea+","+identificador+","+"REAL,"+"null,");
                                 identDefinido=true;
                                 identDeclarados.add(identificador);
                             }else{
                                 if(lineaEvaluar.toLowerCase().contains("char")){
                                     t1.add(nLinea+","+identificador+","+"CHAR"+","+"null,");
                                     identDefinido=true;
                                     identDeclarados.add(identificador);
                                 }
                             }
                         }
                    }else{
                        if(lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{15,100}[ ]*[:][ ]*(integer|real|char)[ ]*[;][ ]*")==true){
                            lineasEnumerada+="\n        error 0036 el identificador supera el tamaño permitido";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*[:][ ]*(integer|real|char)[ ]*[;][ ]*")==true){
                            lineasEnumerada+="\n        error 0037 falta el identificador";
                        }
                        
                        if(lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*(integer|real|char)[ ]*[;][ ]*")==true){
                            lineasEnumerada+="\n        error 0038 falta :";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[:][ ]*[;][ ]*")){
                            lineasEnumerada+="\n        error 0039 falta el tipo de valor del identificador, pueden ser: INTEGER, REAL o CHAR";
                        }
                        if(lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[:][ ]*(integer|real|char)[ ]*")){
                            lineasEnumerada+="\n        error 0040 falta ;";
                        }
                        
                        if(lineaEvaluar.toLowerCase().matches("[ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[:][ ]*[A-Za-z][A-Za-z0-9]{0,14}[ ]*[;][ ]*")){
                            if (lineaEvaluar.toLowerCase().contains("integer")==false&&lineaEvaluar.toLowerCase().contains("real")==false
                              &&lineaEvaluar.toLowerCase().contains("char")==false) {
                                lineasEnumerada+="\n        error 0041 tipo de dato nopermitido";
                            }
                        }
                    }
                }
        }else{
            if(lineaEvaluar.matches("[ ]*[A-Za-z][A-Za-z0-9]*[:].*")){
                lineasEnumerada+="\n        error 0042 no se pueden definir variables porque falta VAR";
            }
        }
        
      }
        
       for (String IdenAct : identificadoresActuales) {
            for (String t : t1) {
                String arreglo[]=t.split(",");
                if(IdenAct.toLowerCase().equalsIgnoreCase(arreglo[1])){
                   if(arreglo[3].toLowerCase().equalsIgnoreCase("null")!=true){
                      lineaEvaluar=lineaEvaluar.replace(IdenAct,arreglo[3]);
                   }
                    //lineaEvaluar=lineaEvaluar.replace(IdenAct,arreglo[3]);//para poder hacer las operaciones se cambia la variable por su valor
                   
                }
            }
        }
        //aquí van las nuevas funcionalidades
        if(lineaEvaluar.toLowerCase().matches(".*[:][=].*")){//para asignar variables
            lineasEnumerada=encontrarTiposID(lineaEvaluar,lineasEnumerada);
            
        }
        if(listaLineasPazcal.size()==nLinea&&encontrarEND==false){
            System.out.println("error no se encontro END");
        }
        
        
        return lineasEnumerada;
    }
    public void reemplazar(String valor){//Para cambiar el valor de la variable cuando se le asigna algún valor
        String nuevoValor="";
        
        if(identificadoresActuales.size()>0){
            for (int i=0;i<t1.size();i++) {
                String arregloT[]=t1.get(i).split(",");
                if(arregloT[1].toLowerCase().equalsIgnoreCase(identificadoresActuales.get(0))){
                    
                    if(arregloT[3].toLowerCase().equalsIgnoreCase("null")){
                        arregloT[3]=arregloT[3].toLowerCase().replace("null",valor+",");
                        arregloT[0]+=","+arregloT[1]+","+arregloT[2]+","+arregloT[3];
                        
                        t1.set(i, arregloT[0]);
                        
                    }else{
                        arregloT[3]=arregloT[3].toLowerCase().replace( arregloT[3],valor+",");
                        arregloT[0]+=","+arregloT[1]+","+arregloT[2]+","+arregloT[3];
                        t1.set(i, arregloT[0]);
                    }
                }
            }
        }
        
    }
    public String reemplazaVariablePorValor(String id){//Para cambiar el nombre de variable por su valor
       
        for(int i=0;i<t1.size();i++){
            String arregloT[]=t1.get(i).split(",");
            if(arregloT[1].toLowerCase().equalsIgnoreCase(id)){
                arregloT[3]=arregloT[3].replace(",","");
                
                return arregloT[3];
               
            }
        }
        return "null";
    }
    public String encontrarTiposID(String expresion, String lineasEnumerada){//para encontrar el tipo de valor de un ID
        
        int nIdent=0;
        String idAsignacion="";
        String tipoID="";
        String valorID=" ";
        String aux="";//"[ ]*[=][ ]*([A-Za-z][A-Za-z0-9]*|[-]*[0-9]+|[-]*[0-9]+[.][0-9]+|['].*['])[;][ ]*"
        //idAsignacion=expresion.replaceAll(".*[=].*","");
        
        if(identificadoresActuales.size()>0){
            idAsignacion=identificadoresActuales.get(0);
        }
        for (String t : t1) {
            String informacionID[]=t.split(",");
            if(idAsignacion.toLowerCase().equalsIgnoreCase(informacionID[1])){
                tipoID=informacionID[2];
                typeValor=tipoID;// para saber que tipo es de manera global, en todo el programa
                
            }
        }
        
        if(identificadoresActuales.size()>1){
            boolean estado=true;
            for (String idActual : identificadoresActuales) {//este for es para poder obtener los tipos de dato de cada identificador, para eso son los dos ciclos for
                nIdent++;
                for (String t : t1) {
                    String informacionID[]=t.split(",");
                   
                    if(idActual.toLowerCase().equalsIgnoreCase(informacionID[1])&&idActual.toLowerCase().equalsIgnoreCase(idAsignacion)!=true){
                        if(informacionID[3].toLowerCase().equalsIgnoreCase("null")!=true){
                            
                            resolver=true;
                        }
                        if(tipoID.toLowerCase().equalsIgnoreCase(informacionID[2])!=true){
                            lineasEnumerada+="\n        error 0043 identificador"+idActual+" no cumple con el tipo de dato "+tipoID;
                            System.out.println("error identificador "+idActual+" no cumple con el tipo de dato "+tipoID);
                            
                        }else{
                            
                            int tamanio=1;
                            String nuevaExpresion="";
                            String variable="";
                            //String aux="";
                            nuevaExpresion=expresion;
                            while(tamanio<identificadoresActuales.size()){ 
                                variable=reemplazaVariablePorValor(identificadoresActuales.get(tamanio));
                                
                                aux=identificadoresActuales.get(tamanio);
                                
                                if(aux.length()==1){
                                    //System.err.println(variable);
                                    nuevaExpresion=expresion.replaceAll("[+|-|*|/]"+"[(]*["+aux+"]{1,1}[)]*|"+"[(]*["+aux+"]{1,1}[)]*[+|-|/|*]",variable);
                                }else{
                                    nuevaExpresion=nuevaExpresion.replaceAll(identificadoresActuales.get(tamanio), variable);
                                    
                                    
                                }
                                
                                tamanio++;
                            }
                            if(expresion.toLowerCase().matches(".*([+|-|*|/])+.*")!=true){
                                nuevaExpresion=nuevaExpresion.replaceAll(".*[:][=]","");
                                nuevaExpresion=nuevaExpresion.replace(";","");
                                nuevaExpresion=nuevaExpresion.replace(" ","");
                                reemplazar(nuevaExpresion);
                                
                            }else{
                                
                                if(nIdent==identificadoresActuales.size()){//Cuando ya se tengas todos los tipos del dato se realiza la expresión aritmetica
                                     
                                    nuevaExpresion=nuevaExpresion.replaceAll(".*[ ]*[:][=]","");//estos replaces para dejar solo la parte derecha de la expresión
                                    nuevaExpresion=nuevaExpresion.replace(" ","");
                                    
                                    
                                    if(resolver){
                                        aux="";
                                        lineasEnumerada=pasarPostfijo(nuevaExpresion,lineasEnumerada);
                                        if(typeValor.toLowerCase().equalsIgnoreCase("INTEGER")){
                                            int valorConvertido=Integer.parseInt(Resultado);
                                            if(valorConvertido<-32768||valorConvertido>32767){
                                                lineasEnumerada+="\n        error 0044 el valor INTEGER está fuera del rango permitido";
                                            }else{
                                               
                                               
                                            }
                                            reemplazar(Resultado);
                                        }
                                        if(typeValor.toLowerCase().equalsIgnoreCase("REAL")){
                                            double valorConvertido=Double.parseDouble(Resultado);
                                            if(valorConvertido<-100000000||valorConvertido>100000000){
                                                lineasEnumerada+="\n        error 0045 el valor REAL está fuera del rango permitido";
                                            }else{
                                                //reemplazar(Resultado);
                                            }
                                            reemplazar(Resultado);
                                        }
                                        
                                        Resultado="";
                                        
                                    }
                                    //estado=false;
                                }
                                
                            }
                            
                        }//termina aquí cuidado
                    }
                   
                }
                
                
            }
        }else{
              
            if(tipoID.toLowerCase().equalsIgnoreCase("INTEGER")){
                if(expresion.toLowerCase().matches(".*([-]*[0-9]+[.][0-9]+|['].*['])+.*")){
                    lineasEnumerada+="\n        error 0046 valor no es del tipo INTEGER";
                    System.out.println("error valor no es del tipo int");
                }else{
                     
                    int valorConvertido=0;
                    if(expresion.toLowerCase().matches("[^/+*-]*[-]*[0-9]+[^/+*-]*")){//se cambió
                        
                        String valor="";
                        valor=expresion.replaceAll(".*[=]","");
                        valor=valor.replaceAll("[;]","");
                        valor=valor.replace(" ","");
                        valorConvertido=Integer.parseInt(valor);
                        if(valorConvertido<-32768||valorConvertido>32767){
                             
                            lineasEnumerada+="\n        error 0047 el valor INTEGER está fuera del rango permitido";
                        }else{
                            //reemplazar(valor);
                        }
                        reemplazar(valor);
                    }else{
                        
                        if (expresion.toLowerCase().matches(".*[:][=].*[+|-|*|/].*")) {
                            expresion=expresion.replaceAll(".*[=]","");
                            expresion=expresion.replace(";","");
                            expresion=expresion.replace(" ","");
                            lineasEnumerada=pasarPostfijo(expresion,lineasEnumerada);
                            valorConvertido=Integer.parseInt(Resultado);
                            if(valorConvertido<-32768||valorConvertido>32767){
                                lineasEnumerada+="\n        error 0047 el valor INTEGER está fuera del rango permitido";
                            }else{
                                //reemplazar(Resultado);
                            }
                            reemplazar(Resultado);
                        }
                    }
                }
            }else{
                if (tipoID.toLowerCase().equalsIgnoreCase("REAL")) {
                    if(expresion.toLowerCase().matches("[^.]*([-]*[(]*[-]*[0-9]+[)]*|['].*['])+[^.]*")||expresion.toLowerCase().matches(".*[-]*[0-9]*[.]+[^0-9]*")||expresion.toLowerCase().matches("[^0-9]*[.]+[-]*[0-9]*")){
                        lineasEnumerada+="\n        error 0048 valor no es del tipo REAL";
                        System.out.println("error valor no es del tipo real");
                    }else{
                        double valorConvertido=0;
                        if(expresion.toLowerCase().matches(".*[:][=][ ]*[(]*[-]*[0-9]+[.][0-9]+[)]*[;]*[ ]*")){
                            String valor="";
                            valor=expresion.replaceAll(".*[:][=]","");
                            valor=valor.replaceAll("[;]","");
                            valor=valor.replace(" ","");
                            valorConvertido=Double.parseDouble(valor);
                            if(valorConvertido<-100000000||valorConvertido>100000000){
                                 lineasEnumerada+="\n        error 0049 el valor REAL está fuera del rango permitido";
                            }else{
                                //reemplazar(valor);
                            }
                            reemplazar(valor);
                        }else{
                            if(expresion.toLowerCase().matches(".*[(]*[-]*[0-9]+[.][0-9]+[)]*[+|-|*|/]+.*")){
                                expresion=expresion.replaceAll(".*[=]","");
                                expresion=expresion.replace(";","");
                                expresion=expresion.replace(" ","");
                                if(expresion.toLowerCase().matches(".*[-]*[0-9]+[.][0-9]+.*")){
                                    
                                    lineasEnumerada=pasarPostfijo(expresion,lineasEnumerada);
                                    valorConvertido=Double.parseDouble(Resultado);
                                    if(valorConvertido<-100000000||valorConvertido>100000000){
                                         lineasEnumerada+="\n        error 0049 el valor REAL está fuera del rango permitido";
                                    }else{
                                        //reemplazar(Resultado);
                                    }
                                    reemplazar(Resultado);
                                }else{
                                    //System.out.println("error valor no es del tipo real%%% "+Resultado);
                                }
                                
                               
                                
                            }
                        }
                    }
                }else{
                    if(tipoID.toLowerCase().equalsIgnoreCase("CHAR")){
                        
                        if(expresion.toLowerCase().matches("([^'][^'])*([-]*[0-9]+|[-]*[0-9]+[.]{1,1}+[0-9]+)+([^'].*[^'])*")||expresion.toLowerCase().matches(".*['].{2,150}['].*")){
                            lineasEnumerada+="\n        error 0050 valor no es del tipo CHAR";
                           
                        }else{
                            if(expresion.toLowerCase().matches(".*[=]([0-9]+|[0-9]+[.][0-9]+)[;][ ]*")){
                                String valor="";
                                valor=expresion.replaceAll(".*[=]","");
                                valor=valor.replaceAll("[;]","");
                                if(valor.toLowerCase().matches("['].[1,1][']")){
                                    reemplazar(valor);
                                }
                                reemplazar(valor);
                                
                            }
                        }
                    }
                }
            }
        }
        
        return lineasEnumerada;
    }
    public String pasarPostfijo(String expresion,String lineasEnumerada){
        Stack pila = new Stack();// si la expresion tiene valores negativos entonces se debe agregar ceros para hacer la expresion postfijo
        String operador1="";
        String operador2="";
        String postfija="";
        String aux="";
        int contador=0;
        char c=' ';
         String nuevaExpresion="";
        if(expresion.toLowerCase().matches("[(].*[)]")){
            
            char arreglo[]=expresion.toCharArray();
            for(int i=0;i<arreglo.length;i++){
                if(i!=0&&i!=arreglo.length-1){
                    nuevaExpresion+=arreglo[i];
                }
                
            }
        }
       
        if(expresion.toLowerCase().matches(".*([+]|[*]|[/]){2,150}.*")){//operadores concatenados no son permitidos
            lineasEnumerada+="\n        error 0051 no se permite la concatenación de operadores";
            
        }else{
            nuevaExpresion=expresion.replace("--","+");
            nuevaExpresion=nuevaExpresion.replace("+-", "-");
            expresion=nuevaExpresion;
        }
        if(expresion.toLowerCase().matches(".*([,]*[(][^-+*/]*[)][,]){2,150}.*")){
            lineasEnumerada+="\n        error 0052 falta un operador";
            
        }
        if(expresion.toLowerCase().matches("[ ]*([+|-|*|/])+.*")||expresion.toLowerCase().matches(".*([+|-|*|/])[ ]*")){
            lineasEnumerada+="\n        error 0053 falta un operando";
        }
        if(expresion.toLowerCase().matches("[ ]*[-]([0-9]+|[0-9]+[.][0-9]+).*")){
            String cero="0,";
            cero+=expresion;
            expresion=cero;
        }
        if(expresion.toLowerCase().matches("[(].*[)]")){
            
            char arreglo[]=expresion.toCharArray();
            for(int i=0;i<arreglo.length;i++){
                if(i!=0&&i!=arreglo.length-1){
                    nuevaExpresion+=arreglo[i];
                }
                
            }
        }
        expresion=nuevaExpresion;
        expresion=expresion.replace("+",",+,");
        expresion=expresion.replace("*",",*,");
        expresion=expresion.replace("-",",-,");
        expresion=expresion.replace("/",",/,");
        expresion=","+expresion+",";
        
       //pasar a postfijo
        char arreglo[]=expresion.toCharArray();
        while(contador<arreglo.length){
            c=arreglo[contador];
            aux=String.valueOf(c);
            if(contador<arreglo.length){
                if(aux.toLowerCase().matches("[0-9]*")||aux.toLowerCase().matches("[.]")||aux.toLowerCase().matches("[A-za-z]")||aux.toLowerCase().equalsIgnoreCase(",")){
                    postfija+=aux;
                }else{
                    if(pila.isEmpty()){
                        pila.push(aux);
                    }else{
                        
                        if(aux.toLowerCase().equalsIgnoreCase("(")){
                            pila.push(aux);
                        }else{
                           
                            if(aux.toLowerCase().matches("([+]|[-])")){
                                operador1=String.valueOf(pila.peek());
                                if(operador1.toLowerCase().matches("([/]|[*])")){
                                    postfija+=String.valueOf(pila.pop());
                                    pila.push(aux);
                                }else{
                                    
                                    if(operador1.toLowerCase().matches("([+]|[-])")){
                                        operador1=String.valueOf(pila.pop());
                                        postfija+=operador1;
                                        pila.push(aux);
                                        
                                    }else{
                                        operador1=String.valueOf(pila.peek());
                                        
                                        if(operador1.toLowerCase().matches("[(]")){
                                            pila.push(aux);
                                        }
                                    }
                                }
                            }else{
                                if(aux.toLowerCase().matches("([/]|[*])")){
                                    operador1=String.valueOf(pila.peek());
                                    if(operador1.toLowerCase().matches("[(]")){
                                        pila.push(aux);
                                    }else{
                                        if(operador1.toLowerCase().matches("([+]|[-])"))
                                            pila.push(aux);
                                    }
                                }else{
                                    if(aux.toLowerCase().matches("[)]")){
                                        operador1="";
                                        while(true){
                                            if(pila.peek().equals("(")!=true){
                                                postfija+=pila.pop();
                                            }else{
                                                pila.pop();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                while(pila.isEmpty()!=true){
                    postfija+=String.valueOf(pila.pop());
                }
            }
           
            
           contador++;
        }
        while(pila.isEmpty()!=true){
            postfija+=String.valueOf(pila.pop());
        }
        
        
        lineasEnumerada=resolverPostfija(postfija,lineasEnumerada);
        return lineasEnumerada;
    }
    public String resolverPostfija(String expresion, String lineasEnumerada){
       
        String tipo="";
        tipo=typeValor;
        //System.out.println(tipo);
        //String info="valo1,5";
        Stack pila = new Stack();
        String operador1="";
        String operador2="";
        String aux="";
        String resultado=" ";
        char c=' ';
        int indice=0;
        char arreglo[]=expresion.toCharArray();
        while(indice<arreglo.length){
            c=arreglo[indice];
            aux=String.valueOf(c);
            if(indice<arreglo.length){
                if(aux.toLowerCase().matches("[+|-|*|/]")){
                    switch(tipo){
                        case "INTEGER":
                            if(aux.toLowerCase().equalsIgnoreCase("+")){
                                
                                
                                //operador2=String.valueOf(pila.pop());
                                operador2=encontrarComa(pila);
                                int op2=Integer.parseInt(operador2);
                                //operador1=String.valueOf(pila.pop());
                                operador1=encontrarComa(pila);
                                int op1=Integer.parseInt(operador1);
                                int nuevoValor=op1+op2;
                                resultado=String.valueOf(nuevoValor);
                                pila.push(",");
                                pila.push(resultado);
                                pila.push(",");
                            }else{
                                if(aux.toLowerCase().equalsIgnoreCase("-")){
                                    operador2=encontrarComa(pila);
                                    int op2=Integer.parseInt(operador2);
                                    operador1=encontrarComa(pila);
                                    int op1=Integer.parseInt(operador1);
                                    int nuevoValor=op1-op2;
                                    resultado=String.valueOf(nuevoValor);
                                    pila.push(",");
                                    pila.push(resultado);
                                    pila.push(",");
                                }else{
                                    if(aux.toLowerCase().equalsIgnoreCase("*")){
                                        operador2=encontrarComa(pila);
                                        int op2=Integer.parseInt(operador2);
                                        operador1=encontrarComa(pila);
                                        int op1=Integer.parseInt(operador1);
                                        int nuevoValor=op1*op2;
                                        resultado=String.valueOf(nuevoValor);
                                        pila.push(",");
                                        pila.push(resultado);
                                        pila.push(",");
                                    }else{
                                        if(aux.toLowerCase().equalsIgnoreCase("/")){
                                            operador2=encontrarComa(pila);
                                            int op2=Integer.parseInt(operador2);
                                            operador1=encontrarComa(pila);
                                            int op1=Integer.parseInt(operador1);
                                            System.out.println(op1);
                                            System.out.println(op2);
                                            if(op2!=0||op2!=0.0){
                                                int nuevoValor=op1/op2;
                                                resultado=String.valueOf(nuevoValor);
                                                pila.push(",");
                                                pila.push(resultado);
                                                pila.push(",");
                                            }else{
                                                lineasEnumerada+="\n        error 0054 no se puede dividir entre 0";
                                                //System.out.println("no se puede dividir entre cero");
                                                this.Resultado="0";
                                                 pila.clear();
                                                return lineasEnumerada;
                                               
                                            }
                                            
                                        }
                                    }
                                }
                            }
                            break;
                        case "REAL":
                            if(aux.toLowerCase().equalsIgnoreCase("+")){
                                operador2=encontrarComa(pila);
                                double op2=Double.parseDouble(operador2);
                                operador1=encontrarComa(pila);
                                double op1=Double.parseDouble(operador1);
                                double nuevoValor=op1+op2;
                                resultado=String.valueOf(nuevoValor);
                                pila.push(",");
                                pila.push(resultado);
                                pila.push(",");
                            }else{
                                if(aux.toLowerCase().equalsIgnoreCase("-")){
                                    operador2=encontrarComa(pila);
                                    double op2=Double.parseDouble(operador2);
                                    operador1=encontrarComa(pila);
                                    double op1=Double.parseDouble(operador1);
                                    double nuevoValor=op1-op2;
                                    resultado=String.valueOf(nuevoValor);
                                    pila.push(",");
                                    pila.push(resultado);
                                    pila.push(",");
                                }else{
                                    if(aux.toLowerCase().equalsIgnoreCase("*")){
                                        operador2=encontrarComa(pila);
                                        double op2=Double.parseDouble(operador2);
                                        operador1=encontrarComa(pila);
                                        double op1=Double.parseDouble(operador1);
                                        double nuevoValor=op1*op2;
                                        resultado=String.valueOf(nuevoValor);
                                        pila.push(",");
                                        pila.push(resultado);
                                        pila.push(",");
                                    }else{
                                        if(aux.toLowerCase().equalsIgnoreCase("/")){
                                            operador2=encontrarComa(pila);
                                            double op2=Double.parseDouble(operador2);
                                            operador1=encontrarComa(pila);
                                            double op1=Double.parseDouble(operador1);
                                            if (op2!=0||op2!=0.0) {
                                                double nuevoValor=op1/op2;
                                                resultado=String.valueOf(nuevoValor);
                                                pila.push(",");
                                                pila.push(resultado);
                                                pila.push(",");
                                            }else{
                                                lineasEnumerada+="\n        error 0054 error no se puede dividir entre 0";
                                                System.out.println("no se puede dividir entre cero");
                                                this.Resultado="0.0";
                                                pila.clear();
                                                return lineasEnumerada;
                                                
                                            }
                                            
                                        }
                                    }
                                }
                            }
                            break;
                           
                    }
                }else{
                    pila.push(aux);
                }
            }
            indice++;
        }
        resultado="";
        while(pila.isEmpty()!=true) {            
            resultado+=pila.pop();//imprime resultado
        }
        resultado=resultado.replace(",","");
        this.Resultado=String.valueOf(resultado);
        System.out.println(resultado);//imprime resultado
        
        return lineasEnumerada;
    }
    public String encontrarComa(Stack pila){
        String operando="";
        String coma="";
        int contador=0;
        
        while(contador<2){
           
            if(pila.peek().equals(",")){
                coma=String.valueOf(pila.pop());
                contador++;
            }else{
                operando+=String.valueOf(pila.pop());
            }
        }
        
        //System.out.println(operando);
        /*if(operando.contains(".")){
            String arreglo[]=operando.split("[.]");
            operando=arreglo[1]+"."+arreglo[0];
        }*/
        char arreglo[]=operando.toCharArray();
        operando="";
        for(int i=arreglo.length-1;i>=0;i--){
            operando+=arreglo[i];
        }
       
        return operando;
    }
    public String descomponerNumeroLinea(int n){//para descomponer las líneas
        String cadena1= Integer.toString(n);// se convierte en string el valor integer que se le pasa al método como argumento
        char[]caracteres=cadena1.toCharArray();// se convierte la cadena en un arreglo de caracteres
        cadena1="";//se inicializa la cadena para agregar el nuevo valor
        for(int i=1; i<caracteres.length;i++){
            cadena1+=caracteres[i];// se empieza a agregar caracter por caracter a la cadena luego se retorna como un string ese sería el numero de línea
        }
        return cadena1;   
    }
    public void crearArchivoPas() throws IOException{
        List<String>listaErrores=new ArrayList();
        FileReader leerArchivoErrores=new FileReader("INFLACION-errores.txt");
        BufferedReader leer=new BufferedReader(leerArchivoErrores);
        String lineaErrores; 
        while ((lineaErrores=leer.readLine())!=null) {             
             listaErrores.add(lineaErrores);
         }
         for (String lineaError : listaErrores) {
             if(lineaError.contains("error")==true){
                 System.out.println("El archivo tiene errores");
                 System.exit(1);
             }
         }
        
        String nombreArchivo="pazcal";//así se llama el al archivo.pas
        nombreArchivo+=".pas";
        FileWriter archivoPas=new FileWriter(nombreArchivo);
        //File archivoPazcal=new File(this.nombreArchivo);
        for (String listaLineasArchivoPazcal1 : listaLineasPazcal) {
            archivoPas.write(listaLineasArchivoPazcal1+="\r\n");
        }
        archivoPas.close();
        String salida = null;
        String rutaArchivo;
        rutaArchivo=Paths.get("").toAbsolutePath().toString();
        rutaArchivo+="\\"+nombreArchivo;
        //String comando = "cmd /c pazcal.exe "+nombreArchivo;
        String comando = "cmd /c fpc.exe "+nombreArchivo;
        try{

            
            Process proceso = Runtime.getRuntime().exec(comando);
            //Runtime.getRuntime().exec( "cmd /c  C:\\Users\\User\\Desktop\\Proyecto-PAZCAL\\PAZCAL\\");
            InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
            BufferedReader stdInput = new BufferedReader(entrada);

            //Si el comando tiene una salida la mostramos
            if((salida=stdInput.readLine()) != null){//Esto lo tomé del ejemplo del profesor
            	//System.out.println("Comando ejecutado Correctamente");
            	while ((salida=stdInput.readLine()) != null){
                	//System.out.println(salida);
                }
            }else{
            	//System.out.println("No se a producido ninguna salida");
            }
        }catch (IOException e) {
                //System.out.println("Excepción: ");
                e.printStackTrace();
        }
        
    }
}
