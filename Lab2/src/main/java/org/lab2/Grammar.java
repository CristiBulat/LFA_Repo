package org.lab2;

import java.util.*;

/**
 * Class to represent a Grammar based on Chomsky hierarchy
 */
public class Grammar {
    private Set<String> nonTerminalSymbols;
    private Set<String> terminalSymbols;
    private String startSymbol;
    private Map<String, List<String>> productions;

    public Grammar(Set<String> nonTerminalSymbols, Set<String> terminalSymbols,
                   String startSymbol, Map<String, List<String>> productions) {
        this.nonTerminalSymbols = nonTerminalSymbols;
        this.terminalSymbols = terminalSymbols;
        this.startSymbol = startSymbol;
        this.productions = productions;
    }

    /**
     * Method to classify grammar based on Chomsky hierarchy
     * @return String representation of grammar type
     */
    public String classifyGrammar() {
        // Check for Type 3 (Regular Grammar)
        boolean isRightLinear = true;
        boolean isLeftLinear = true;

        for (String nonTerminal : productions.keySet()) {
            List<String> rules = productions.get(nonTerminal);
            for (String rule : rules) {
                // Check right-linear form: A -> aB or A -> a
                if (!isRightLinearProduction(rule) && !isTerminalOnly(rule)) {
                    isRightLinear = false;
                }

                // Check left-linear form: A -> Ba or A -> a
                if (!isLeftLinearProduction(rule) && !isTerminalOnly(rule)) {
                    isLeftLinear = false;
                }

                // If neither right nor left linear, break early
                if (!isRightLinear && !isLeftLinear) {
                    break;
                }
            }
        }

        if (isRightLinear || isLeftLinear) {
            return "Type 3 (Regular Grammar)";
        }

        // Check for Type 2 (Context-Free Grammar)
        boolean isContextFree = true;
        for (String nonTerminal : productions.keySet()) {
            // In a CFG, LHS must be a single non-terminal
            if (nonTerminal.length() != 1 || !nonTerminalSymbols.contains(nonTerminal)) {
                isContextFree = false;
                break;
            }
        }

        if (isContextFree) {
            return "Type 2 (Context-Free Grammar)";
        }

        // Check for Type 1 (Context-Sensitive Grammar)
        boolean isContextSensitive = true;
        for (String lhs : productions.keySet()) {
            List<String> rules = productions.get(lhs);
            for (String rhs : rules) {
                // In a CSG, |LHS| <= |RHS| (except for S -> ε if S doesn't appear on RHS)
                if (rhs.length() < lhs.length() &&
                        !(lhs.equals(startSymbol) && rhs.isEmpty())) {
                    isContextSensitive = false;
                    break;
                }
            }
            if (!isContextSensitive) break;
        }

        if (isContextSensitive) {
            return "Type 1 (Context-Sensitive Grammar)";
        }

        // If none of the above, it's Type 0 (Unrestricted Grammar)
        return "Type 0 (Unrestricted Grammar)";
    }

    private boolean isRightLinearProduction(String rule) {
        // Right-linear: A -> aB (terminal followed by at most one non-terminal)
        if (rule.isEmpty()) return false;

        // Check if the last character is a non-terminal and the rest are terminals
        if (rule.length() > 1) {
            String lastChar = rule.substring(rule.length() - 1);
            String restOfRule = rule.substring(0, rule.length() - 1);

            return nonTerminalSymbols.contains(lastChar) &&
                    isAllTerminals(restOfRule);
        }
        return false;
    }

    private boolean isLeftLinearProduction(String rule) {
        // Left-linear: A -> Ba (at most one non-terminal followed by terminals)
        if (rule.isEmpty()) return false;

        // Check if the first character is a non-terminal and the rest are terminals
        if (rule.length() > 1) {
            String firstChar = rule.substring(0, 1);
            String restOfRule = rule.substring(1);

            return nonTerminalSymbols.contains(firstChar) &&
                    isAllTerminals(restOfRule);
        }
        return false;
    }

    private boolean isTerminalOnly(String rule) {
        // Rule contains only terminal symbols
        return isAllTerminals(rule);
    }

    private boolean isAllTerminals(String str) {
        for (int i = 0; i < str.length(); i++) {
            String ch = str.substring(i, i + 1);
            if (!terminalSymbols.contains(ch)) {
                return false;
            }
        }
        return true;
    }

    // Getters
    public Set<String> getNonTerminalSymbols() {
        return nonTerminalSymbols;
    }

    public Set<String> getTerminalSymbols() {
        return terminalSymbols;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public Map<String, List<String>> getProductions() {
        return productions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grammar:\n");
        sb.append("Non-terminals: ").append(nonTerminalSymbols).append("\n");
        sb.append("Terminals: ").append(terminalSymbols).append("\n");
        sb.append("Start Symbol: ").append(startSymbol).append("\n");
        sb.append("Productions:\n");

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String lhs = entry.getKey();
            List<String> rhs = entry.getValue();
            sb.append(lhs).append(" -> ");
            sb.append(String.join(" | ", rhs)).append("\n");
        }

        return sb.toString();
    }
}