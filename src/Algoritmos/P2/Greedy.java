/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algoritmos.P2;

import Utils.Restricciones;
import static Utils.Utilidades.compruebaTransmisores;
import static Utils.Utilidades.rDiferencia;
import Utils.listaTransmisores;
import Utils.rangoFrec;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static main.main.NUMERO;

/**
 *
 * @author psfue
 */
public class Greedy {
    
    List<List<Integer>> frecuencias = new ArrayList<>();
    List<Integer> transmisores = new ArrayList<>();
    Restricciones restricciones;
    List<Integer> frecuenciasR = new ArrayList<>();

    int resultado;

    public Greedy ( listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _rest  ) throws FileNotFoundException{
        frecuencias = _frecuencias.rangoFrecuencias;
        transmisores = _transmisores.transmisores;
        restricciones = _rest;
        
        algoritmo();
    }
    
    public void algoritmo () throws FileNotFoundException {
        for ( int i = 0; i < transmisores.size(); i ++ ) {
                    frecuenciasR.add(0);
                }

                Random numero = NUMERO;
                int seleccionado = numero.nextInt(transmisores.size());

                int tamanio = frecuencias.get(transmisores.get(seleccionado)).size();
                int frecuenciaRandom = frecuencias.get(transmisores.get(seleccionado)).get(numero.nextInt(tamanio));
                frecuenciasR.set(seleccionado, frecuenciaRandom);

                List<List<Integer>> listaRestric = new ArrayList<>();
                int transmisor = 0;
                boolean fin = false;
                while( transmisor < transmisores.size() ) {
                    listaRestric = restricciones.restriccionesTransmisor(transmisor);
                    if ( transmisor != seleccionado && listaRestric.size() > 0 ) {

                        int minimo = Integer.MAX_VALUE;
                        boolean encontrado = false;
                        int frecuenciaR = 0;
                        int frecuencia;
                        int pos = 0;

                        int valor = 0; //Sacado del bucle while

                        while( pos < frecuencias.get(transmisores.get(transmisor)).size() &&  ! encontrado ) {

                            List<Integer> nuevaLista = new ArrayList<>();
                            nuevaLista.addAll(frecuenciasR);

                            frecuencia = frecuencias.get(transmisores.get(transmisor)).get(pos);
                            nuevaLista.set(transmisor, frecuencia);
                            List<List<Integer>> listaRest = compruebaTransmisores(transmisor, restricciones, frecuenciasR);

                            if ( listaRest.size() > 0 ) { // Lista no vacía, se selecciona frecuencia que afecte lo menos posible al resultado

                                valor = rDiferencia(nuevaLista, listaRest);
                                if ( valor < minimo ) {
                                    minimo = valor;
                                    frecuenciaR = frecuencia;
                                    if ( valor == 0 ) // Si la suma de todas las restricciones = 0 entonces es el mejor resultado posible
                                    {
                                        encontrado = true;
                                    }
                                }
                            } else { // En caso de que la lista este vacía no hay restricciones que se puedan satisfacer -> frecuencia aleatoria

                                tamanio = frecuencias.get(transmisores.get(transmisor)).size();
                                frecuenciaR = frecuencias.get(transmisores.get(transmisor)).get(numero.nextInt(tamanio));
                                valor = 0;
                                encontrado = true;
                            }
                            pos ++;
                        }
                        frecuenciasR.set(transmisor, frecuenciaR);
                    }
                    transmisor ++;
                }
                resultado = rDiferencia(frecuenciasR, restricciones); 
        }

    public void resultados () {
        for ( int i = 0; i < frecuenciasR.size(); i++ ) {
            if ( frecuenciasR.get(i) != 0 ) {
                System.out.println("Transmisor " + (i + 1) + ": " + frecuenciasR.get(i));
            }            
        }
        
        System.out.println("Resultado: "+resultado);
    }
    
}