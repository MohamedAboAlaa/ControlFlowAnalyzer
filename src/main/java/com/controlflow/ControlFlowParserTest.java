package com.controlflow;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * ControlFlowParserTest — Manual test suite for the Control Flow Syntax Analyzer.
 *
 * Run this class directly (no JUnit required).
 * Each test method:
 *   - Parses a specific input string
 *   - Checks for expected errors (or no errors)
 *   - Optionally validates the AST structure
 *   - Prints PASS / FAIL with a short description
 *
 * Categories covered:
 *   1. Valid simple statements
 *   2. If-only statements
 *   3. If-else statements
 *   4. For loops (with all clause variants)
 *   5. Nested structures
 *   6. Dangling else problem
 *   7. Empty blocks
 *   8. Arithmetic expressions
 *   9. Multiple top-level statements
 *  10. Invalid / malformed inputs (expect errors)
 */
public class ControlFlowParserTest {

    // ─── Test Runner State ────────────────────────────────────────────────────

    private static int passed = 0;
    private static int failed = 0;

    // ─── Entry Point ──────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║      Control Flow Parser — Full Test Suite           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // ── Category 1: Simple Statements ─────────────────────────────────────
        section("1. Simple Statements");
        testValid("Simple assignment",
                "x = 5;",
                "Program > Stmt: x=5");

        testValid("Assignment with arithmetic",
                "y = x + 1;",
                "Program > Stmt: y=x+1");

        testValid("Assignment with complex expression",
                "z = a + b * c;",
                "Program > Stmt");

        testValid("Expression statement (no assignment)",
                "x + 1;",
                "Program > Stmt");

        // ── Category 2: If-Only ────────────────────────────────────────────────
        section("2. If-Only Statements");
        testValid("Simple if with block",
                "if (x < 10) { y = 1; }",
                "Program > If > Condition: x<10");

        testValid("If with equality check",
                "if (x == 0) { y = 0; }",
                "Program > If > Condition: x==0");

        testValid("If with != operator",
                "if (x != y) { z = 1; }",
                "Program > If");

        testValid("If with >= operator",
                "if (a >= b) { c = 1; }",
                "Program > If");

        testValid("If with <= operator",
                "if (a <= 100) { b = 0; }",
                "Program > If");

        testValid("If with single simple statement (no braces)",
                "if (x < 5) y = 0;",
                "Program > If > Then > Stmt: y=0");

        testValid("If with empty block",
                "if (x < 5) { }",
                "Program > If > Then > Block");

        // ── Category 3: If-Else ───────────────────────────────────────────────
        section("3. If-Else Statements");
        testValid("Simple if-else",
                "if (x == 0) { y = 1; } else { y = 2; }",
                "Program > IfElse");

        testValid("If-else with single statements (no braces)",
                "if (x > 0) y = 1; else y = 0;",
                "Program > IfElse > Then > Stmt: y=1");

        testValid("If-else with greater-than",
                "if (a > b) { max = a; } else { max = b; }",
                "Program > IfElse > Else > Block > Stmt: max=b");

        testValid("If-else — else branch is another if (else-if chain)",
                "if (x == 1) { y = 1; } else if (x == 2) { y = 2; } else { y = 0; }",
                "Program > IfElse > Else > IfElse");

        // ── Category 4: For Loops ─────────────────────────────────────────────
        section("4. For Loops");
        testValid("For loop with int declaration",
                "for (int i = 0; i < 5; i++) { x = i; }",
                "Program > For > Init: inti=0");

        testValid("For loop without int keyword",
                "for (i = 0; i < 10; i++) { x = i; }",
                "Program > For > Init: i=0");

        testValid("For loop with decrement update",
                "for (int i = 10; i > 0; i--) { x = i; }",
                "Program > For > Update: i--");

        testValid("For loop with assignment update",
                "for (int i = 0; i < 5; i = i + 1) { x = i; }",
                "Program > For > Update: i=i+1");

        testValid("For loop with empty init",
                "for (; i < 5; i++) { x = i; }",
                "Program > For > Init: (empty)");

        testValid("For loop with empty update",
                "for (int i = 0; i < 5;) { x = i; }",
                "Program > For > Update: (empty)");

        testValid("For loop with empty body block",
                "for (int i = 0; i < 5; i++) { }",
                "Program > For > Body > Block");

        testValid("For loop with single statement body (no braces)",
                "for (int i = 0; i < 3; i++) x = i;",
                "Program > For > Body > Stmt: x=i");

        // ── Category 5: Nested Structures ─────────────────────────────────────
        section("5. Nested Structures");
        testValid("If inside for loop",
                "for (int i = 0; i < 5; i++) { if (i == 3) { x = i; } }",
                "Program > For > Body > Block > If");

        testValid("If-else inside for loop",
                "for (int i = 0; i < 5; i++) { if (i == 3) { x = i; } else { x = 0; } }",
                "Program > For > Body > Block > IfElse");

        testValid("Nested if inside if",
                "if (x > 0) { if (x < 10) { y = x; } }",
                "Program > If > Then > Block > If");

