package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class GrammarGUI extends JFrame {
    private JTextArea grammarArea;
    private JTextField wordField;
    private JTextArea outputArea;
    private JButton loadButton, parseButton, copyButton;
    private Grammar grammar;

    public GrammarGUI() {
        setTitle("Grammar Parser");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Grammar input area
        grammarArea = new JTextArea(10, 40);
        JScrollPane grammarScroll = new JScrollPane(grammarArea);
        mainPanel.add(grammarScroll, BorderLayout.NORTH);

        // Word input and buttons
        JPanel inputPanel = new JPanel(new FlowLayout());
        wordField = new JTextField(20);
        loadButton = new JButton("Load Grammar");
        parseButton = new JButton("Parse");
        inputPanel.add(new JLabel("Word:"));
        inputPanel.add(wordField);
        inputPanel.add(loadButton);
        inputPanel.add(parseButton);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Output area
        outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        mainPanel.add(outputScroll, BorderLayout.SOUTH);

        // Copy button
        copyButton = new JButton("Copy Output");
        mainPanel.add(copyButton, BorderLayout.EAST);

        setContentPane(mainPanel);

        // Add action listeners
        loadButton.addActionListener(e -> loadGrammar());
        parseButton.addActionListener(e -> parseWord());
        copyButton.addActionListener(e -> copyOutput());
    }

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
                    "Grammar loaded successfully!\n\n" +
                    "Format reminder:\n" +
                    "Each line should be in the format:\n" +
                    "LHS RHS1|RHS2|...\n\n" +
                    "Example:\n" +
                    "S aA|b\n" +
                    "A aS|c");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }

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

    private void parseWord() {
        if (grammar == null) {
            JOptionPane.showMessageDialog(this, "Please load a grammar first!");
            return;
        }
        String word = wordField.getText();
        ArrayList<String> inputSymbols = Main.symList(word);
        
        Earley parser = new Earley(grammar);
        ArrayList<NonTerminalTree> trees = new ArrayList<>();
        boolean full = parser.parse(inputSymbols, trees);
        
        Collections.sort(trees, new NonTerminalTree.Ascending());
        
        String htmlOutput = Main.generateHTMLOutput(grammar, trees, word, full);
        outputArea.setText(htmlOutput);
    }

    private void copyOutput() {
        outputArea.selectAll();
        outputArea.copy();
        JOptionPane.showMessageDialog(this, "Output copied to clipboard!");
    }
}