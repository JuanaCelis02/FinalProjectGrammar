package org.example; 

// Clase TerminalTree que extiende de ParseTree, representa un nodo terminal en un árbol de análisis
public class TerminalTree extends ParseTree {
    // Definición de colores para el símbolo y la línea
    private static final String SYMBOL_COLOUR = "#0000cc"; // Color del símbolo (azul)
    private static final String LINE_COLOUR = "#dddddd"; // Color de la línea (gris claro)

    private final String sym; // Almacena el símbolo terminal

    // Constructor de la clase, recibe un símbolo como parámetro
    public TerminalTree(String s) {
        sym = s; 
    }

    // Método que devuelve la altura del nodo, que es 1 para nodos terminales
    public int height() {
        return 1;
    }

    // Método que devuelve el ancho del nodo, que también es 1
    public int width() {
        return 1;
    }

    // Método que devuelve el nombre corto del símbolo
    public String shortName() {
        return sym;
    }

    // Método que verifica si este nodo es igual a otro objeto
    public boolean equals(Object obj) {
        if (obj instanceof TerminalTree) { // Verifica si el objeto es una instancia de TerminalTree
            TerminalTree o = (TerminalTree)obj; // Hace un cast del objeto a TerminalTree
            return sym.equals(o.sym); // Compara los símbolos
        }
        return false; // Si no es del mismo tipo, devuelve falso
    }

    // Método que genera un código hash para este nodo basado en su símbolo
    public int hashCode() {
        return sym.hashCode(); 
    }

    // Método protegido que dibuja el nodo en un objeto SVG
    protected final int draw(SVG out, int x, int y, int levels) {
        // Dibuja el símbolo en la posición actual del árbol
        out.text(x, y, SYMBOL_COLOUR, sym);
        
        // Calcula la posición vertical para dibujar el símbolo debajo
        int ly = y + levels * VSEP - 10; // VSEP es un valor de separación vertical, ajustado en 10 unidades
        out.text(x, ly, SYMBOL_COLOUR, sym); // Dibuja el símbolo en la nueva posición

        // Dibuja una línea gris que conecta ambos símbolos
        out.startLines(LINE_COLOUR); // Inicia el dibujo de líneas
        out.line(x, y + BOTTOM, x, ly - TOP); // Dibuja la línea de conexión
        out.endLines(); // Finaliza el dibujo de líneas
        
        return x; // Retorna la posición horizontal actual
    }

    // Método protegido que agrega el símbolo a un buffer de cadena
    protected final void addSentence(StringBuffer s) {
        if (s.length() > 0) // Si el buffer no está vacío, agrega un espacio
            s.append(' ');
        s.append(sym); // Agrega el símbolo al buffer
    }
}
