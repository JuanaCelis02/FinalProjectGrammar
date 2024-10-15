package org.example;

import java.io.PrintWriter;
import java.util.Comparator;

/**
 * Representa un nodo no terminal en un árbol de análisis sintáctico.
 * Esta clase extiende ParseTree y proporciona funcionalidad para
 * dibujar el árbol en formato SVG.
 */
public class NonTerminalTree extends ParseTree {
    // Colores para el dibujo SVG
    private static final String SYMBOL_COLOUR = "#cc0000";
    private static final String LINE_COLOUR = "black";
    private static final String NULL_COLOUR = "#aaaaaa";
    private static final String NULL_SYMBOL = "&#x03B5;"; // Símbolo épsilon

    private final String sym;                 // Símbolo no terminal
    private final Iterable<ParseTree> children; // Hijos del nodo

    // Valores derivados
    private final int ht;       // Altura del subárbol
    private final int wd;       // Ancho del subárbol
    private final String sentence; // Frase generada por este subárbol

    /**
     * Constructor del árbol no terminal.
     * @param sym Símbolo no terminal
     * @param children Hijos del nodo
     */
    public NonTerminalTree(String sym, Iterable<ParseTree> children) {
        this.sym = sym;
        this.children = children;

        // Calcula la altura y el ancho del subárbol
        int h = 1;
        int w = 0;
        for (ParseTree t : children) {
            h = Math.max(h, t.height());
            w = w + t.width();
        }
        ht = h + 1;
        wd = Math.max(1, w);

        // Genera la frase representada por este subárbol
        StringBuffer buff = new StringBuffer();
        addSentence(buff);
        sentence = buff.toString();
    }

    // Métodos para obtener las dimensiones del árbol
    public int height() { return ht; }
    public int width() { return wd; }

    public String shortName() { return sym; }
    public String nonTerminal() { return sym; }

    /**
     * Agrega la frase generada por este subárbol a un StringBuffer.
     */
    protected final void addSentence(StringBuffer s) {
        for (ParseTree t : children)
            t.addSentence(s);
    }

    /**
     * Compara este árbol con otro objeto.
     */
    public boolean equals(Object obj) {
        if (obj instanceof NonTerminalTree) {
            NonTerminalTree o = (NonTerminalTree)obj;
            return sym.equals(o.sym) && children.equals(o.children);
        }
        return false;
    }

    /**
     * Calcula el hash code del árbol.
     */
    public int hashCode() {
        return sym.hashCode()*7 + children.hashCode();
    }

    /**
     * Dibuja el subárbol en formato SVG.
     * @param out Objeto SVG para dibujar
     * @param x Coordenada x inicial
     * @param y Coordenada y inicial
     * @param levels Niveles restantes para dibujar
     * @return Coordenada x del centro del subárbol dibujado
     */
    protected final int draw(SVG out, int x, int y, int levels) {
        int rx;
        final int ty = y + VSEP;
        int trx[] = new int[100];
        int n = 0;
        int tx = x;
        
        // Dibuja los hijos recursivamente
        for (ParseTree t : children) {
            trx[n] = t.draw(out, tx, ty, levels-1);
            n++;
            tx = tx + t.width()*HSEP;
        }
        
        // Calcula la posición x del nodo actual
        if (n == 0) {
            rx = x;
        } else {
            // rx es la mediana de las coordenadas x de las raíces de los subárboles
            rx = (trx[(n-1)/2] + trx[n/2])/2;
        }
        
        // Dibuja el símbolo no terminal
        out.text(rx, y, SYMBOL_COLOUR, sym);
        
        if (n == 0) {
            // Caso de nodo hoja (épsilon)
            out.text(x, ty, NULL_COLOUR, NULL_SYMBOL);
            out.startLines(NULL_COLOUR);
            out.line(x, y+BOTTOM, x, ty-TOP);
            out.endLines();
        } else {
            // Dibuja líneas a los hijos
            out.startLines(LINE_COLOUR);
            tx = x;
            int i = 0;
            for (ParseTree t : children) {
                out.line(rx, y+BOTTOM, trx[i], ty-TOP);
                i++;
                tx = tx + t.width()*HSEP;
            }
            out.endLines();
        }
        return rx;
    }

    /**
     * Dibuja el árbol completo en formato SVG.
     */

    public void drawSVG(PrintWriter out) {
        SVG svg = new SVG(out);
        svg.startTag("svg");
        svg.attribute("width", width()*HSEP);
        svg.attribute("height", height()*VSEP + STRIP_HEIGHT);
        svg.attribute("xmlns", "http://www.w3.org/2000/svg");
        svg.attribute("version", "1.1");
        svg.attribute("font-family", "sans-serif");
        svg.attribute("font-size", 15);
        svg.closeBracket();
        svg.startTag("rect");
        svg.attribute("width", width()*HSEP);
        svg.attribute("height", height()*VSEP);
        svg.attribute("fill", "#fff7db");
        svg.closeEmpty();

        // Dibuja rectángulos de fondo
        svg.startTag("rect");
        svg.attribute("y", height()*VSEP);
        svg.attribute("width", width()*HSEP);
        svg.attribute("height", STRIP_HEIGHT);
        svg.attribute("fill", "#f0e6bc");
        svg.closeEmpty();
        int rootX = draw(svg, HSEP/2, 30, height());
        svg.endTag("svg");
        out.println();
    }

    /**
     * Clase interna para comparar árboles no terminales.
     */
    public static class Ascending implements Comparator<NonTerminalTree> {
        /**
         * Compara árboles primero por longitud de la frase generada,
         * luego por la frase en sí, y finalmente por altura.
         */
        public final int compare(NonTerminalTree a, NonTerminalTree b) {
            if (a.sentence.length() != b.sentence.length())
                return a.sentence.length() - b.sentence.length();
            if (!a.sentence.equals(b.sentence))
                return a.sentence.compareTo(b.sentence);
            return a.height() - b.height();
        }
    }
}