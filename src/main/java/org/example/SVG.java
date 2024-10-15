package org.example;

import java.io.PrintWriter; 

// Clase SVG que se encarga de generar contenido en formato SVG
public class SVG {
    private final PrintWriter out; // Almacena el objeto PrintWriter para la salida

    // Constructor que recibe un PrintWriter y lo asigna al atributo out
    public SVG(PrintWriter out) {
        this.out = out;
    }

    // Método para iniciar una etiqueta SVG
    public void startTag(String tag) {
        out.print("<" + tag); 
    }

    // Método sobrecargado para agregar un atributo a la etiqueta con valor entero
    public void attribute(String name, int value) {
        out.print(" " + name + "=\"" + value + "\""); 
    }

    // Método sobrecargado para agregar un atributo a la etiqueta con valor string
    public void attribute(String name, String value) {
        out.print(" " + name + "=\"" + value + "\"");
    }

    // Método para cerrar la etiqueta con un corchete de cierre
    public void closeBracket() {
        out.print(">"); 
    }

    // Método para cerrar una etiqueta vacía (como un <tag/>)
    public void closeEmpty() {
        out.print("/>"); 
    }

    // Método para cerrar una etiqueta existente
    public void endTag(String tag) {
        out.print("</" + tag + ">"); 
    }

    // Método para iniciar un grupo de líneas con un color específico
    public void startLines(String colour) {
        startTag("g"); // Inicia un nuevo grupo SVG
        attribute("stroke", colour); // Establece el color de la línea
        attribute("stroke-width", 1); // Establece el ancho de la línea
        attribute("stroke-linecap", "round"); // Establece el estilo de los extremos de la línea
        closeBracket(); // Cierra la etiqueta del grupo
    }

    // Método para finalizar el grupo de líneas
    public void endLines() {
        endTag("g"); 
    }

    // Método para dibujar una línea en el SVG
    public void line(int x1, int y1, int x2, int y2) {
        startTag("line"); // Inicia la etiqueta de línea
        attribute("x1", x1); // Establece la coordenada x del primer punto
        attribute("y1", y1); // Establece la coordenada y del primer punto
        attribute("x2", x2); // Establece la coordenada x del segundo punto
        attribute("y2", y2); // Establece la coordenada y del segundo punto
        closeEmpty(); // Cierra la etiqueta de línea
    }

    // Método para dibujar texto en el SVG
    public void text(int x, int y, String colour, String s) {
        startTag("text"); // Inicia la etiqueta de texto
        attribute("x", x); // Establece la posición x del texto
        attribute("y", y); // Establece la posición y del texto
        attribute("text-anchor", "middle"); // Centra el texto en la posición especificada
        attribute("fill", colour); // Establece el color del texto
        closeBracket(); // Cierra la etiqueta de texto
        out.print(s); // Imprime el contenido del texto
        endTag("text"); // Cierra la etiqueta de texto
    }
}
