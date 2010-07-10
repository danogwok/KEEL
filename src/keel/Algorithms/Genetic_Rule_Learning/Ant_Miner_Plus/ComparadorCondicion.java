package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner_Plus;

import java.util.Comparator;

/**
 * <p>T�tulo: Ant Colony Optimization</p>
 * <p>Descripci�n:Comparador entre atributos</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * @author Vicente Rub�n del Pino Ruiz
 * @version 1.0
 */

public class ComparadorCondicion implements Comparator {

    public ComparadorCondicion() {
    }

    /**
     * Compara dos atributos
     *
     * @param o1 Object Atributo a comparar
     * @param o2 Object Atributo a comparar
     * @return int Devuelve 0 si tienen la misma posicion, 1 si el primero esta
     * antes, -1 si el primero esta despues.
     *
     * OJO!!!! Como el Collections.sort ordena de mayor a menor y el orden que interesa
     * que tengan los atributos es de menor a mayor, este CompareTo esta trucado al reves
     * es decir cuando es menor devuelve mayor y cuando es mayor devuelve menor.
     *
     */
    public int compare(Object o1, Object o2) {
        Condicion c1 = (Condicion) o1;
        Condicion c2 = (Condicion) o2;

        Atributo original = c1.getValor();
        Atributo actual = c2.getValor();
        int atributo1, atributo2;
        String valor1, valor2;
        int devolver = 0;

        atributo1 = original.getAtributo();
        atributo2 = actual.getAtributo();
        valor1 = original.getValor();
        valor2 = actual.getValor();

        if (atributo1 == atributo2 && valor1.equals(valor2) &&
            c1.getOperador() == c2.getOperador()) { //Para ver si son iguales tiene que coincidir tambien el valor
            devolver = 0;
        } else {
            if (atributo1 < atributo2) {
                devolver = -1;
            } else {
                if (atributo1 > atributo2) {
                    devolver = 1;
                }
            }
        }
        return devolver;

    }

}