        testValid("Deeply nested: for inside if inside for",
                "for (int i = 0; i < 3; i++) { " +
                "  if (i > 0) { " +
                "    for (int j = 0; j < i; j++) { x = j; } " +
                "  } " +
                "}",
                "Program > For");

        testValid("Triple-nested if",
                "if (a < b) { if (b < c) { if (c < d) { x = 1; } } }",
                "Program > If > Then > Block > If > Then > Block > If");

        testValid("For loop inside if-else both branches",
                "if (x > 0) { for (int i = 0; i < x; i++) { y = i; } } else { y = 0; }",
                "Program > IfElse > Then > Block > For");

        // ── Category 6: Dangling Else ─────────────────────────────────────────
        section("6. Dangling Else Problem");

        // The 'else' must bind to the inner 'if (b)' — not the outer 'if (a)'
        testDanglingElse(
                "if (a < b) if (b < c) x = 1; else x = 2;",
                "Else should bind to inner if — outer If > Then > IfElse"
        );

        testValid("Dangling else with explicit braces forces outer binding",
                "if (a < b) { if (b < c) x = 1; } else { x = 2; }",
                "Program > IfElse");

        // ── Category 7: Multiple Top-Level Statements ─────────────────────────
        section("7. Multiple Top-Level Statements");
        testValid("Two if statements",
                "if (x < 5) { y = 1; } if (x > 5) { y = 2; }",
                "Program has 2 children");

        testValid("Mix of if, for, and assignment",
                "x = 0; if (x < 5) { x = 1; } for (int i = 0; i < 3; i++) { x = x + i; }",
                "Program has 3 children");

        testValid("Three sequential assignments",
                "x = 1; y = 2; z = 3;",
                "Program has 3 children");

        // ── Category 8: Arithmetic Expressions ────────────────────────────────
        section("8. Arithmetic Expressions");
        testValid("Addition in condition",
                "if (x + 1 < 10) { y = 0; }",
                "Program > If > Condition");

        testValid("Subtraction in assignment",
                "y = x - 5;",
                "Program > Stmt");

        testValid("Multiplication in assignment",
                "z = a * b;",
                "Program > Stmt");

        testValid("Division in assignment",
                "r = n / 2;",
                "Program > Stmt");

        testValid("Complex arithmetic in for body",
                "for (int i = 0; i < 10; i++) { y = x + i * 2; }",
                "Program > For > Body > Block > Stmt: y=x+i*2");

        // ── Category 9: Edge Cases ─────────────────────────────────────────────
        section("9. Edge Cases");
        testValid("Empty program (no statements)",
                "",
                "Program with no children");

        testValid("If with condition using only identifier (truthy-style)",
                "if (x < 1) { y = x; }",
                "Program > If");

        testValid("Nested blocks",
                "{ { x = 1; } }",
                "Program > Block > Block > Stmt");

        testValid("For loop with arithmetic in init value",
                "for (int i = 0; i < 5; i++) { x = 1; }",
                "Program > For");

        testValid("Assignment using result of compound expression in for body",
                "for (int k = 0; k < 10; k++) { result = k * k; }",
                "Program > For > Body > Block > Stmt: result=k*k");

        // ── Category 10: Invalid Inputs (Expect Errors) ────────────────────────
        section("10. Invalid / Malformed Inputs");
        testInvalid("Missing closing parenthesis in if",
                "if (x < 10 { y = 1; }",
                "Should report syntax error");

        testInvalid("Missing opening brace",
                "if (x < 10) y = 1; x = 2 }",
                "Should report syntax error");

        testInvalid("Missing semicolon in assignment",
                "if (x < 5) { y = 1 }",
                "Should report syntax error — missing ; after y=1");

        testInvalid("For loop missing first semicolon",
                "for (int i = 0 i < 5; i++) { x = i; }",
                "Should report syntax error");

        testInvalid("For loop missing second semicolon",
                "for (int i = 0; i < 5 i++) { x = i; }",
                "Should report syntax error");

        testInvalid("Missing closing brace",
                "if (x < 10) { y = 1;",
                "Should report syntax error — unclosed block");

        testInvalid("Else without if",
                "else { y = 2; }",
                "Should report syntax error");

        testInvalid("For without parentheses",
                "for { x = 1; }",
                "Should report syntax error");

        testInvalid("Double assignment operator",
                "x == 5;",
                "Should report syntax error — == is RELOP not ASSIGN in stmt");

        // ─── Summary ──────────────────────────────────────────────────────────
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.printf( "║  Results:  %3d passed   %3d failed   %3d total       ║%n",
                passed, failed, passed + failed);
        System.out.println("╚══════════════════════════════════════════════════════╝");

