package com.controlflow;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Main — Entry point for the Control Flow Syntax Analyzer.
 *
 * Steps:
 *   1. Define a test input string.
 *   2. Run the ANTLR Lexer → Parser to get a Parse Tree.
 *   3. Walk the Parse Tree with our Visitor to build an AST.
 *   4. Print the AST using simple text indentation.
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Test Input ────────────────────────────────────────────────────
        //
        // This sample exercises: if, if-else, for, nested structures, and blocks.
        // Change this string to test different programs.

        String input =
                "if (x < 10) { y = x + 1; } " +
                        "if (x == 0) { y = 1; } else { y = 2; } " +
                        "for (int i = 0; i < 5; i++) { " +
                        "    if (i == 3) { x = i; } else { x = 0; } " +
                        "}";

        System.out.println("═══════════════════════════════════════");
        System.out.println("  Control Flow Syntax Analyzer & Parser");
        System.out.println("═══════════════════════════════════════");
        System.out.println();
        System.out.println("── Input Code ──────────────────────────");
        System.out.println(formatInput(input));
        System.out.println();

        // ── 2. Lexer & Parser ────────────────────────────────────────────────

        ControlFlowLexer  lexer  = new ControlFlowLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ControlFlowParser parser = new ControlFlowParser(tokens);

        // Capture syntax errors — ANTLR prints them to stderr by default
        parser.removeErrorListeners();
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.addErrorListener(errorListener);

        ParseTree parseTree = parser.program();

        // Report errors if any
        if (errorListener.hasErrors()) {
            System.out.println("── Syntax Errors ───────────────────────");
            for (String error : errorListener.getErrors()) {
                System.out.println("  ERROR: " + error);
            }
            System.out.println();
            System.out.println("Parsing failed. Fix the errors above and try again.");
            return;
        }

        System.out.println("✓ Syntax validation passed — no errors found.");
        System.out.println();

        // ── 3. Build AST via Visitor ─────────────────────────────────────────

        ControlFlowASTVisitor visitor = new ControlFlowASTVisitor();
        ASTNode ast = visitor.visit(parseTree);

        // ── 4. Print AST ─────────────────────────────────────────────────────

        System.out.println("── Abstract Syntax Tree (AST) ──────────");
        printAST(ast, 0);
        System.out.println();
        System.out.println("═══════════════════════════════════════");
    }

    // ─── AST Printer ─────────────────────────────────────────────────────────

    /**
     * Recursively prints the AST with indentation.
     * Each level adds two dashes so depth is immediately visible.
     *
     * Example output:
     *   - Program
     *   -- If
     *   ---- Condition: x<10
     *   ---- Then
     *   ------ Block
     *   -------- Stmt: y=x+1
     */
    private static void printAST(ASTNode node, int depth) {
        if (node == null) return;

        // Build the indent prefix: 2 dashes per depth level
        String prefix = "--".repeat(depth);
        System.out.println(prefix + (depth == 0 ? "" : " ") + node.label);

        // Recursively print each child
        for (ASTNode child : node.children) {
            printAST(child, depth + 1);
        }
    }

    // ─── Input Formatter ─────────────────────────────────────────────────────

    /**
     * Pretty-prints the compacted input string for display.
     * Adds a newline before each statement keyword for readability.
     */
    private static String formatInput(String input) {
        return input
                .replace("} if",   "}\nif")
                .replace("} for",  "}\nfor")
                .replace("; }",    ";\n}")
                .replace("{ ",     "{\n  ")
                .replace("; i",    ";\n  i");
    }
}