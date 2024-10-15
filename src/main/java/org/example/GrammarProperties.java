package org.example;

import java.util.*;

/**
 * GrammarProperties: Calcula y almacena propiedades estáticas de una gramática.
 * Esta clase analiza una gramática para determinar características como
 * símbolos no alcanzables, no realizables, anulables y cíclicos.
 */
public class GrammarProperties {
    private final Grammar grammar;
    private final Set<String> unreachable;  // Símbolos no alcanzables
    private final Set<String> unrealizable; // Símbolos no realizables
    private final Set<String> nullable;     // Símbolos anulables
    private final Set<String> cyclic;       // Símbolos cíclicos

    /**
     * Constructor: Calcula todas las propiedades de la gramática.
     * @param grammar La gramática a analizar
     */
    public GrammarProperties(Grammar grammar) {
        this.grammar = grammar;
        this.unreachable = computeUnreachable();
        this.unrealizable = computeUnrealizable();
        this.nullable = computeNullable();
        this.cyclic = computeCyclic();
    }

    // Métodos getter para cada propiedad calculada

    /** @return Conjunto de no terminales no alcanzables desde el símbolo inicial */
    public Set<String> getUnreachable() {
        return unreachable;
    }

    /** @return Conjunto de no terminales que no generan ninguna cadena */
    public Set<String> getUnrealizable() {
        return unrealizable;
    }

    /** @return Conjunto de no terminales que pueden generar la cadena vacía */
    public Set<String> getNullable() {
        return nullable;
    }

    /** @return Conjunto de no terminales que pueden derivarse a sí mismos */
    public Set<String> getCyclic() {
        return cyclic;
    }

    /**
     * Determina si la gramática es infinitamente ambigua.
     * Esto ocurre si y solo si un no terminal cíclico es alcanzable y realizable.
     * @return true si la gramática es infinitamente ambigua, false en caso contrario
     */
    public boolean infinitelyAmbiguous() {
        for (String nt : cyclic)
            if (!unreachable.contains(nt) && !unrealizable.contains(nt))
                return true;
        return false;
    }

    /**
     * Calcula los no terminales no alcanzables desde el símbolo inicial.
     * Utiliza un algoritmo de búsqueda en anchura (BFS).
     */
    private Set<String> computeUnreachable() {
        Set<String> reachable = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(grammar.getStart());
        while (!queue.isEmpty()) {
            String nt = queue.remove();
            if (!reachable.contains(nt)) {
                reachable.add(nt);
                for (ArrayList<String> rhs : grammar.expansions(nt))
                    for (String sym : rhs)
                        if (grammar.expansions(sym) != null)
                            queue.add(sym);
            }
        }
        return complement(reachable);
    }

    /**
     * Calcula los no terminales no realizables (que no generan ninguna cadena).
     * Utiliza un algoritmo iterativo que elimina no terminales realizables.
     */
    private Set<String> computeUnrealizable() {
        Set<String> unrealizable = new HashSet<>(grammar.nonTerminals());
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : new ArrayList<>(unrealizable)) {
                if (isRealizable(nt, unrealizable)) {
                    unrealizable.remove(nt);
                    changed = true;
                    break;
                }
            }
        }
        return unrealizable;
    }

    /**
     * Determina si un no terminal es realizable.
     */
    private boolean isRealizable(String nt, Set<String> unrealizable) {
        for (ArrayList<String> rhs : grammar.expansions(nt)) {
            boolean allRealizableInRhs = true;
            for (String sym : rhs) {
                if (unrealizable.contains(sym)) {
                    allRealizableInRhs = false;
                    break;
                }
            }
            if (allRealizableInRhs) return true;
        }
        return false;
    }

    /**
     * Calcula los no terminales anulables (que pueden generar la cadena vacía).
     * Utiliza un algoritmo iterativo que añade no terminales anulables.
     */
    private Set<String> computeNullable() {
        Set<String> nullable = new HashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : grammar.nonTerminals()) {
                if (!nullable.contains(nt) && isNullable(nt, nullable)) {
                    nullable.add(nt);
                    changed = true;
                    break;
                }
            }
        }
        return nullable;
    }

    /**
     * Determina si un no terminal es anulable.
     */
    private boolean isNullable(String nt, Set<String> nullable) {
        for (ArrayList<String> rhs : grammar.expansions(nt)) {
            boolean allNullableInRhs = true;
            for (String sym : rhs) {
                if (!nullable.contains(sym)) {
                    allNullableInRhs = false;
                    break;
                }
            }
            if (allNullableInRhs) return true;
        }
        return false;
    }

    /**
     * Calcula los no terminales cíclicos (que pueden derivarse a sí mismos).
     * Utiliza un algoritmo de expansión trivial y cierre transitivo.
     */
    private Set<String> computeCyclic() {
        Map<String, Set<String>> trivialExpansion = computeTrivialExpansions();
        computeTransitiveClosure(trivialExpansion);
        return findCyclicNonTerminals(trivialExpansion);
    }

    /**
     * Calcula las expansiones triviales para cada no terminal.
     */
    private Map<String, Set<String>> computeTrivialExpansions() {
        Map<String, Set<String>> trivialExpansion = new HashMap<>();
        for (String nt : grammar.nonTerminals()) {
            Set<String> s = new HashSet<>();
            for (ArrayList<String> rhs : grammar.expansions(nt)) {
                int nonNullCount = (int) rhs.stream().filter(sym -> !nullable.contains(sym)).count();
                if (nonNullCount == 0) {
                    s.addAll(rhs);
                } else if (nonNullCount == 1) {
                    rhs.stream()
                       .filter(sym -> grammar.expansions(sym) != null && !nullable.contains(sym))
                       .forEach(s::add);
                }
            }
            if (!s.isEmpty()) trivialExpansion.put(nt, s);
        }
        return trivialExpansion;
    }

    /**
     * Calcula el cierre transitivo de las expansiones triviales.
     */
    private void computeTransitiveClosure(Map<String, Set<String>> trivialExpansion) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, Set<String>> entry : trivialExpansion.entrySet()) {
                Set<String> exp = entry.getValue();
                int originalSize = exp.size();
                for (String target : new ArrayList<>(exp)) {
                    if (trivialExpansion.containsKey(target)) {
                        exp.addAll(trivialExpansion.get(target));
                    }
                }
                if (exp.size() > originalSize) {
                    changed = true;
                }
            }
        }
    }

    /**
     * Encuentra los no terminales cíclicos basados en las expansiones transitivas.
     */
    private Set<String> findCyclicNonTerminals(Map<String, Set<String>> trivialExpansion) {
        Set<String> cyclic = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : trivialExpansion.entrySet()) {
            if (entry.getValue().contains(entry.getKey())) {
                cyclic.add(entry.getKey());
            }
        }
        return cyclic;
    }

    /**
     * Calcula el complemento de un conjunto respecto a todos los no terminales de la gramática.
     */
    private Set<String> complement(Set<String> s) {
        Set<String> rest = new HashSet<>(grammar.nonTerminals());
        rest.removeAll(s);
        return rest;
    }
}