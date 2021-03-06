/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Algoritmos.P2.*;
import Utils.Restricciones;
import Utils.listaTransmisores;
import Utils.rangoFrec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


/**
 *
 * @author alumno
 */
public class main {

    public static String DIRECTORIO;
    public static String TRABAJO;
    public static Integer LINEAS;
    public static Random NUMERO;
    public static Integer SEMILLAS[] = {3181827, 1818273, 8182731, 1827318, 8273181};
    
    //Variables para el menu
    static Scanner scanner = new Scanner(System.in);
    static int select = -1;

    /**
     * @param args the command line arguments
     */
    public static void main ( String[] args ) throws FileNotFoundException, IOException {
        
        int lineaInicial = 2;
        int columnaInicial = 1;
        int cuentaDirectorios = 0;
        
        NUMERO = new Random();

        TRABAJO = System.getProperty("user.dir");

        System.out.println("Conjunto de archivos que quiere usar: ");
        Scanner reader = new Scanner(System.in);
        
        DIRECTORIO = reader.nextLine();
        LINEAS = countLines(DIRECTORIO)+1;

        rangoFrec frecuencias = new rangoFrec();
        listaTransmisores transmisores = new listaTransmisores();
        Restricciones rest = new Restricciones();

        int cuentaArchivos = 0;
        float startTime;
        boolean inicio = true;
        float endTime;
        float duration;
        int contador = 0;
        int semilla = 0;
        select = -1;
        while( select != 0 ) {

            if ( semilla == 0 ) {
                NUMERO.setSeed(SEMILLAS[contador]);
            } else {
                NUMERO.setSeed(semilla);
            }

            try {
                System.out.print("Elige opción:\n"
                        + "1.- Generacional (2 Puntos)\n"
                        + "2.- Generacional (BLX)\n"
                        + "3.- Estacionario (2 Puntos)\n"
                        + "4.- Estacionario (BLX)\n"
                        + "5.- Greedy\n"
                        + "6.- Cambiar conjunto de archivos\n"
                        + "7.- Cambiar semilla\n "
                        + "0.- Salir"
                        + "\n: ");

                select = Integer.parseInt(scanner.nextLine());
                
                switch( select ) {
                    case 1:
                        System.out.println("Ejecucion G_2P de "+DIRECTORIO);
                        startTime = System.nanoTime();
                        Generacional.cruce = false;
                        Generacional generacional = new Generacional(transmisores, frecuencias, rest);
                        endTime = System.nanoTime();
                        
                        duration = (endTime - startTime) / 1000000000;
                        generacional.resMejorIndividuo();
                        System.out.println("Tiempo de ejecucion: " + duration + " segundos");
                        break;
                    case 2:
                        System.out.println("Ejecucion G_BLX de "+DIRECTORIO);
                        startTime = System.nanoTime();
                        Generacional.cruce = true;
                        generacional = new Generacional(transmisores, frecuencias, rest);
                        endTime = System.nanoTime();                        
                                
                        duration = (endTime - startTime) / 1000000000;
                        generacional.resMejorIndividuo();
                        System.out.println("Tiempo de ejecucion: " + duration + " segundos");
                        break;
                    case 3:
                        System.out.println("Ejecucion E_2P de "+DIRECTORIO);
                        startTime = System.nanoTime();
                        Estacionario.cruce = false;
                        Estacionario estacionario = new Estacionario(transmisores, frecuencias, rest);
                        endTime = System.nanoTime();
                        
                        duration = (endTime - startTime) / 1000000000;
                        estacionario.resMejorIndividuo();
                        System.out.println("Tiempo de ejecucion: " + duration + " segundos");
                        break;
                    case 4:
                        System.out.println("Ejecucion E_BLX de "+DIRECTORIO);
                        startTime = System.nanoTime();
                        Estacionario.cruce = true;
                        estacionario = new Estacionario(transmisores, frecuencias, rest);
                        endTime = System.nanoTime();

                        duration = (endTime - startTime) / 1000000000;
                        estacionario.resMejorIndividuo();
                        System.out.println("Tiempo de ejecucion: " + duration + " segundos");
                        break;
                    case 5:
                        System.out.println("Ejecución Greedy de "+DIRECTORIO);
                        startTime = System.nanoTime();
                        Greedy greedy = new Greedy (transmisores, frecuencias, rest);
                        endTime = System.nanoTime();
                        
                        duration = (endTime - startTime) / 1000000000;
                        greedy.resultados();
                        System.out.println("Tiempo de ejecución: "+ duration + " segundos");
                        break;
                    case 6:
                        System.out.println("Conjunto de archivos que quiere usar: ");

                        DIRECTORIO = reader.next();
                        LINEAS = countLines(DIRECTORIO) + 1;

                        frecuencias = new rangoFrec();
                        transmisores = new listaTransmisores();
                        rest = new Restricciones();
                        break;
                    case 7:
                        System.out.print("Nueva semilla: ");
                        semilla = reader.nextInt();
                        NUMERO.setSeed(semilla);
                        break;
                    case 0:
                        System.out.println("Fin");
                        break;
                    default:
                        System.out.println("Número no reconocido");
                        break;
                }

                System.out.println();
            } catch( Exception e ) {
                System.out.println("Uoop! Error! " + e.toString());
            }
            
            contador = Math.floorMod(contador + 1, 5);
        }
    }

    public static int countLines ( String filename ) throws IOException {
        String archivo = "var.txt";
        if ( filename.matches("scen.*") ) {
            archivo = archivo.toUpperCase();
        }
        File file = new File(main.TRABAJO + "/conjuntos/" + main.DIRECTORIO + "/" + archivo);
        Scanner lineas = new Scanner(file);
        int ultimoTransmisor = 0;
        while( lineas.hasNextLine() ) {
            ultimoTransmisor = lineas.nextInt();
            String ultimaLinea = lineas.nextLine();
        }
        return ultimoTransmisor;
    }
}
