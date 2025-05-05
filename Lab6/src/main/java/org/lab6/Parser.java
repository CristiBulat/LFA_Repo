package org.lab6;

import org.lab6.Token;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Parse the complete program
    public ProgramNode parse() {
        ProgramNode program = new ProgramNode();

        while (!isAtEnd()) {
            program.addStatement(declaration());
        }

        return program;
    }

    // Parse declarations
    private ASTNode declaration() {
        try {
            if (match(Token.Type.FUNCTION)) {
                return functionDeclaration();
            }

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    // Parse function declarations
    private FunctionDeclarationNode functionDeclaration() {
        Token nameToken = consume(Token.Type.IDENTIFIER, "Expect function name.");
        IdentifierNode name = new IdentifierNode(nameToken.getValue());

        consume(Token.Type.LPAREN, "Expect '(' after function name.");

        List<IdentifierNode> parameters = new ArrayList<>();
        if (!check(Token.Type.RPAREN)) {
            do {
                Token param = consume(Token.Type.IDENTIFIER, "Expect parameter name.");
                parameters.add(new IdentifierNode(param.getValue()));
            } while (match(Token.Type.COMMA));
        }

        consume(Token.Type.RPAREN, "Expect ')' after parameters.");

        consume(Token.Type.LBRACE, "Expect '{' before function body.");
        BlockNode body = block();

        return new FunctionDeclarationNode(name, parameters, body);
    }

    // Parse statements
    private ASTNode statement() {
        if (match(Token.Type.RETURN)) {
            return returnStatement();
        }

        if (match(Token.Type.IF)) {
            return ifStatement();
        }

        if (match(Token.Type.WHILE)) {
            return whileStatement();
        }

        if (match(Token.Type.FOR)) {
            return forStatement();
        }

        if (match(Token.Type.LBRACE)) {
            return block();
        }

        return expressionStatement();
    }

    // Parse block
    private BlockNode block() {
        BlockNode blockNode = new BlockNode();

        while (!check(Token.Type.RBRACE) && !isAtEnd()) {
            blockNode.addStatement(declaration());
        }

        consume(Token.Type.RBRACE, "Expect '}' after block.");
        return blockNode;
    }

    // Parse return statement
    private ReturnStatementNode returnStatement() {
        ASTNode value = null;
        if (!check(Token.Type.SEMICOLON)) {
            value = expression();
        }

        consume(Token.Type.SEMICOLON, "Expect ';' after return value.");
        return new ReturnStatementNode(value);
    }

    // Parse if statement
    private IfStatementNode ifStatement() {
        consume(Token.Type.LPAREN, "Expect '(' after 'if'.");
        ASTNode condition = expression();
        consume(Token.Type.RPAREN, "Expect ')' after if condition.");

        ASTNode thenBranch = statement();
        ASTNode elseBranch = null;
        if (match(Token.Type.ELSE)) {
            elseBranch = statement();
        }

        return new IfStatementNode(condition, thenBranch, elseBranch);
    }

    // Parse while statement
    private WhileStatementNode whileStatement() {
        consume(Token.Type.LPAREN, "Expect '(' after 'while'.");
        ASTNode condition = expression();
        consume(Token.Type.RPAREN, "Expect ')' after condition.");
        ASTNode body = statement();

        return new WhileStatementNode(condition, body);
    }

    // Parse for statement
    private ForStatementNode forStatement() {
        consume(Token.Type.LPAREN, "Expect '(' after 'for'.");

        ASTNode initializer;
        if (match(Token.Type.SEMICOLON)) {
            initializer = null;
        } else {
            initializer = expressionStatement();
        }

        ASTNode condition = null;
        if (!check(Token.Type.SEMICOLON)) {
            condition = expression();
        }
        consume(Token.Type.SEMICOLON, "Expect ';' after loop condition.");

        ASTNode increment = null;
        if (!check(Token.Type.RPAREN)) {
            increment = expression();
        }
        consume(Token.Type.RPAREN, "Expect ')' after for clauses.");

        ASTNode body = statement();

        return new ForStatementNode(initializer, condition, increment, body);
    }

    // Parse expression statement
    private ASTNode expressionStatement() {
        ASTNode expr = expression();
        consume(Token.Type.SEMICOLON, "Expect ';' after expression.");
        return new ExpressionStatementNode(expr);
    }

    // Parse expression
    private ASTNode expression() {
        return assignment();
    }

    // Parse assignment
    private ASTNode assignment() {
        ASTNode expr = or();

        if (match(Token.Type.ASSIGN)) {
            ASTNode value = assignment();

            if (expr instanceof IdentifierNode) {
                IdentifierNode target = (IdentifierNode) expr;
                return new AssignmentNode(target, value);
            }

            error(previous(), "Invalid assignment target.");
        }

        return expr;
    }

    // Parse logical OR
    private ASTNode or() {
        ASTNode expr = and();

        while (match(Token.Type.OR)) {
            String operator = previous().getValue();
            ASTNode right = and();
            expr = new BinaryExpressionNode(expr, operator, right);
        }

        return expr;
    }

    // Parse logical AND
    private ASTNode and() {
        ASTNode expr = equality();

        while (match(Token.Type.AND)) {
            String operator = previous().getValue();
            ASTNode right = equality();
            expr = new BinaryExpressionNode(expr, operator, right);
        }

        return expr;
    }

    // Parse equality expressions
    private ASTNode equality() {
        ASTNode expr = comparison();

        while (match(Token.Type.EQUALS, Token.Type.NOT_EQUALS)) {
            String operator = previous().getValue();
            ASTNode right = comparison();
            expr = new BinaryExpressionNode(expr, operator, right);
        }

        return expr;
    }

    // Parse comparison expressions
    private ASTNode comparison() {
        ASTNode expr = term();

        while (match(Token.Type.LESS, Token.Type.GREATER, Token.Type.LESS_EQUALS, Token.Type.GREATER_EQUALS)) {
            String operator = previous().getValue();
            ASTNode right = term();
            expr = new BinaryExpressionNode(expr, operator, right);
        }

        return expr;
    }

    // Parse term expressions
    private ASTNode term() {
        ASTNode expr = factor();

        while (match(Token.Type.PLUS, Token.Type.MINUS)) {
            String operator = previous().getValue();
            ASTNode right = factor();
            expr = new BinaryExpressionNode(expr, operator, right);
        }

        return expr;
    }

    // Parse factor expressions
    private ASTNode factor() {
        ASTNode expr = unary();

        while (match(Token.Type.MULTIPLY, Token.Type.DIVIDE, Token.Type.MODULO)) {
            String operator = previous().getValue();
            ASTNode right = unary();
            expr = new BinaryExpressionNode(expr, operator, right);
        }

        return expr;
    }

    // Parse unary expressions
    private ASTNode unary() {
        if (match(Token.Type.NOT, Token.Type.MINUS)) {
            String operator = previous().getValue();
            ASTNode right = unary();
            return new UnaryExpressionNode(operator, right);
        }

        return call();
    }

    // Parse function calls
    private ASTNode call() {
        ASTNode expr = primary();

        while (true) {
            if (match(Token.Type.LPAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    // Finish parsing function call
    private ASTNode finishCall(ASTNode callee) {
        List<ASTNode> arguments = new ArrayList<>();

        if (!check(Token.Type.RPAREN)) {
            do {
                arguments.add(expression());
            } while (match(Token.Type.COMMA));
        }

        consume(Token.Type.RPAREN, "Expect ')' after arguments.");

        if (!(callee instanceof IdentifierNode)) {
            error(previous(), "Can only call functions.");
            return callee;
        }

        return new CallExpressionNode((IdentifierNode)callee, arguments);
    }

    // Parse primary expressions
    private ASTNode primary() {
        if (match(Token.Type.INTEGER)) {
            return new IntegerLiteralNode(Integer.parseInt(previous().getValue()));
        }

        if (match(Token.Type.FLOAT)) {
            return new FloatLiteralNode(Double.parseDouble(previous().getValue()));
        }

        if (match(Token.Type.STRING)) {
            return new StringLiteralNode(previous().getValue());
        }

        if (match(Token.Type.IDENTIFIER)) {
            return new IdentifierNode(previous().getValue());
        }

        if (match(Token.Type.LPAREN)) {
            ASTNode expr = expression();
            consume(Token.Type.RPAREN, "Expect ')' after expression.");
            return expr;
        }

        throw error(peek(), "Expect expression.");
    }

    // Utility methods for token matching and error handling

    private boolean match(Token.Type... types) {
        for (Token.Type type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(Token.Type type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(Token.Type type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == Token.Type.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        String errorMsg = String.format("[line %d, col %d] Error at '%s': %s",
                token.getLine(), token.getColumn(), token.getValue(), message);
        System.err.println(errorMsg);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getType() == Token.Type.SEMICOLON) return;

            switch (peek().getType()) {
                case FUNCTION:
                case IF:
                case WHILE:
                case FOR:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    private static class ParseError extends RuntimeException {}
}