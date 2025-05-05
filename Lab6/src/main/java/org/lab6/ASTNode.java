package org.lab6;

import java.util.ArrayList;
import java.util.List;

// Base AST Node interface
public interface ASTNode {
    void accept(ASTVisitor visitor);
}

// Visitor interface for traversing the AST
interface ASTVisitor {
    void visit(ProgramNode node);
    void visit(FunctionDeclarationNode node);
    void visit(BlockNode node);
    void visit(VariableDeclarationNode node);
    void visit(AssignmentNode node);
    void visit(IfStatementNode node);
    void visit(WhileStatementNode node);
    void visit(ForStatementNode node);
    void visit(ReturnStatementNode node);
    void visit(ExpressionStatementNode node);
    void visit(BinaryExpressionNode node);
    void visit(UnaryExpressionNode node);
    void visit(CallExpressionNode node);
    void visit(IdentifierNode node);
    void visit(IntegerLiteralNode node);
    void visit(FloatLiteralNode node);
    void visit(StringLiteralNode node);
}

// Root node for the program
class ProgramNode implements ASTNode {
    private List<ASTNode> statements;

    public ProgramNode() {
        statements = new ArrayList<>();
    }

    public void addStatement(ASTNode statement) {
        statements.add(statement);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Function declaration node
class FunctionDeclarationNode implements ASTNode {
    private IdentifierNode name;
    private List<IdentifierNode> parameters;
    private BlockNode body;

    public FunctionDeclarationNode(IdentifierNode name, List<IdentifierNode> parameters, BlockNode body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public IdentifierNode getName() {
        return name;
    }

    public List<IdentifierNode> getParameters() {
        return parameters;
    }

    public BlockNode getBody() {
        return body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Block node for code blocks enclosed in braces
class BlockNode implements ASTNode {
    private List<ASTNode> statements;

    public BlockNode() {
        statements = new ArrayList<>();
    }

    public void addStatement(ASTNode statement) {
        statements.add(statement);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Variable declaration node
class VariableDeclarationNode implements ASTNode {
    private IdentifierNode name;
    private ASTNode initializer;

    public VariableDeclarationNode(IdentifierNode name, ASTNode initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public IdentifierNode getName() {
        return name;
    }

    public ASTNode getInitializer() {
        return initializer;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Assignment node
class AssignmentNode implements ASTNode {
    private IdentifierNode target;
    private ASTNode value;

    public AssignmentNode(IdentifierNode target, ASTNode value) {
        this.target = target;
        this.value = value;
    }

    public IdentifierNode getTarget() {
        return target;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// If statement node
class IfStatementNode implements ASTNode {
    private ASTNode condition;
    private ASTNode thenBranch;
    private ASTNode elseBranch;

    public IfStatementNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getThenBranch() {
        return thenBranch;
    }

    public ASTNode getElseBranch() {
        return elseBranch;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// While statement node
class WhileStatementNode implements ASTNode {
    private ASTNode condition;
    private ASTNode body;

    public WhileStatementNode(ASTNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// For statement node
class ForStatementNode implements ASTNode {
    private ASTNode initializer;
    private ASTNode condition;
    private ASTNode increment;
    private ASTNode body;

    public ForStatementNode(ASTNode initializer, ASTNode condition, ASTNode increment, ASTNode body) {
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    public ASTNode getInitializer() {
        return initializer;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getIncrement() {
        return increment;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Return statement node
class ReturnStatementNode implements ASTNode {
    private ASTNode value;

    public ReturnStatementNode(ASTNode value) {
        this.value = value;
    }

    public ASTNode getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Expression statement node
class ExpressionStatementNode implements ASTNode {
    private ASTNode expression;

    public ExpressionStatementNode(ASTNode expression) {
        this.expression = expression;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Binary expression node
class BinaryExpressionNode implements ASTNode {
    private ASTNode left;
    private String operator;
    private ASTNode right;

    public BinaryExpressionNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ASTNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getRight() {
        return right;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Unary expression node
class UnaryExpressionNode implements ASTNode {
    private String operator;
    private ASTNode operand;

    public UnaryExpressionNode(String operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getOperand() {
        return operand;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Function call expression node
class CallExpressionNode implements ASTNode {
    private IdentifierNode callee;
    private List<ASTNode> arguments;

    public CallExpressionNode(IdentifierNode callee, List<ASTNode> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    public IdentifierNode getCallee() {
        return callee;
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Identifier node
class IdentifierNode implements ASTNode {
    private String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Integer literal node
class IntegerLiteralNode implements ASTNode {
    private int value;

    public IntegerLiteralNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// Float literal node
class FloatLiteralNode implements ASTNode {
    private double value;

    public FloatLiteralNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

// String literal node
class StringLiteralNode implements ASTNode {
    private String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}