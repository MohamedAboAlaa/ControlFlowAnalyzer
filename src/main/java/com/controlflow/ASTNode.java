package com.controlflow;

import java.util.ArrayList;
import java.util.List;

/**
 * ASTNode — Base class for all nodes in the Abstract Syntax Tree.
 *
 * Design is intentionally flat and simple:
 *   - No generics, no abstract methods with type parameters.
 *   - Each node type is a plain inner static class.
 *   - Children are stored as a List<ASTNode> for uniform tree printing.
 */
public class ASTNode {

    // Human-readable label shown when printing the tree
    public String label;

    // Child nodes (sub-trees)
    public List<ASTNode> children = new ArrayList<>();

    public ASTNode(String label) {
        this.label = label;
    }

    // Convenience: add a child and return it (lets callers chain if needed)
    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
        }
    }

    // ─── Concrete Node Types ─────────────────────────────────────────────────

    /** Root node that wraps all top-level statements */
    public static class ProgramNode extends ASTNode {
        public ProgramNode() { super("Program"); }
    }

    /** Represents an if statement (without else) */
    public static class IfNode extends ASTNode {
        public IfNode() { super("If"); }
    }

    /** Represents an if-else statement */
    public static class IfElseNode extends ASTNode {
        public IfElseNode() { super("IfElse"); }
    }

    /** Represents a for loop */
    public static class ForNode extends ASTNode {
        public ForNode() { super("For"); }
    }

    /** Represents a { } block containing statements */
    public static class BlockNode extends ASTNode {
        public BlockNode() { super("Block"); }
    }

    /** Holds the condition expression of an if or for */
    public static class ConditionNode extends ASTNode {
        public ConditionNode(String text) { super("Condition: " + text); }
    }

    /** Holds the init clause of a for loop */
    public static class InitNode extends ASTNode {
        public InitNode(String text) { super("Init: " + text); }
    }

    /** Holds the update clause of a for loop */
    public static class UpdateNode extends ASTNode {
        public UpdateNode(String text) { super("Update: " + text); }
    }

    /** Marks the 'then' branch of an if/if-else */
    public static class ThenNode extends ASTNode {
        public ThenNode() { super("Then"); }
    }

    /** Marks the 'else' branch of an if-else */
    public static class ElseNode extends ASTNode {
        public ElseNode() { super("Else"); }
    }

    /** Represents a body of a for loop */
    public static class BodyNode extends ASTNode {
        public BodyNode() { super("Body"); }
    }

    /** Represents a simple statement (assignment or expression) */
    public static class SimpleStmtNode extends ASTNode {
        public SimpleStmtNode(String text) { super("Stmt: " + text); }
    }
}
