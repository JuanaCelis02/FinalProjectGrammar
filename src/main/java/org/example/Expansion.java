package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * La clase Expansion se encarga de generar y expandir árboles de derivación
 * para una gramática dada, hasta un límite especificado.
 */
class Expansion {
    private final Grammar grammar;  // La gramática a expandir
    private final int limit;        // Límite de expansión (posiblemente en términos de nodos totales)

    // Mapa de no terminales a sus árboles de derivación
    private Map<String, ArrayList<NonTerminalTree>> lgges;
    private int count;       // Contador de nodos totales generados
    private int expandCount; // Número de expansiones realizadas

    /**
     * Constructor de Expansion.
     * Inicializa la expansión con una gramática y un límite.
     * Crea un lenguaje vacío para cada no terminal.
     * 
     * @param grammar La gramática a expandir
     * @param limit   El límite de expansión
     */
    public Expansion(Grammar grammar, int limit) {
        this.grammar = grammar;
        this.limit = limit;

        count = 0;
        expandCount = 0;
        lgges = new HashMap<>();
        for (String nt : grammar.nonTerminals())
            lgges.put(nt, new ArrayList<NonTerminalTree>());
    }

    /**
     * Expande los árboles de derivación a la siguiente profundidad.
     * Dado un conjunto de árboles hasta la profundidad n, actualiza a árboles hasta la profundidad n+1.
     * 
     * @return false si se excede el límite de expansión, true en caso contrario
     */
    public final boolean expand() {
        Map<String, ArrayList<NonTerminalTree>> new_lgges = new HashMap<>();
        for (String nt : grammar.nonTerminals()) {
            ArrayList<NonTerminalTree> ts = new ArrayList<>();
            for (ArrayList<String> rhs : grammar.expansions(nt)) {
                ArrayList<ImmutableListNode<ParseTree>> strs = new ArrayList<>();
                strs.add(null);
                // Procesa el lado derecho de la producción de derecha a izquierda
                for (int i = rhs.size() - 1; i >= 0; i--) {
                    String sym = rhs.get(i);
                    ArrayList<NonTerminalTree> exps = lgges.get(sym);
                    if (exps == null) { // Es un terminal
                        ParseTree t = new TerminalTree(sym);
                        for (int j = 0; j < strs.size(); j++)
                            strs.set(j, new ImmutableListNode<>(t, strs.get(j)));
                    } else { // Es un no terminal
                        ArrayList<ImmutableListNode<ParseTree>> new_strs = new ArrayList<>();
                        for (ParseTree t : exps)
                            for (ImmutableListNode<ParseTree> str : strs)
                                new_strs.add(new ImmutableListNode<>(t, str));
                        strs = new_strs;
                    }
                }
                // Crea nuevos árboles no terminales y verifica el límite
                for (ImmutableListNode<ParseTree> str : strs) {
                    NonTerminalTree t = new NonTerminalTree(nt, ImmutableListNode.iterable(str));
                    ts.add(t);
                    count = count + t.height()*t.width();
                    if (count > limit)
                        return false;
                }
            }
            new_lgges.put(nt, ts);
        }
        lgges = new_lgges;
        expandCount++;
        return true;
    }

    /**
     * Obtiene las derivaciones para un no terminal específico.
     * 
     * @param nt El no terminal del cual se quieren obtener las derivaciones
     * @return Lista de árboles no terminales que representan las derivaciones
     */
    public ArrayList<NonTerminalTree> derivations(String nt) {
        return lgges.get(nt);
    }

    /**
     * Obtiene la profundidad actual de la expansión.
     * 
     * @return El número de expansiones realizadas
     */
    public final int depth() {
        return expandCount;
    }

    /**
     * Calcula el tamaño total de la expansión.
     * 
     * @return La suma de los tamaños de todas las derivaciones
     */
    public final int size() {
        int n = 0;
        for (String nt : grammar.nonTerminals())
            n = n + derivations(nt).size();
        return n;
    }
}