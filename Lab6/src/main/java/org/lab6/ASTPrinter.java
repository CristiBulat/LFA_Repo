package org.lab6;

public class ASTPrinter implements ASTVisitor {
    private StringBuilder builder = new StringBuilder();
    private int indentLevel = 0;

    public String print(ASTNode node) {
        builder = new StringBuilder();
        indentLevel = 0;
        node.accept(this);
        return builder.toString();
    }

    private void indent() {
        for (int i = 0; i < indentLevel; i++) {
            builder.append("  ");
        }
    }

    @Override
    public void visit(ProgramNode node) {
        builder.append("Program\n");
        indentLevel++;

        for (ASTNode statement : node.getStatements()) {
            indent();
            statement.accept(this);
        }

        indentLevel--;
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        builder.append("Function Declaration: ");
        node.getName().accept(this);
        builder.append("\n");

        indentLevel++;

        indent();
        builder.append("Parameters: ");
        for (IdentifierNode param : node.getParameters()) {
            param.accept(this);
            builder.append(" ");
        }
        builder.append("\n");

        indent();
        builder.append("Body:\n");
        indentLevel++;
        node.getBody().accept(this);
        indentLevel -= 2;
    }

    @Override
    public void visit(BlockNode node) {
        builder.append("Block\n");
        indentLevel++;

        for (ASTNode statement : node.getStatements()) {
            indent();
            statement.accept(this);
        }

        indentLevel--;
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        builder.append("Variable Declaration: ");
        node.getName().accept(this);

        if (node.getInitializer() != null) {
            builder.append(" = ");
            node.getInitializer().accept(this);
        }

        builder.append("\n");
    }

    @Override
    public void visit(AssignmentNode node) {
        builder.append("Assignment: ");
        node.getTarget().accept(this);
        builder.append(" = ");
        node.getValue().accept(this);
        builder.append("\n");
    }

    @Override
    public void visit(IfStatementNode node) {
        builder.append("If Statement\n");
        indentLevel++;

        indent();
        builder.append("Condition: ");
        node.getCondition().accept(this);
        builder.append("\n");

        indent();
        builder.append("Then Branch:\n");
        indentLevel++;
        indent();
        node.getThenBranch().accept(this);
        indentLevel--;

        if (node.getElseBranch() != null) {
            indent();
            builder.append("Else Branch:\n");
            indentLevel++;
            indent();
            node.getElseBranch().accept(this);
            indentLevel--;
        }

        indentLevel--;
    }

    @Override
    public void visit(WhileStatementNode node) {
        builder.append("While Statement\n");
        indentLevel++;

        indent();
        builder.append("Condition: ");
        node.getCondition().accept(this);
        builder.append("\n");

        indent();
        builder.append("Body:\n");
        indentLevel++;
        indent();
        node.getBody().accept(this);
        indentLevel -= 2;
    }

    @Override
    public void visit(ForStatementNode node) {
        builder.append("For Statement\n");
        indentLevel++;

        if (node.getInitializer() != null) {
            indent();
            builder.append("Initializer: ");
            node.getInitializer().accept(this);
            builder.append("\n");
        }

        if (node.getCondition() != null) {
            indent();
            builder.append("Condition: ");
            node.getCondition().accept(this);
            builder.append("\n");
        }

        if (node.getIncrement() != null) {
            indent();
            builder.append("Increment: ");
            node.getIncrement().accept(this);
            builder.append("\n");
        }

        indent();
        builder.append("Body:\n");
        indentLevel++;
        indent();
        node.getBody().accept(this);
        indentLevel -= 2;
    }

    @Override
    public void visit(ReturnStatementNode node) {
        builder.append("Return");

        if (node.getValue() != null) {
            builder.append(": ");
            node.getValue().accept(this);
        }

        builder.append("\n");
    }

    @Override
    public void visit(ExpressionStatementNode node) {
        builder.append("Expression Statement: ");
        node.getExpression().accept(this);
        builder.append("\n");
    }

    @Override
    public void visit(BinaryExpressionNode node) {
        builder.append("(");
        node.getLeft().accept(this);
        builder.append(" ").append(node.getOperator()).append(" ");
        node.getRight().accept(this);
        builder.append(")");
    }

    @Override
    public void visit(UnaryExpressionNode node) {
        builder.append(node.getOperator());
        node.getOperand().accept(this);
    }

    @Override
    public void visit(CallExpressionNode node) {
        builder.append("Call: ");
        node.getCallee().accept(this);
        builder.append("(");

        for (int i = 0; i < node.getArguments().size(); i++) {
            if (i > 0) builder.append(", ");
            node.getArguments().get(i).accept(this);
        }

        builder.append(")");
    }

    @Override
    public void visit(IdentifierNode node) {
        builder.append(node.getName());
    }

    @Override
    public void visit(IntegerLiteralNode node) {
        builder.append(node.getValue());
    }

    @Override
    public void visit(FloatLiteralNode node) {
        builder.append(node.getValue());
    }

    @Override
    public void visit(StringLiteralNode node) {
        builder.append("\"").append(node.getValue()).append("\"");
    }
}