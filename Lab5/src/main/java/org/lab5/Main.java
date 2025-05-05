package org.lab5;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Lab 5: Chomsky Normal Form Conversion");
        System.out.println("=====================================\n");

        // Create the grammar from Variant 5
        Grammar grammarVariant5 = createVariant5Grammar();

        // Convert to CNF
        grammarVariant5.convertToCNF();

        // Bonus: Test with a different grammar
        System.out.println("\n\nBONUS: Testing with a different grammar");
        System.out.println("=====================================\n");

        Grammar customGrammar = createCustomGrammar();
        customGrammar.convertToCNF();
    }

    /**
     * Create the grammar from Variant 5
     */
    private static Grammar createVariant5Grammar() {
        // Define non-terminals
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S", "A", "B", "C", "D"));

        // Define terminals
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "d"));

        // Define productions
        Map<String, List<String>> productions = new HashMap<>();
        productions.put("S", new ArrayList<>(Arrays.asList("dB", "A")));
        productions.put("A", new ArrayList<>(Arrays.asList("d", "dS", "aBdB")));
        productions.put("B", new ArrayList<>(Arrays.asList("a", "aS", "AC")));
        productions.put("C", new ArrayList<>(Arrays.asList("bC", "")));  // Empty string represents epsilon
        productions.put("D", new ArrayList<>(Arrays.asList("AB")));

        // Define start symbol
        String startSymbol = "S";

        return new Grammar(nonTerminals, terminals, productions, startSymbol);
    }

    /**
     * Create a custom grammar for bonus testing
     */
    private static Grammar createCustomGrammar() {
        // Define non-terminals
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S", "A", "B", "C"));

        // Define terminals
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "c"));

        // Define productions
        Map<String, List<String>> productions = new HashMap<>();
        productions.put("S", new ArrayList<>(Arrays.asList("AB", "aSc", "")));  // S -> AB | aSc | ε
        productions.put("A", new ArrayList<>(Arrays.asList("aA", "B", "a")));  // A -> aA | B | a
        productions.put("B", new ArrayList<>(Arrays.asList("bB", "b", "C")));  // B -> bB | b | C
        productions.put("C", new ArrayList<>(Arrays.asList("cC", "aCb", "")));  // C -> cC | aCb | ε

        // Define start symbol
        String startSymbol = "S";

        return new Grammar(nonTerminals, terminals, productions, startSymbol);
    }
}