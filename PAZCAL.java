/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pazcal;

import java.io.IOException;

/**
 *
 * @author Josué
 */
public class PAZCAL {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //TODO code application logic here
        //String nombreArchivo=args[0];
        String nombreArchivo="bueno.pazcal";
        AnalizadorLexico analizadorLexico=new AnalizadorLexico(nombreArchivo);
        analizadorLexico.iniciarAnalizadorLexico();
    }
    
}
