package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa una gramática libre de contexto.
 * Esta clase permite definir y manipular una gramática mediante la adición de producciones
 * y la consulta de sus componentes.
 */
public class Grammar {
    // Lista de símbolos no terminales en el orden en que fueron definidos
    private ArrayList<String> lhss;
    // Mapa de símbolos no terminales a sus producciones
    private Map<String, Collection<ArrayList<String>>> productions;

    /**
     * Constructor que inicializa una gramática vacía.
     */
    public Grammar() {
        lhss = new ArrayList<>();
        productions = new HashMap<>();
    }

    /**
     * Añade una producción a la gramática.
     * Si es la primera producción para un no terminal, también lo añade a la lista de no terminales.
     * 
     * @param lhs El símbolo no terminal del lado izquierdo de la producción
     * @param rhs La lista de símbolos del lado derecho de la producción
     */
    public void addProduction(String lhs, ArrayList<String> rhs) {
        Collection<ArrayList<String>> prods = productions.get(lhs);
        if (prods == null) {
            prods = new ArrayList<>();
            lhss.add(lhs);  // Añade el nuevo no terminal a la lista
        }
        prods.add(rhs);
        productions.put(lhs, prods);
    }

    /**
     * Obtiene el símbolo inicial de la gramática.
     * Se asume que es el no terminal del lado izquierdo de la primera producción añadida.
     * 
     * @return El símbolo inicial de la gramática
     */
    public String getStart() {
        return lhss.get(0);
    }

    /**
     * Obtiene la colección de todos los símbolos no terminales de la gramática.
     * Los no terminales se devuelven en el orden en que fueron definidos.
     * 
     * @return Colección de símbolos no terminales
     */
    public Collection<String> nonTerminals() {
        return lhss;
    }

    /**
     * Obtiene todas las expansiones (producciones) para un símbolo no terminal dado.
     * 
     * @param nt El símbolo no terminal del cual se quieren obtener las expansiones
     * @return Colección de expansiones para el no terminal, o null si el no terminal no existe
     */
    public Collection<ArrayList<String>> expansions(String nt) {
        return productions.get(nt);
    }
}
