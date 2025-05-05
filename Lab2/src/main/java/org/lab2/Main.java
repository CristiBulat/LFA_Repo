package org.lab2;

import java.util.*;

/**
 * Main class to demonstrate the implementation for Variant 6
 */
public class Main {
    public static void main(String[] args) {
        // Defining the finite automaton from Variant 6
        Set<String> states = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3", "q4"));
        Set<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b'));
        String initialState = "q0";
        Set<String> finalStates = new HashSet<>(Arrays.asList("q4"));

        // Define transitions
        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();

        // Initialize transition maps for each state
        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        // Add transitions
        // δ(q0,a) = q1
        FiniteAutomaton.addTransition(transitions, "q0", 'a', "q1");

        // δ(q1,b) = q1
        FiniteAutomaton.addTransition(transitions, "q1", 'b', "q1");

        // δ(q1,b) = q2 (This makes it non-deterministic)
        FiniteAutomaton.addTransition(transitions, "q1", 'b', "q2");

        // δ(q2,b) = q3
        FiniteAutomaton.addTransition(transitions, "q2", 'b', "q3");

        // δ(q3,a) = q1
        FiniteAutomaton.addTransition(transitions, "q3", 'a', "q1");

        // δ(q2,a) = q4
        FiniteAutomaton.addTransition(transitions, "q2", 'a', "q4");

        // Create the finite automaton
        FiniteAutomaton fa = new FiniteAutomaton(states, alphabet, transitions, initialState, finalStates);

        System.out.println("================ Variant 6 ================");

        // Print the automaton
        System.out.println(fa);

        // Check if it's deterministic
        boolean isDeterministic = fa.isDeterministic();
        System.out.println("Is the automaton deterministic? " + isDeterministic);

        // Convert FA to regular grammar
        Grammar regularGrammar = fa.toRegularGrammar();
        System.out.println("\nConverted to Regular Grammar:");
        System.out.println(regularGrammar);

        // Classify the grammar
        String grammarType = regularGrammar.classifyGrammar();
        System.out.println("Grammar Classification: " + grammarType);

        // Convert NDFA to DFA
        if (!isDeterministic) {
            System.out.println("\nConverting NDFA to DFA:");
            FiniteAutomaton dfa = fa.toDFA();
            System.out.println(dfa);

            // Verify that the DFA is indeed deterministic
            System.out.println("Is the converted DFA deterministic? " + dfa.isDeterministic());
        }
    }
}