package com.controlflow;

/**
 * ControlFlowASTVisitor — Walks the ANTLR parse tree and builds our AST.
 *
 * Extends the generated ControlFlowBaseVisitor<ASTNode>.
 * Each visitXxx method creates and returns one ASTNode.
 *
 * NOTE: The generated base class (ControlFlowBaseVisitor) is created by
 *       running `mvn generate-sources` — you will NOT see it until then.
 */
public class ControlFlowASTVisitor extends ControlFlowBaseVisitor<ASTNode> {

    // ─── Program ─────────────────────────────────────────────────────────────

    @Override
    public ASTNode visitProgram(ControlFlowParser.ProgramContext ctx) {
        ASTNode.ProgramNode program = new ASTNode.ProgramNode();

        // Visit every top-level statement and attach it to the program node
        for (ControlFlowParser.StatementContext stmt : ctx.statement()) {
            ASTNode child = visit(stmt);
            program.addChild(child);
        }

        return program;
    }

    // ─── Statements ──────────────────────────────────────────────────────────

    @Override
    public ASTNode visitStatement(ControlFlowParser.StatementContext ctx) {
        // Delegate to whichever sub-rule matched
        return visitChildren(ctx);
    }

    // ─── If (no else) ────────────────────────────────────────────────────────

    @Override
    public ASTNode visitIfOnly(ControlFlowParser.IfOnlyContext ctx) {
        ASTNode.IfNode ifNode = new ASTNode.IfNode();

        // Condition
        String condText = ctx.condition().getText();
        ifNode.addChild(new ASTNode.ConditionNode(condText));

        // Then branch
        ASTNode.ThenNode thenNode = new ASTNode.ThenNode();
        thenNode.addChild(visit(ctx.statement()));
        ifNode.addChild(thenNode);

        return ifNode;
    }

    // ─── If-Else ─────────────────────────────────────────────────────────────

    @Override
    public ASTNode visitIfElse(ControlFlowParser.IfElseContext ctx) {
        ASTNode.IfElseNode ifElseNode = new ASTNode.IfElseNode();

        // Condition
        String condText = ctx.condition().getText();
        ifElseNode.addChild(new ASTNode.ConditionNode(condText));

        // Then branch  (statement(0) = the 'then' body)
        ASTNode.ThenNode thenNode = new ASTNode.ThenNode();
        thenNode.addChild(visit(ctx.statement(0)));
        ifElseNode.addChild(thenNode);

        // Else branch  (statement(1) = the 'else' body)
        ASTNode.ElseNode elseNode = new ASTNode.ElseNode();
        elseNode.addChild(visit(ctx.statement(1)));
        ifElseNode.addChild(elseNode);

        return ifElseNode;
    }

    // ─── For Loop ────────────────────────────────────────────────────────────

    @Override
    public ASTNode visitForStatement(ControlFlowParser.ForStatementContext ctx) {
        ASTNode.ForNode forNode = new ASTNode.ForNode();

        // Init clause
        String initText = ctx.init().getText();
        forNode.addChild(new ASTNode.InitNode(initText.isEmpty() ? "(empty)" : initText));

        // Condition clause
        String condText = ctx.condition().getText();
        forNode.addChild(new ASTNode.ConditionNode(condText.isEmpty() ? "(empty)" : condText));

        // Update clause
        String updateText = ctx.update().getText();
        forNode.addChild(new ASTNode.UpdateNode(updateText.isEmpty() ? "(empty)" : updateText));

        // Loop body
        ASTNode.BodyNode bodyNode = new ASTNode.BodyNode();
        bodyNode.addChild(visit(ctx.statement()));
        forNode.addChild(bodyNode);

        return forNode;
    }

    // ─── Block ───────────────────────────────────────────────────────────────

    @Override
    public ASTNode visitBlock(ControlFlowParser.BlockContext ctx) {
        ASTNode.BlockNode blockNode = new ASTNode.BlockNode();

        for (ControlFlowParser.StatementContext stmt : ctx.statement()) {
            blockNode.addChild(visit(stmt));
        }

        return blockNode;
    }

    // ─── Simple Statement ────────────────────────────────────────────────────

    @Override
    public ASTNode visitSimpleStatement(ControlFlowParser.SimpleStatementContext ctx) {
        // Use the raw text of the statement as the label (strip trailing ';')
        String text = ctx.getText().replace(";", "");
        return new ASTNode.SimpleStmtNode(text);
    }
}
