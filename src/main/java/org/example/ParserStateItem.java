package org.example;

import java.util.ArrayList;

/**
 * ParserStateItem representa un estado en el algoritmo de análisis sintáctico de Earley.
 * Este algoritmo escanea la entrada de derecha a izquierda en esta implementación.
 */
public class ParserStateItem {
    private final String nt;                               // Símbolo no terminal actual
    private final ImmutableListNode<ParseTree> parsed;     // Árboles de análisis ya construidos
    private final ArrayList<String> rhs;                   // Lado derecho de la producción
    private final int pos;                                 // Posición actual en el lado derecho
    private final int finish;                              // Posición final en la cadena de entrada
    private final int cachedHash;                          // Valor hash pre-calculado para eficiencia

    /**
     * Constructor para un ítem al final de una producción.
     * @param nt Símbolo no terminal
     * @param rhs Lado derecho de la producción
     * @param finish Posición final en la cadena de entrada
     */
    public ParserStateItem(String nt, ArrayList<String> rhs, int finish) {
        this.nt = nt;
        this.parsed = null;
        this.rhs = rhs;
        this.pos = rhs.size();  // Inicializa en el final del lado derecho
        this.finish = finish;
        this.cachedHash = realHashCode();
    }

    /**
     * Constructor para avanzar un ítem existente.
     * @param prev Ítem anterior
     * @param t Árbol de análisis para el símbolo reconocido
     */
    public ParserStateItem(ParserStateItem prev, ParseTree t) {
        if (prev.finished())
            throw new IllegalArgumentException("advancing at end");
        nt = prev.nt;
        parsed = new ImmutableListNode<ParseTree>(t, prev.parsed);
        rhs = prev.rhs;
        pos = prev.pos - 1;  // Retrocede una posición (análisis de derecha a izquierda)
        finish = prev.finish;
        this.cachedHash = realHashCode();
    }

    /**
     * Compara este ítem con otro objeto para igualdad.
     */
    public boolean equals(Object obj) {
        ParserStateItem o = (ParserStateItem)obj;
        return o != null &&
            finish == o.finish && pos == o.pos && nt.equals(o.nt) &&
            rhs.equals(o.rhs) &&
            (parsed == null ? o.parsed == null : parsed.equals(o.parsed));
    }

    /**
     * Retorna el hash code pre-calculado del ítem.
     */
    public int hashCode() {
        return cachedHash;
    }

    /**
     * Calcula el hash code real del ítem.
     */
    private int realHashCode() {
        return 13*finish + 19*pos + 23*nt.hashCode() +
            29*rhs.hashCode() + (parsed == null ? 1 : 37*parsed.hashCode());
    }

    /**
     * Genera una representación en cadena del ítem.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append('(').append(nt).append(" -> ");
        for (int i = 0; i < pos; i++)
            s.append(rhs.get(i));
        s.append('.');
        for (ParseTree t : ImmutableListNode.iterable(parsed))
            s.append(t.shortName());
        s.append(", ");
        s.append(finish);
        s.append(')');
        return s.toString();
    }

    /**
     * Verifica si el ítem ha terminado de procesar su producción.
     */
    public boolean finished() {
        return pos == 0;
    }

    /**
     * Verifica si el ítem ha terminado y corresponde a un símbolo no terminal específico.
     */
    public boolean finished(String nt) {
        return pos == 0 && this.nt.equals(nt);
    }

    /**
     * Verifica si el siguiente símbolo a procesar coincide con el dado.
     */
    public boolean match(String sym) {
        return pos > 0 && rhs.get(pos-1).equals(sym);
    }

    /**
     * Retorna el símbolo actual a procesar.
     */
    public String current() {
        if (finished())
            throw new IllegalStateException("current at end");
        return rhs.get(pos-1);
    }

    /**
     * Completa el árbol de análisis para este ítem.
     */
    public NonTerminalTree complete() {
        if (! finished())
            throw new IllegalStateException("not complete");
        return new NonTerminalTree(nt, ImmutableListNode.iterable(parsed));
    }

    /**
     * Retorna la posición de inicio en la cadena de entrada para este ítem.
     */
    public int start() {
        return finish;
    }

    /**
     * Completa el árbol de análisis superior para este ítem.
     */
    public NonTerminalTree completeTop() {
        if (! finished())
            throw new IllegalStateException("current at end");
        if (parsed == null)
            throw new IllegalStateException("null list");
        if (parsed.tail != null)
            throw new IllegalStateException("non-singleton list");
        return (NonTerminalTree)parsed.head;
    }
}