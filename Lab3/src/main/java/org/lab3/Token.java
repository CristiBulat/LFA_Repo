package org.lab3;

public class Token {
    public enum Type{
        // Keywords
        IF, ELSE, WHILE, FOR, FUNCTION, RETURN,

        // Literals
        INTEGER, FLOAT, STRING, IDENTIFIER,

        // Operators
        PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
        ASSIGN,
        EQUALS, NOT_EQUALS, LESS, GREATER, LESS_EQUALS, GREATER_EQUALS,
        AND, OR, NOT,

        // Delimiters
        LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA,

        // Special
        EOF, UNKNOWN
    }

    private final Type type;
    private final String value;
    private final int line;
    private final int column;

    public Token(Type type, String value, int line, int column){
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s', line: %d, col: %d)",
                type, value, line, column);
    }
}
