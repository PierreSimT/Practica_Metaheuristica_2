/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algoritmos.P2;

import Utils.Restricciones;
import Utils.listaTransmisores;
import Utils.rangoFrec;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static main.main.NUMERO;

/**
 *
 * @author ptondreau
 */
public class Generacional {
    
    List<List<Integer>> frecuencias = new ArrayList<> ();
    List<Integer> transmisores = new ArrayList<> ();
    List<Integer> frecuenciasR = new ArrayList<>();
    Restricciones restricciones;
    int resultado;
    
    public Generacional(listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _rest ) throws FileNotFoundException {
        frecuencias = _frecuencias.rangoFrecuencias;
        transmisores = _transmisores.transmisores;
        restricciones = _rest;
        
        for ( int i = 0; i < transmisores.size(); i++ ) {
            frecuenciasR.add(0);
        }
        
        greedyInicial();
    }
    
    void greedyInicial () throws FileNotFoundException {

        Random numero = NUMERO;
        int seleccionado = numero.nextInt(transmisores.size());
        
        System.out.println("Transmisor seleccionado: "+seleccionado);
        
        List<List<Integer>> listaRestric = new ArrayList<>();
        int transmisor = 0;
        boolean fin = false;
        while( transmisor < transmisores.size()) {
            listaRestric = restricciones.restriccionesTransmisor(transmisor);
            if (  transmisor != seleccionado && listaRestric.size() > 0 ) {

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
                    List<List<Integer>> listaRest = compruebaTransmisores(transmisor);

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

                        int tamanio = frecuencias.get(transmisores.get(transmisor)).size();
                        frecuenciaR = frecuencias.get(transmisores.get(transmisor)).get(numero.nextInt(tamanio));
                        valor = 0;
                        encontrado = true;
                    }
                    pos ++;
                }
                frecuenciasR.set(transmisor, frecuenciaR);
            }
            transmisor++;
        }
        resultado = rDiferencia(frecuenciasR, restricciones);
    }
    
        /**
     * Funcion que devuelve una lista con las restricciones que puede satisfacer
     * un transmisor
     *
     * @param transmisor
     * @return
     * @throws FileNotFoundException
     */
    private List<List<Integer>> compruebaTransmisores ( int transmisor ) throws FileNotFoundException {
        int contador = 0;
        List<List<Integer>> listaRest = new ArrayList<>();
        List<List<Integer>> listaT = restricciones.restriccionesTransmisor(transmisor);
        for ( int i = 0; i < listaT.size(); i ++ ) {
            if ( frecuenciasR.get(listaT.get(i).get(0) - 1) != 0 || frecuenciasR.get(listaT.get(i).get(1) - 1) != 0 ) {
                listaRest.add(new LinkedList<>());
                listaRest.get(contador ++).addAll(listaT.get(i));
            }
        }

        return listaRest;
    }
    
    private int rDiferencia ( List<Integer> valores, List<List<Integer>> rest ) {
        int total = 0;
        for ( int i = 0; i < rest.size(); i ++ ) {
            int tr1 = rest.get(i).get(0);
            int tr2 = rest.get(i).get(1);
            int diferencia = rest.get(i).get(2);
            int result = rest.get(i).get(3);

            if ( Math.abs(valores.get(tr1 - 1) - valores.get(tr2 - 1)) > diferencia ) {
                total += result;
            }

        }

        return total;
    }

    public int rDiferencia ( List<Integer> valores, Restricciones rest ) throws FileNotFoundException {

        int total = 0;
        for ( int i = 0; i < rest.restricciones.size(); i ++ ) {
            int tr1 = rest.restricciones.get(i).get(0);
            int tr2 = rest.restricciones.get(i).get(1);
            int diferencia = rest.restricciones.get(i).get(2);
            int result = rest.restricciones.get(i).get(3);

            if ( Math.abs(valores.get(tr1 - 1) - valores.get(tr2 - 1)) > diferencia ) {
                total += result;
            }

        }

        return total;
    }

    /**
     * Calcula el resultado del problema a minimizar
     *
     * @param valores Valores de los transmisores
     * @param cambioTransmisor Transmisor al que se le aplico un cambio de
     * frecuencia
     * @param rest Restricciones a evaluar
     * @return
     * @throws FileNotFoundException
     */
    public int rDiferencia ( List<Integer> valores, int cambioTransmisor, Restricciones rest ) throws FileNotFoundException {

        List<List<Integer>> listaRest = new ArrayList<>();
        listaRest = rest.restriccionesTransmisor(cambioTransmisor);

        int total = 0;
        for ( int i = 0; i < listaRest.size(); i ++ ) {

            int tr1 = listaRest.get(i).get(0);
            int tr2 = listaRest.get(i).get(1);

            if ( tr1 == cambioTransmisor + 1 || tr2 == cambioTransmisor + 1 ) {
                int diferencia = listaRest.get(i).get(2);
                int result = listaRest.get(i).get(3);

                if ( Math.abs(valores.get(tr1 - 1) - valores.get(tr2 - 1)) > diferencia ) {
                    total += result;
                }

            }

        }

        return total;
    }
    
    public void resultados () {

        List<List<Integer>> listaTrans = new ArrayList<>();
        for ( int i = 0; i < transmisores.size(); i ++ ) {
            listaTrans = restricciones.restriccionesTransmisor(i);
            if ( listaTrans.size() > 0 ) {
                System.out.println("Transmisor " + (i + 1) + ": " + frecuenciasR.get(i));
            }
        }
        System.out.println("Coste: " + resultado);
    }
}
