/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algoritmos.P2;

import static Utils.Utilidades.*;
import Utils.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static main.main.NUMERO;

/**
 *
 * @author ptondreau
 */
public class Generacional {

    final int numParejas = 18;
    public static boolean cruce;
    
    List<List<Integer>> frecuencias = new ArrayList<>();
    List<Integer> transmisores = new ArrayList<>();
    Restricciones restricciones;

    int[] resultado = new int[ 50 ];
    List<List<Integer>> padres = new ArrayList<>();
    List<List<Integer>> hijos = new ArrayList<>();
    List<Integer> transmisoresPosibles = new ArrayList<>();

    int idMejorResult;
    int mejorResult = Integer.MAX_VALUE;
    int idMutado;

    int numGeneraciones = 0;
    int numEvaluaciones = 0;

    public Generacional ( listaTransmisores _transmisores, rangoFrec _frecuencias, Restricciones _rest ) throws FileNotFoundException {
        frecuencias = _frecuencias.rangoFrecuencias;
        transmisores = _transmisores.transmisores;
        restricciones = _rest;

        for ( int i = 0; i < 50; i ++ ) {
            padres.add(new ArrayList<>());
        }

        for ( int i = 0; i < 50; i ++ ) {
            greedyInicial(i);
        }

        //Loop hasta 20000 evaluaciones
        while( numEvaluaciones < 20000 ) {
            System.out.print((numEvaluaciones) + " : ");
            generarHijos();
            cruzarIndividuos();
            mutarIndividuos();
            nuevaGeneracion();
            System.out.println();
        }

    }

    void greedyInicial ( int id ) throws FileNotFoundException {

        List<Integer> frecuenciasR = new ArrayList<>();

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
        resultado[ id ] = rDiferencia(frecuenciasR, restricciones);
        padres.get(id).addAll(frecuenciasR);
        frecuenciasR.clear(); // Borra todos los elementos anteriores para nueva solucion
    }

    void generarHijos () {
        for ( int i = 0; i < 50; i ++ ) {
            Random numero = NUMERO;
            int seleccionado = numero.nextInt(50);

            Random numero2 = NUMERO;
            int seleccionado2 = numero.nextInt(50);

            if ( resultado[ seleccionado ] < resultado[ seleccionado2 ] ) {
                hijos.add(i, padres.get(seleccionado));

            } else {
                hijos.add(i, padres.get(seleccionado2));
            }
        }
    }

    void cruzarIndividuos () {
        int cont = 0;
        while( cont < numParejas ) {
            int individuo1 = cont;
            int individuo2 = cont + 1;

            if ( !cruce )
                algCruce2Puntos(individuo1, individuo2);
            else algBX(individuo1, individuo2);
            cont += 2;
        }

    }

    // No estoy seguro de si habría que hacerla así.
    final double alfa = 0.5;
    void mutarIndividuos () {
        //Mutamos solo un individuo

        //Seleccionamos el individuo a mutar
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(50);

        int transmisorMut = numero.nextInt(hijos.get(seleccionado).size());
        int frecAsociada = transmisores.get(transmisorMut);

        int frecuenciaMut = numero.nextInt(frecuencias.get(frecAsociada).size());
        hijos.get(seleccionado).set(transmisorMut, frecuencias.get(frecAsociada).get(frecuenciaMut));

        idMutado = seleccionado;

    }

