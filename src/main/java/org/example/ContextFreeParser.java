package org.example;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * ContextFreeParser: Analizador sintáctico para gramáticas libres de contexto.
 *  Características principales:
 * 
 * - Analiza de derecha a izquierda.
 * - Puede manejar gramáticas ambiguas y producciones vacías (epsilon).
 * - Utiliza un límite de expansión para prevenir bucles infinitos en gramáticas complejas.
 * - Genera árboles de análisis sintáctico para las entradas válidas.
 * 
 * El parser se escarga de:
 * 1. Determinar si una cadena de entrada pertenece al lenguaje definido por la gramática.
 * 2. Construir uno o más árboles de análisis sintáctico para entradas válidas.
 * 3. Manejar eficientemente una amplia gama de gramáticas libres de contexto.
 * 
 * 'grammar' es la gramática a utilizar, 'input' es la cadena de entrada a analizar,
 * y 'results' es donde se almacenarán los árboles de análisis resultantes.
 */

public class ContextFreeParser {

    // Símbolo de inicio para la gramática aumentada
    private static final String START = "Start";
    // Límite para evitar expansiones infinitas
    private static final int EXPANSION_LIMIT = 100;

    private final Grammar grammar;
// Almacena los estados del último análisis realizado
    private ArrayList<Set<ParserStateItem>> lastStates;

    public ContextFreeParser(Grammar grammar) {
        this.grammar = grammar;
        this.lastStates = null;
    }

    // Método principal de análisis
    public boolean parse(final ArrayList<String> input, ArrayList<NonTerminalTree> results) {
        ArrayList<Set<ParserStateItem>> states = new ArrayList<>();
        for (int i = 0; i <= input.size(); i++)
		states.add(null);

        boolean truncated = false;

        // Itera de derecha a izquierda en la entrada
        for (int pos = input.size(); pos >= 0; pos--) {
            Queue<ParserStateItem> queue = new ArrayDeque<>();
            if (pos == input.size()) {
                // Estado inicial (comenzando desde el final de la cadena)
                ArrayList<String> rhs0 = new ArrayList<>();
                rhs0.add(grammar.getStart());
                queue.add(new ParserStateItem(START, rhs0, pos));
            } else {
                // Escanea un símbolo termina
                String nextSym = input.get(pos);
                if (grammar.expansions(nextSym) == null) { // terminal
                    TerminalTree t = new TerminalTree(nextSym);
                    for (ParserStateItem item : states.get(pos+1))
                        if (item.match(nextSym))
                            queue.add(new ParserStateItem(item, t));
                }
            }

            Set<ParserStateItem> state = new HashSet<>();
            states.set(pos, state);
            Set<NonTerminalTree> empties = new HashSet<>();
            // Evita expansiones ilimitadas
            while (! queue.isEmpty()) {
                if (state.size() > EXPANSION_LIMIT) {
                    truncated = true;
                    break;
                }
                ParserStateItem item = queue.remove();
                if (! state.contains(item)) {
                    state.add(item);
                    // Expande el item
                    if (item.finished()) {
                        // completa una produccion
                        NonTerminalTree t = item.complete();
                        String nt = t.nonTerminal();
                        final int end = item.start();
                        if (end == pos)
                            // Las expansiones nulas necesitan tratamiento especial
                            empties.add(t);
                        for (ParserStateItem prev : states.get(end))
                            if (prev.match(nt))
                                queue.add(new ParserStateItem(prev, t));
                    } else {
                        // predice: expande un no terminal
                        String nt = item.current();
                        Collection<ArrayList<String>> rhss =
                            grammar.expansions(nt);
                        if (rhss != null) {
                            for (ArrayList<String> rhs : rhss)
                                queue.add(new ParserStateItem(nt, rhs, pos));
                            for (NonTerminalTree t : empties)
                                if (t.nonTerminal().equals(nt))
                                    queue.add(new ParserStateItem(item, t));
                        }
                    }
                }
            }
        }

        // Recopila los resultados
        results.clear();
        for (ParserStateItem item : states.get(0))
            if (item.finished(START))
                results.add(item.completeTop());
        lastStates = states;
        return ! truncated;
    }

    // Método para imprimir los estados del último análisis
    public void printStates(PrintWriter out) {
        if (lastStates != null)
            for (int i = 0; i < lastStates.size(); i++) {
                out.println("State " + i + ":");
                for (ParserStateItem item : lastStates.get(i))
                    out.println(item.toString());
                out.println();
            }
    }
}
