package org.lab1;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Set<String> V_n = Set.of("S", "I", "J", "K");
        Set<String> V_t = Set.of("a", "b", "c", "e", "n", "f", "m");
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("cI"));
        P.put("I", List.of("bJ", "fI", "eK", "e"));
        P.put("J", List.of("nJ", "cS"));
        P.put("K", List.of("nK", "m"));

        String S = "S";
        Grammar grammar = new Grammar(V_n, V_t, P, S);

        System.out.println("Generated strings:");
        for (int i = 0; i < 5; i++) {
            System.out.println(grammar.generateString());
        }

        FiniteAutomaton automaton = grammar.toFiniteAutomaton();

        System.out.println();
        String testString1 = "cIbJ";
        System.out.println("Does \"" + testString1 + "\" belong to the language? " +
                automaton.stringBelongToLanguage(testString1));

        String testString2 = "cem";
        System.out.println("Does \"" + testString2 + "\" belong to the language? " +
                automaton.stringBelongToLanguage(testString2));
    }
}