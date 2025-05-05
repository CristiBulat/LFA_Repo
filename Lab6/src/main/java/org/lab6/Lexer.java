package org.lab6;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;


public class Lexer {
    public final String source;
    public int position;
    public int line;
    public int column;
    public char currentChar;

    public static final Map<String, Token.Type> KEYWORDS;

    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("if", Token.Type.IF);
        KEYWORDS.put("else", Token.Type.ELSE);
        KEYWORDS.put("while", Token.Type.WHILE);
        KEYWORDS.put("for", Token.Type.FOR);
        KEYWORDS.put("function", Token.Type.FUNCTION);
        KEYWORDS.put("return", Token.Type.RETURN);
    }

    public Lexer(String source){
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;

        if (!source.isEmpty()) {
            this.currentChar = source.charAt(0);
        } else {
            this.currentChar = '\0';
        }
    }

    private void advance() {
        position++;
        column++;

        if (position >= source.length()) {
            currentChar = '\0';
        } else {
            currentChar = source.charAt(position);

            if (currentChar == '\n') {
                line++;
                column = 1;
            }
        }
    }

    private char peek() {
        int peekPos = position + 1;
        if (peekPos >= source.length()) {
            return '\0';
        }
        return source.charAt(peekPos);
    }

    /**
     * Skips whitespace characters
     */
    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    /**
     * Skips comments (both single-line and multi-line)
     */
    private void skipComments() {
        // Single-line comments
        if (currentChar == '/' && peek() == '/') {
            while (currentChar != '\0' && currentChar != '\n') {
                advance();
            }
            // Skip the newline
            if (currentChar != '\0') {
                advance();
            }
        }
        // Multi-line comments
        else if (currentChar == '/' && peek() == '*') {
            advance(); // Skip /
            advance(); // Skip *

            boolean done = false;
            while (!done && currentChar != '\0') {
                if (currentChar == '*' && peek() == '/') {
                    advance(); // Skip *
                    advance(); // Skip /
                    done = true;
                } else {
                    advance();
                }
            }
        }
    }

    /**
     * Processes numeric literals (integers and floats)
     */
    private Token number() {
        StringBuilder value = new StringBuilder();
        int startColumn = column;
        boolean isFloat = false;

        // Process digits before decimal point
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            value.append(currentChar);
            advance();
        }

        // Check for decimal point
        if (currentChar == '.' && Character.isDigit(peek())) {
            isFloat = true;
            value.append(currentChar); // Add the decimal point
            advance();

            // Process digits after decimal point
            while (currentChar != '\0' && Character.isDigit(currentChar)) {
                value.append(currentChar);
                advance();
            }
        }

        Token.Type type = isFloat ? Token.Type.FLOAT : Token.Type.INTEGER;
        return new Token(type, value.toString(), line, startColumn);
    }

    /**
     * Processes identifiers and keywords
     */
    private Token identifier() {
        StringBuilder value = new StringBuilder();
        int startColumn = column;

        while (currentChar != '\0' &&
                (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            value.append(currentChar);
            advance();
        }

        String identifier = value.toString();
        // Check if the identifier is a keyword
        Token.Type type = KEYWORDS.getOrDefault(identifier, Token.Type.IDENTIFIER);

        return new Token(type, identifier, line, startColumn);
    }

    /**
     * Processes string literals
     */
    private Token string() {
        StringBuilder value = new StringBuilder();
        int startColumn = column;

        // Skip the opening quote
        advance();

        while (currentChar != '\0' && currentChar != '"') {
            // Handle escape sequences
            if (currentChar == '\\') {
                advance();
                switch (currentChar) {
                    case 'n': value.append('\n'); break;
                    case 't': value.append('\t'); break;
                    case 'r': value.append('\r'); break;
                    case '\\': value.append('\\'); break;
                    case '"': value.append('"'); break;
                    default: value.append('\\').append(currentChar);
                }
            } else {
                value.append(currentChar);
            }
            advance();
        }

        // Skip the closing quote
        if (currentChar == '"') {
            advance();
        }

        return new Token(Token.Type.STRING, value.toString(), line, startColumn);
    }

    /**
     * Gets the next token from the input
     */
    public Token getNextToken() {
        while (currentChar != '\0') {
            // Skip whitespace
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            // Skip comments
            if (currentChar == '/' && (peek() == '/' || peek() == '*')) {
                skipComments();
                continue;
            }

            // Record the start position for error reporting
            int currentLine = line;
            int currentColumn = column;

            // Check for different token types

            // Numbers
            if (Character.isDigit(currentChar)) {
                return number();
            }

            // Identifiers and keywords
            if (Character.isLetter(currentChar) || currentChar == '_') {
                return identifier();
            }

            // String literals
            if (currentChar == '"') {
                return string();
            }

            // Operators and special characters
            switch (currentChar) {
                case '+':
                    advance();
                    return new Token(Token.Type.PLUS, "+", currentLine, currentColumn);

                case '-':
                    advance();
                    return new Token(Token.Type.MINUS, "-", currentLine, currentColumn);

                case '*':
                    advance();
                    return new Token(Token.Type.MULTIPLY, "*", currentLine, currentColumn);

                case '/':
                    advance();
                    return new Token(Token.Type.DIVIDE, "/", currentLine, currentColumn);

                case '%':
                    advance();
                    return new Token(Token.Type.MODULO, "%", currentLine, currentColumn);

                case '=':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(Token.Type.EQUALS, "==", currentLine, currentColumn);
                    }
                    return new Token(Token.Type.ASSIGN, "=", currentLine, currentColumn);

                case '!':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(Token.Type.NOT_EQUALS, "!=", currentLine, currentColumn);
                    }
                    return new Token(Token.Type.NOT, "!", currentLine, currentColumn);

                case '<':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(Token.Type.LESS_EQUALS, "<=", currentLine, currentColumn);
                    }
                    return new Token(Token.Type.LESS, "<", currentLine, currentColumn);

                case '>':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        return new Token(Token.Type.GREATER_EQUALS, ">=", currentLine, currentColumn);
                    }
                    return new Token(Token.Type.GREATER, ">", currentLine, currentColumn);

                case '&':
                    advance();
                    if (currentChar == '&') {
                        advance();
                        return new Token(Token.Type.AND, "&&", currentLine, currentColumn);
                    }
                    return new Token(Token.Type.UNKNOWN, "&", currentLine, currentColumn);

                case '|':
                    advance();
                    if (currentChar == '|') {
                        advance();
                        return new Token(Token.Type.OR, "||", currentLine, currentColumn);
                    }
                    return new Token(Token.Type.UNKNOWN, "|", currentLine, currentColumn);

                case '(':
                    advance();
                    return new Token(Token.Type.LPAREN, "(", currentLine, currentColumn);

                case ')':
                    advance();
                    return new Token(Token.Type.RPAREN, ")", currentLine, currentColumn);

                case '{':
                    advance();
                    return new Token(Token.Type.LBRACE, "{", currentLine, currentColumn);

                case '}':
                    advance();
                    return new Token(Token.Type.RBRACE, "}", currentLine, currentColumn);

                case ';':
                    advance();
                    return new Token(Token.Type.SEMICOLON, ";", currentLine, currentColumn);

                case ',':
                    advance();
                    return new Token(Token.Type.COMMA, ",", currentLine, currentColumn);

                default:
                    String unknownChar = String.valueOf(currentChar);
                    advance();
                    return new Token(Token.Type.UNKNOWN, unknownChar, currentLine, currentColumn);
            }
        }

        // End of file
        return new Token(Token.Type.EOF, "", line, column);
    }

    /**
     * Tokenizes the entire input and returns a list of tokens
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token = getNextToken();

        while (token.getType() != Token.Type.EOF) {
            tokens.add(token);
            token = getNextToken();
        }

        // Add the EOF token
        tokens.add(token);

        return tokens;
    }
}

