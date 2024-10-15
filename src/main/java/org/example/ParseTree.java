package org.example;

/**
 * Clase abstracta que define la estructura básica para los árboles de análisis sintáctico.
 * Proporciona constantes para el dibujo y métodos abstractos que deben ser implementados
 * por las clases concretas de árboles de análisis.
 */
public abstract class ParseTree {
    // Constantes para el dibujo del árbol
    protected final static int HSEP = 30;         // Separación horizontal entre nodos
    protected final static int VSEP = 45;         // Separación vertical entre niveles
    protected final static int STRIP_HEIGHT = 30; // Altura de la franja inferior del SVG
    protected final static int TOP = 15;          // Margen superior para el dibujo de líneas
    protected final static int BOTTOM = 5;        // Margen inferior para el dibujo de líneas

    /**
     * Devuelve la altura del árbol.
     * @return Altura del árbol en unidades lógicas
     */
    public abstract int height();

    /**
     * Devuelve el ancho del árbol.
     * @return Ancho del árbol en unidades lógicas
     */
    public abstract int width();

    /**
     * Devuelve una representación corta del nombre del nodo.
     * @return Nombre corto o símbolo del nodo
     */
    public abstract String shortName();

    /**
     * Añade la frase generada por este subárbol a un StringBuffer.
     * Este método es utilizado para construir la frase completa generada por el árbol.
     * @param s StringBuffer al que se añade la frase
     */
    protected abstract void addSentence(StringBuffer s);

    /**
     * Dibuja el subárbol en un objeto SVG.
     * @param out Objeto SVG en el que se dibuja
     * @param x Coordenada x inicial para el dibujo
     * @param y Coordenada y inicial para el dibujo
     * @param levels Número de niveles restantes para dibujar
     * @return La coordenada x del centro del subárbol dibujado
     */
    protected abstract int draw(SVG out, int x, int y, int levels);
}