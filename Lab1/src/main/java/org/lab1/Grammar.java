package org.lab1;

import java.util.*;

class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Map<String, List<String>> productionRules;
    private String startSymbol;

    public Grammar(Set<String> V_n, Set<String> V_t, Map<String, List<String>> P, String S) {
        this.nonTerminals = V_n;
        this.terminals = V_t;
        this.productionRules = P;
        this.startSymbol = S;
    }

    public String generateString() {
        Random random = new Random();
        StringBuilder result = new StringBuilder(startSymbol);

        while (true) {
            boolean replaced = false;
            for (String nonTerminal : nonTerminals) {
                if (result.toString().contains(nonTerminal)) {
                    List<String> productions = productionRules.get(nonTerminal);
                    String chosenProduction = productions.get(random.nextInt(productions.size()));
                    int index = result.indexOf(nonTerminal);
                    result.replace(index, index + nonTerminal.length(), chosenProduction);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) break;
        }
        return result.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        return new FiniteAutomaton(this);
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Map<String, List<String>> getProductionRules() {
        return productionRules;
    }

    public String getStartSymbol() {
        return startSymbol;
    }
}