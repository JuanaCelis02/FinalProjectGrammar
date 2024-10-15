package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * GrammarGUI: Interfaz gráfica de usuario para cargar gramáticas y analizar palabras.
 * proporciona una interfaz para interactuar con el analizador de gramáticas.
 */
public class GrammarGUI extends JFrame {
    private JTextArea grammarArea;    // Área para mostrar y editar la gramática
    private JTextField wordField;     // Campo para ingresar la palabra a analizar
    private JTextArea outputArea;     // Área para mostrar los resultados del análisis
    private JButton loadButton, parseButton, copyButton;  // Botones de acción
    private Grammar grammar;          // Objeto Grammar para almacenar la gramática cargada

    /**
     * Constructor: Inicializa y configura la interfaz gráfica.
     */
    public GrammarGUI() {
        setTitle("Analizador gramatical");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Configuración del área de entrada de la gramática
        grammarArea = new JTextArea(10, 40);
        JScrollPane grammarScroll = new JScrollPane(grammarArea);
        mainPanel.add(grammarScroll, BorderLayout.NORTH);

        // Configuración del panel de entrada de palabra y botones
        JPanel inputPanel = new JPanel(new FlowLayout());
        wordField = new JTextField(20);
        loadButton = new JButton("Cargar gramatica");
        parseButton = new JButton("Analice");
        inputPanel.add(new JLabel("Palabra:"));
        inputPanel.add(wordField);
        inputPanel.add(loadButton);
        inputPanel.add(parseButton);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Configuración del área de salida
        outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        mainPanel.add(outputScroll, BorderLayout.SOUTH);

        // Configuración del botón de copiar
        copyButton = new JButton("Copiar salida");
        mainPanel.add(copyButton, BorderLayout.EAST);

        setContentPane(mainPanel);

        // Configuración de los listeners de eventos
        loadButton.addActionListener(e -> loadGrammar());
        parseButton.addActionListener(e -> parseWord());
        copyButton.addActionListener(e -> copyOutput());
    }

    /**
     * Carga una gramática desde un archivo seleccionado por el usuario.
     */
    private void loadGrammar() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String content = new String(java.nio.file.Files.readAllBytes(selectedFile.toPath()));
                grammarArea.setText(content);
                grammar = parseGrammar(content);
                JOptionPane.showMessageDialog(this, 
                    "Gramática cargada correctamente!\n\n" +
                    "Recordatorio de formato:\n" +
                    "Cada línea debe tener el formato\n" +
                    "LHS RHS1|RHS2|...\n\n" +
                    "Ejemplo:\n" +
                    "S aA|b\n" +
                    "A aS|c");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage());
            }
        }
    }

    /**
     * Parsea el contenido de la gramática y crea un objeto Grammar.
     * @param content El contenido de la gramática como una cadena
     * @return Un objeto Grammar representando la gramática parseada
     */
    private Grammar parseGrammar(String content) {
        Grammar g = new Grammar();
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] parts = line.trim().split("\\s+", 2);
            if (parts.length == 2) {
                String lhs = parts[0];
                String rhs = parts[1];
                for (String alternative : rhs.split("[|]", -1)) {
                    g.addProduction(lhs, Main.symList(alternative));
                }
            }
        }
        return g;
    }

    /**
     * Parsea la palabra ingresada utilizando la gramática cargada.
     */
    private void parseWord() {
        if (grammar == null) {
            JOptionPane.showMessageDialog(this, "Please load a grammar first!");
            return;
        }
        String word = wordField.getText();
        ArrayList<String> inputSymbols = Main.symList(word);
        
        ContextFreeParser parser = new ContextFreeParser(grammar);
        ArrayList<NonTerminalTree> trees = new ArrayList<>();
        boolean full = parser.parse(inputSymbols, trees);
        
        Collections.sort(trees, new NonTerminalTree.Ascending());
        // Generación del HTML
        String htmlOutput = Main.generateHTMLOutput(grammar, trees, word, full);
        // Mostrar el HTML en el área de salida
        outputArea.setText(htmlOutput);
    }

    /**
     * Copia el contenido del área de salida al portapapeles.
     */
    private void copyOutput() {
        outputArea.selectAll();
        outputArea.copy();
        JOptionPane.showMessageDialog(this, "Salida copiada en el portapapeles!");
    }
}