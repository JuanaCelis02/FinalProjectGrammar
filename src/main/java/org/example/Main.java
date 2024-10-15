package org.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Set;

/**
 * Clase principal que inicia la aplicación y contiene métodos utilitarios
 * para generar salida HTML y procesar la gramática.
 */
public class Main {
    private final static char EMPTY = '\u03b5';  // Carácter épsilon (ε) para representar producciones vacías

    /**
     * Método principal que inicia la interfaz gráfica de usuario.
     */
    public static void main(String[] args) {
        // Iniciar la GUI en el hilo de eventos de Swing
        javax.swing.SwingUtilities.invokeLater(() -> new GrammarGUI().setVisible(true));
    }

    /**
     * Genera una salida HTML con los árboles de derivación y propiedades de la gramática.
     * 
     * @param g La gramática analizada
     * @param trees Lista de árboles de derivación
     * @param sentence La frase analizada
     * @param full Indica si el análisis fue completo
     * @return String con el contenido HTML generado
     */
    public static String generateHTMLOutput(Grammar g, ArrayList<NonTerminalTree> trees, String sentence, boolean full) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);

        GrammarProperties properties = new GrammarProperties(g);

        // Generar estructura básica HTML
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"https://staff.city.ac.uk/~ross/IN1002/css/algorithms.css\"/>");
        out.println("<title>Arboles de derivacion</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<!-- Java version " + System.getProperty("java.version") + " -->");
        out.println("<h1>Arboles de derivacio</h1>");

        // Mostrar la gramática
        out.println("<h2>Gramatica</h2>");
        showGrammar(out, g);

        // Reportar problemas en la gramática si los hay
        if (!properties.getUnreachable().isEmpty() ||
            !properties.getUnrealizable().isEmpty() ||
            !properties.getCyclic().isEmpty()) {
            out.println("<p>Esta gramática presenta los siguientes problemas:");
            out.println("<ul>");
            report(out, properties.getUnreachable(), "Inalcanzable desde el simbolo de inicio" + g.getStart());
            report(out, properties.getUnrealizable(), "inalcanzable, no puede generar ninguna cadena");
            report(out, properties.getCyclic(),
                properties.infinitelyAmbiguous() ?
                    "ciclica, por lo que algunas cadenas tienen infinitas derivaciones" :
                    "ciclica");
            out.println("</ul>");
        }

        // Determinar el encabezado para los árboles de derivación
        String treeHeading;
        if (!full)
            treeHeading = "Algunas derivaciones";
        else if (trees.isEmpty())
            treeHeading = "No hay derivaciones";
        else if (trees.size() == 1)
            treeHeading = "Arbol de derivacion";
        else
            treeHeading = "Arboles de derivacion";
        treeHeading = treeHeading + " para '" + sentence + "'";

        // Mostrar los árboles de derivación
        out.println("<h2>" + treeHeading + "</h2>");
        for (NonTerminalTree t : trees)
            t.drawSVG(out);

        out.println("</body>");
        out.println("</html>");

        return stringWriter.toString();
    }

    /**
     * Convierte una cadena en una lista de símbolos, ignorando espacios en blanco y el carácter épsilon.
     * 
     * @param s La cadena a convertir
     * @return Lista de símbolos
     */
    static ArrayList<String> symList(String s) {
        ArrayList<String> exp = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c) && c != EMPTY)
                exp.add(String.valueOf(c));
        }
        return exp;
    }

    /**
     * Genera una representación HTML de la gramática.
     * 
     * @param out PrintWriter para la salida HTML
     * @param g La gramática a mostrar
     */
    private static void showGrammar(PrintWriter out, Grammar g) {
        out.println("<ul class=\"plain\">");
        for (String lhs : g.nonTerminals()) {
            out.print("<li>");
            out.print(lhs);
            out.print(" &#x2192; ");  // Flecha Unicode
            boolean firstAlt = true;
            for (ArrayList<String> alt : g.expansions(lhs)) {
                if (firstAlt)
                    firstAlt = false;
                else
                    out.print(" | ");
                if (alt.size() == 0)
                    out.print("&#x03B5;");  // Épsilon Unicode
                else {
                    boolean firstSym = true;
                    for (String s : alt) {
                        if (firstSym)
                            firstSym = false;
                        else
                            out.print(" ");
                        out.print(s);
                    }
                }
            }
            out.println();
        }
        out.println("</ul>");
    }

    /**
     * Genera un reporte HTML para un conjunto de símbolos no terminales con una etiqueta específica.
     * 
     * @param out PrintWriter para la salida HTML
     * @param s Conjunto de símbolos no terminales
     * @param label Etiqueta descriptiva para el reporte
     */
    private static void report(PrintWriter out, Set<String> s, String label) {
        if (!s.isEmpty()) {
            out.print("<li>");
            out.print(s.size() == 1 ? "No terminal " : "No terminales ");
            boolean comma = false;
            for (String nt : s) {
                if (comma)
                    out.print(", ");
                out.print(nt);
                comma = true;
            }
            out.print(s.size() == 1 ? " es " : " son ");
            out.print(label);
            out.println(".");
        }
    }
}