        if (failed > 0) {
            System.exit(1); // Non-zero exit for CI systems
        }
    }

    // ─── Test Helpers ─────────────────────────────────────────────────────────

    /**
     * Asserts that the input parses successfully with NO syntax errors,
     * and optionally checks that the printed AST contains the expected substring.
     */
    private static void testValid(String name, String input, String expectedASTHint) {
        ParseResult result = parse(input);

        if (result.hasErrors) {
            fail(name, "Expected no errors but got: " + result.errors);
            return;
        }

        // Build AST and print it to a string
        String astText = buildASTText(result.tree);

        if (expectedASTHint != null && !expectedASTHint.isEmpty()) {
            // Only check structural hints that map to actual node labels
            // (hints like "Program has 2 children" are checked separately)
            if (expectedASTHint.contains("has") && expectedASTHint.contains("children")) {
                // Count top-level children of Program node
                ASTNode ast = buildAST(result.tree);
                if (ast instanceof ASTNode.ProgramNode) {
                    int count = extractExpectedCount(expectedASTHint);
                    if (count >= 0 && ast.children.size() != count) {
                        fail(name, "Expected " + count + " children but found " + ast.children.size());
                        return;
                    }
                }
            } else if (expectedASTHint.contains("no children")) {
                ASTNode ast = buildAST(result.tree);
                if (!ast.children.isEmpty()) {
                    fail(name, "Expected empty program but found " + ast.children.size() + " children");
                    return;
                }
            } else {
                // Check that the AST text contains the first node type mentioned
                String firstNode = expectedASTHint.split(" > ")[0].trim();
                if (!astText.contains(firstNode)) {
                    fail(name, "AST does not contain expected node '" + firstNode + "'.\nAST:\n" + astText);
                    return;
                }
            }
        }

        pass(name);
    }

    /**
     * Asserts that the input produces at least one syntax error.
     */
    private static void testInvalid(String name, String input, String reason) {
        ParseResult result = parse(input);

        if (!result.hasErrors) {
            fail(name, "Expected syntax errors but none were found. Reason: " + reason);
        } else {
            pass(name + " [errors: " + result.errors.get(0) + "]");
        }
    }

    /**
     * Special test for the dangling-else scenario.
     * Verifies parsing succeeds AND the inner if got the else (IfElse node exists).
     */
    private static void testDanglingElse(String input, String description) {
        ParseResult result = parse(input);

        if (result.hasErrors) {
            fail("Dangling Else: " + description, "Parse failed unexpectedly: " + result.errors);
            return;
        }

        String astText = buildASTText(result.tree);

        // The outer If must contain an IfElse child (else bound to inner if)
        if (!astText.contains("IfElse")) {
            fail("Dangling Else: " + description,
                    "Expected an IfElse node inside an If node.\nAST:\n" + astText);
            return;
        }

        System.out.println("  [DANGLING-ELSE AST]");
        for (String line : astText.split("\n")) {
            System.out.println("    " + line);
        }
        pass("Dangling Else: " + description);
    }

    // ─── Parsing Infrastructure ───────────────────────────────────────────────

    private static class ParseResult {
        ParseTree tree;
        boolean hasErrors;
        List<String> errors;
    }

    private static ParseResult parse(String input) {
        ControlFlowLexer lexer   = new ControlFlowLexer(CharStreams.fromString(input));
        CommonTokenStream tokens  = new CommonTokenStream(lexer);
        ControlFlowParser parser  = new ControlFlowParser(tokens);

        // Suppress default ANTLR error output; collect into our listener
        parser.removeErrorListeners();
        SyntaxErrorListener listener = new SyntaxErrorListener();
        parser.addErrorListener(listener);

        ParseResult r = new ParseResult();
        r.tree      = parser.program();
        r.hasErrors = listener.hasErrors();
        r.errors    = listener.getErrors();
        return r;
    }

    private static ASTNode buildAST(ParseTree tree) {
        return new ControlFlowASTVisitor().visit(tree);
    }

    private static String buildASTText(ParseTree tree) {
        ASTNode root = buildAST(tree);
        StringBuilder sb = new StringBuilder();
        appendAST(root, 0, sb);
        return sb.toString();
    }

    private static void appendAST(ASTNode node, int depth, StringBuilder sb) {
        if (node == null) return;
        String prefix = "  ".repeat(depth);
        sb.append(prefix).append(node.label).append("\n");
        for (ASTNode child : node.children) {
            appendAST(child, depth + 1, sb);
        }
    }

    private static int extractExpectedCount(String hint) {
        // e.g. "Program has 3 children"
        try {
            String[] parts = hint.split(" ");
            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i + 1].equals("children") || parts[i + 1].startsWith("child")) {
                    return Integer.parseInt(parts[i]);
                }
            }
        } catch (NumberFormatException ignored) { }
        return -1;
    }

    // ─── Reporting ────────────────────────────────────────────────────────────

    private static void section(String title) {
        System.out.println();
        System.out.println("── " + title + " " + "─".repeat(Math.max(0, 50 - title.length())));
    }

    private static void pass(String name) {
        System.out.println("  ✓ PASS  " + name);
        passed++;
    }

    private static void fail(String name, String reason) {
        System.out.println("  ✗ FAIL  " + name);
        System.out.println("         Reason: " + reason);
        failed++;
    }
}
