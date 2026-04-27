package com.controlflow;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.IOException;

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

        try {

            String input = "x = 10;";

            System.out.println("═══════════════════════════════════════");
            System.out.println("  Control Flow Syntax Analyzer & Parser");
            System.out.println("═══════════════════════════════════════");
            System.out.println();
//            System.out.println("── Input Code ──────────────────────────");
//            System.out.println(formatInput(input));
//            System.out.println();

            // ── 2. Lexer & Parser ────────────────────────────────────────────────

            // Input from code
            //        ControlFlowLexer  lexer  = new ControlFlowLexer(CharStreams.fromString(input));
            // Input from file
            ControlFlowLexer lexer = new ControlFlowLexer(CharStreams.fromFileName("input.txt"));
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

            StringBuilder dotBuilder = new StringBuilder();
            dotBuilder.append("digraph AST {\n");
            dotBuilder.append("  node [fontname=\"Arial\"];\n");
            generateDOT(ast, dotBuilder, new int[]{0});
            dotBuilder.append("}\n");

            try (java.io.PrintWriter out = new java.io.PrintWriter("ast.dot")) {
                out.println(dotBuilder.toString());
                System.out.println("✓ AST DOT file generated: ast.dot");
            } catch (IOException e) {
                System.err.println("Error writing DOT file: " + e.getMessage());
            }

            try {
                ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "ast.dot", "-o", "ast.png");
                Process process = pb.start();

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("✓ Success! Image generated: ast.png");

                    // new ProcessBuilder("xdg-open", "ast.png").start();
                } else {
                    System.err.println("✗ Error: Graphviz failed with exit code " + exitCode);
                }
            } catch (Exception e) {
                System.err.println("✗ Could not generate image. Make sure 'graphviz' is installed.");
                System.err.println("  Hint: sudo apt install graphviz");
            }

        } catch (IOException e) {
            System.err.println("Error: Could not read file. " + e.getMessage());
        }
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

    private static void generateDOT(ASTNode node, StringBuilder sb, int[] nodeCounter) {
        if (node == null) return;

        int currentId = nodeCounter[0]++;
        String cleanLabel = node.label.replace("\"", "\\\"").replace(":", " ");

        sb.append(String.format("  node%d [label=\"%s\"];\n", currentId, cleanLabel));

        for (ASTNode child : node.children) {
            int childId = nodeCounter[0];
            sb.append(String.format("  node%d -> node%d;\n", currentId, childId));
            generateDOT(child, sb, nodeCounter);
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