    void algBX ( int individuo1, int individuo2 ) {
        System.out.print(" BLX : ");
        List<Integer> solucion1 = new ArrayList<>();
        List<Integer> solucion2 = new ArrayList<>();

        for ( int i = 0; i < transmisores.size(); i ++ ) {
            int d = Math.abs(hijos.get(individuo1).get(i) - hijos.get(individuo2).get(i));
            int cmin = Integer.MAX_VALUE;
            int cmax = Integer.MIN_VALUE;

            if ( hijos.get(individuo1).get(i) < hijos.get(individuo2).get(i) ) {
                cmin = hijos.get(individuo1).get(i);
            } else {
                cmin = hijos.get(individuo2).get(i);
            }

            if ( hijos.get(individuo1).get(i) > hijos.get(individuo2).get(i) ) {
                cmax = hijos.get(individuo1).get(i);
            } else {
                cmax = hijos.get(individuo2).get(i);
            }

            double vmin = cmin - d * alfa;
            double vmax = cmax + d * alfa;

            int frecAsociada = transmisores.get(i);

            //Para la solución 1
            int valorObtenido = (int) Math.floor(Math.random() * (vmax - vmin + 1) + vmin);
            int minimaDiferencia = Integer.MAX_VALUE;
            int frecuenciaFinal = 0;

            for ( int j = 0; j < frecuencias.get(frecAsociada).size(); j ++ ) {
                if ( Math.abs(valorObtenido - frecuencias.get(frecAsociada).get(j)) < minimaDiferencia ) {
                    frecuenciaFinal = frecuencias.get(frecAsociada).get(j);
                }
            }

            solucion1.add(i, frecuenciaFinal);

            //Para la solución 2
            int valorObtenido2 = (int) Math.floor(Math.random() * (vmax - vmin + 1) + vmin);
            int minimaDiferencia2 = Integer.MAX_VALUE;
            int frecuenciaFinal2 = 0;

            for ( int j = 0; j < frecuencias.get(frecAsociada).size(); j ++ ) {
                if ( Math.abs(valorObtenido2 - frecuencias.get(frecAsociada).get(j)) < minimaDiferencia2 ) {
                    frecuenciaFinal2 = frecuencias.get(frecAsociada).get(j);
                }
            }

            solucion2.add(i, frecuenciaFinal2);

        }

        hijos.set(individuo1, solucion1);
        hijos.set(individuo2, solucion2);
    }

    void algCruce2Puntos ( int individuo1, int individuo2 ) {
        Random numero = NUMERO;
        int seleccionado = numero.nextInt(transmisores.size());

        Random numero2 = NUMERO;
        int seleccionado2 = numero.nextInt(transmisores.size());

        if ( seleccionado2 < seleccionado ) {
            int temp = seleccionado;
            seleccionado = seleccionado2;
            seleccionado2 = temp;

        }

        List<Integer> solucion1 = new ArrayList<>();
        List<Integer> solucion2 = new ArrayList<>();

        //Primer cruce
        solucion1.addAll(0, hijos.get(individuo1).subList(0, seleccionado));
        solucion1.addAll(seleccionado, hijos.get(individuo2).subList(seleccionado, seleccionado2));
        solucion1.addAll(seleccionado2, hijos.get(individuo1).subList(seleccionado2, transmisores.size()));

        hijos.set(individuo1, solucion1);

        //Segundo cruce
        solucion2.addAll(0, hijos.get(individuo2).subList(0, seleccionado));
        solucion2.addAll(seleccionado, hijos.get(individuo1).subList(seleccionado, seleccionado2));
        solucion2.addAll(seleccionado2, hijos.get(individuo2).subList(seleccionado2, transmisores.size()));

        hijos.set(individuo2, solucion2);

    }

    public void nuevaGeneracion () throws FileNotFoundException {
        //Elitismo

        int aux = mejorResult;
        int resultadoHijos[] = new int[ 50 ];
        //Buscamos el mejor individuo de la generación de padres
        int minimo = Integer.MAX_VALUE;
        int actual = 0;
        for ( int i = 0; i < 50; i ++ ) {
            if ( resultado[ i ] < minimo ) {
                minimo = resultado[ i ];
                actual = i;
            }
        }

        List<Integer> mejorIndividuo = padres.get(actual);

        if ( idMutado <= 36 ) {
            resultadoHijos = evaluar(hijos, 36);
            numEvaluaciones += 36;
        } else {
            resultadoHijos = evaluar(hijos, idMutado);
            numEvaluaciones += 37;
        }

        for ( int i = 36; i < 50; i ++ ) {
            if ( i != idMutado ) {
                resultadoHijos[ i ] = resultado[ i ];
            }
        }

        //Evaluamos los hijos
//        resultadoHijos = evaluar(hijos);
//        resultadoHijos = evaluar(hijos);
        //Buscamos el hijo con el mayor coste
        int maximo = Integer.MIN_VALUE;
        int actual2 = 0;

        for ( int i = 0; i < 50; i ++ ) {
            if ( resultadoHijos[ i ] > maximo ) {
                maximo = resultadoHijos[ i ];
                actual2 = i;
            }
        }

        //Si el menor de los padres tiene menor coste que el mayor de los hijos se reemplaza
        if ( minimo < maximo ) {
            hijos.set(actual2, mejorIndividuo);
            resultadoHijos[ actual2 ] = minimo;

        }

        //Los hijos serán los padres para la siguiente generación
        padres.clear();
        padres.addAll(hijos);
        hijos.clear();

        for ( int i = 0; i < resultado.length; i ++ ) {
            resultado[ i ] = resultadoHijos[ i ]; //rDiferencia(padres.get(i), restricciones);
            if ( resultado[ i ] < mejorResult ) {
                mejorResult = resultado[ i ];
                idMejorResult = i;
            }
        }

        if ( aux == mejorResult ) {
            numGeneraciones ++;
        } else {
            numGeneraciones = 0;
        }

        if ( numGeneraciones >= 20 || comprobarConvergencia() ) {
            reinicializacion();
            System.out.print("Reinicializacion en curso");
            numGeneraciones = 0;
        }
    }

