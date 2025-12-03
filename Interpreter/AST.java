import java.util.ArrayList;
import java.util.List;

/**
 * Defines the Abstract Syntax Tree (AST) structure used by the Analyser.
 */

abstract class ASTNode {
    /** Abstract method to recursively print the tree structure. */
    abstract public String toTreeString(String indent);
}

// --- AST Node Implementations ---

public class ProgramNode extends ASTNode {
    String name;
    ASTNode body;

    public ProgramNode(String name, ASTNode body) {
        this.name = name;
        this.body = body;
    }

    @Override
    public String toTreeString(String indent) {
        return indent + "PROGRAM " + name + "\n" + body.toTreeString(indent + "  ");
    }
}

public class BodyNode extends ASTNode {
    ASTNode declarations; // Can be null
    ASTNode statements;

    public BodyNode(ASTNode declarations, ASTNode statements) {
        this.declarations = declarations;
        this.statements = statements;
    }

    @Override
    public String toTreeString(String indent) {
        // Only print declarations if they are not a NOOP node
        String declStr = (declarations instanceof NoOpNode) ? "" : declarations.toTreeString(indent);
        return declStr + statements.toTreeString(indent);
    }
}

public class StatementsNode extends ASTNode {
    List<ASTNode> statements = new ArrayList<>();

    public StatementsNode(ASTNode statement) {
        this.statements.add(statement);
    }

    public void addStatement(ASTNode statement) {
        this.statements.add(statement);
    }

    @Override
    public String toTreeString(String indent) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode statement : statements) {
            sb.append(statement.toTreeString(indent));
        }
        return sb.toString();
    }
}

public class IfNode extends ASTNode {
    ASTNode condition;
    ASTNode thenBranch;
    ASTNode elseBranch; // Should be a NoOpNode if no else

    public IfNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public String toTreeString(String indent) {
        return indent + "IF\n" +
               condition.toTreeString(indent + "  ") +
               thenBranch.toTreeString(indent + "  ") +
               elseBranch.toTreeString(indent + "  ");
    }
}

public class BinaryOpNode extends ASTNode {
    String operator;
    ASTNode left;
    ASTNode right;

    public BinaryOpNode(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toTreeString(String indent) {
        return indent + operator + "\n" +
               left.toTreeString(indent + "  ") +
               right.toTreeString(indent + "  ");
    }
}

public class UnaryOpNode extends ASTNode {
    String operator;
    ASTNode operand;

    public UnaryOpNode(String operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public String toTreeString(String indent) {
        return indent + operator + "\n" + operand.toTreeString(indent + "  ");
    }
}

public class IntLiteralNode extends ASTNode {
    String value;

    public IntLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String toTreeString(String indent) {
        return indent + "INT_LITERAL " + value + "\n";
    }
}

public class BoolLiteralNode extends ASTNode {
    String value; // "True" or "False"

    public BoolLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String toTreeString(String indent) {
        return indent + "BOOL_LITERAL " + value + "\n";
    }
}

public class PrintNode extends ASTNode {
    ASTNode expression;
    String type; // Used to differentiate between PRINT_INT, PRINT_BOOL, etc.

    public PrintNode(ASTNode expression, String type) {
        this.expression = expression;
        this.type = "PRINT_" + type.toUpperCase(); // Example: PRINT_BOOL
    }


    public String toTreeString(String indent) {
        return indent + type + "\n" + expression.toTreeString(indent + "  ");
    }
}

public class AssignNode extends ASTNode {
    String id;
    ASTNode expression;

    public AssignNode(String id, ASTNode expression) {
        this.id = id;
        this.expression = expression;
    }

    public String toTreeString(String indent) {
        return indent + "ASSIGN " + id + "\n" + expression.toTreeString(indent + "  ");
    }
}

public class IdNode extends ASTNode {
    String id;

    public IdNode(String id) {
        this.id = id;
    }

    public String toTreeString(String indent) {
        return indent + "ID " + id + "\n";
    }
}

public class NoOpNode extends ASTNode {
    public String toTreeString(String indent) {
        return indent + "NOOP\n";
    }
}