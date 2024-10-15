package org.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Set;

public class Main {
    private static final int LIMIT = 10000;
    private final static char EMPTY = '\u03b5';

    public static void main(String[] args) {
        // Iniciar la GUI
        javax.swing.SwingUtilities.invokeLater(() -> new GrammarGUI().setVisible(true));
    }

    public static String generateHTMLOutput(Grammar g, ArrayList<NonTerminalTree> trees, String sentence, boolean full) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);

        GrammarProperties properties = new GrammarProperties(g);

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"https://staff.city.ac.uk/~ross/IN1002/css/algorithms.css\"/>");
        out.println("<title>Derivation trees</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<!-- Java version " + System.getProperty("java.version") + " -->");
        out.println("<h1>Derivation trees</h1>");

        out.println("<h2>Grammar</h2>");
        showGrammar(out, g);

        if (!properties.getUnreachable().isEmpty() ||
            !properties.getUnrealizable().isEmpty() ||
            !properties.getCyclic().isEmpty()) {
            out.println("<p>This grammar has the following problems:");
            out.println("<ul>");
            report(out, properties.getUnreachable(), "unreachable from the start symbol " + g.getStart());
            report(out, properties.getUnrealizable(), "unrealizable (cannot generate any strings)");
            report(out, properties.getCyclic(),
                properties.infinitelyAmbiguous() ?
                    "cyclic, so some strings have infinitely many derivations" :
                    "cyclic");
            out.println("</ul>");
        }

        String treeHeading;
        if (!full)
            treeHeading = "Some of the derivations";
        else if (trees.isEmpty())
            treeHeading = "There are no derivations";
        else if (trees.size() == 1)
            treeHeading = "Derivation tree";
        else
            treeHeading = "Derivation trees";
        treeHeading = treeHeading + " for '" + sentence + "'";

        out.println("<h2>" + treeHeading + "</h2>");
        for (NonTerminalTree t : trees)
            t.drawSVG(out);

        out.println("</body>");
        out.println("</html>");

        return stringWriter.toString();
    }

    static ArrayList<String> symList(String s) {
        ArrayList<String> exp = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (! Character.isWhitespace(c) && c != EMPTY)
                exp.add(String.valueOf(c));
        }
        return exp;
    }

    private static void showGrammar(PrintWriter out, Grammar g) {
        out.println("<ul class=\"plain\">");
        for (String lhs : g.nonTerminals()) {
            out.print("<li>");
            out.print(lhs);
            out.print(" &#x2192; ");
            boolean firstAlt = true;
            for (ArrayList<String> alt : g.expansions(lhs)) {
                if (firstAlt)
                    firstAlt = false;
                else
                    out.print(" | ");
                if (alt.size() == 0)
                    out.print("&#x03B5;");
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

    private static void report(PrintWriter out, Set<String> s, String label) {
        if (! s.isEmpty()) {
            out.print("<li>");
            out.print(s.size() == 1 ? "Nonterminal " : "Nonterminals ");
            boolean comma = false;
            for (String nt : s) {
                if (comma)
                    out.print(", ");
                out.print(nt);
                comma = true;
            }
            out.print(s.size() == 1 ? " is " : " are ");
            out.print(label);
            out.println(".");
        }
    }
}