    public int[] evaluar ( List<List<Integer>> individuos, int mutado ) throws FileNotFoundException {
        int[] resultados = new int[ 50 ];

        for ( int i = 0; i < 36; i ++ ) {
            resultados[ i ] = rDiferencia(individuos.get(i), restricciones);
        }
        if ( mutado >= 36 ) {
            resultados[ mutado ] = rDiferencia(individuos.get(mutado), restricciones);
        }

        return resultados;
    }

    private void reinicializacion () throws FileNotFoundException {
        List<Integer> mejorSolucion = new ArrayList();
        mejorSolucion.addAll(padres.get(idMejorResult));
        padres.clear();
        hijos.clear();

        for ( int i = 0; i < 50; i ++ ) {
            padres.add(new ArrayList<>());
        }

        padres.set(0, mejorSolucion);
        resultado[ 0 ] = mejorResult;

        for ( int i = 1; i < 50; i ++ ) {
            greedyInicial(i);
        }

    }

    private boolean comprobarConvergencia () {

        int[] auxiliar;
        auxiliar = Arrays.copyOf(resultado, 50);
        Arrays.sort(auxiliar);

        int contador = 1;
        boolean convergencia = false;
        int maximo = Integer.MIN_VALUE;

        for ( int i = 1; i < auxiliar.length; i ++ ) {
            if ( contador >= 40 ) {
                convergencia = true;
                break;
            }
            if ( auxiliar[ i ] == (auxiliar[ i - 1 ]) ) {
                contador ++;
            } else {
                contador = 1;
            }
        }

        // Preguntar al profesor puesto que añade complejidad
        if ( convergencia ) {

            List<List<Integer>> auxiliarP = new ArrayList<>();

            for ( int i = 0; i < padres.size(); i ++ ) {
                auxiliarP.add(new ArrayList<>());
                auxiliarP.get(i).addAll(padres.get(i));
                Collections.sort(auxiliarP.get(i));
            }

            contador = 1;
            convergencia = false;
            for ( int i = 1; i < auxiliarP.size(); i ++ ) {
                if ( contador >= 40 ) {
                    convergencia = true;
                    System.out.print(" Convergencia dada : ");
                    break;
                }
                if ( auxiliarP.get(i).equals(auxiliarP.get(i - 1)) ) {
                    contador ++;
                } else {
                    contador = 1;
                }
            }
        }

        return convergencia;
    }

    public void resMejorIndividuo () throws FileNotFoundException {
        int minimo = Integer.MAX_VALUE;
        int actual = 0;
        for ( int i = 0; i < 50; i ++ ) {
            if ( resultado[ i ] < minimo ) {
                minimo = resultado[ i ];
                actual = i;
            }
        }
        List<Integer> mejorIndividuo = padres.get(actual);

        for ( int i = 0; i < mejorIndividuo.size(); i ++ ) {
            if ( mejorIndividuo.get(i) != 0 ) {
                System.out.println("Transmisor " + (i + 1) + ": " + mejorIndividuo.get(i));
            }
        }

        System.out.println(resultado[ actual ]);
    }
}
