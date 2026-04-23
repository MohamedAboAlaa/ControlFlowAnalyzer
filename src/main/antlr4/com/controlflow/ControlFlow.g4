grammar ControlFlow;

// IMPORTANT: This @header block makes ANTLR stamp every generated Java class
// (Lexer, Parser, Visitor) with "package com.controlflow;" so they are found
// by our hand-written Java files in the same package.
@header {
package com.controlflow;
}

// ─── Parser Rules ────────────────────────────────────────────────────────────

// Entry point: a program is a list of statements
program
    : statement* EOF
    ;

// A statement is one of: if, if-else, for, or a block
statement
    : ifStatement
    | forStatement
    | block
    | simpleStatement
    ;

// ANTLR resolves the "Dangling Else" automatically by greedy matching:
// the 'else' always binds to the nearest 'if' — no extra grammar needed.
ifStatement
    : IF LPAREN condition RPAREN statement                   # IfOnly
    | IF LPAREN condition RPAREN statement ELSE statement    # IfElse
    ;

forStatement
    : FOR LPAREN init SEMI condition SEMI update RPAREN statement
    ;

// A block is zero or more statements wrapped in braces
block
    : LBRACE statement* RBRACE
    ;

// Simple statement: an assignment or expression ending with semicolon
simpleStatement
    : IDENTIFIER ASSIGN expression SEMI
    | expression SEMI
    ;

// Condition: a boolean expression (e.g., x < 10, x == y)
condition
    : expression (RELOP expression)?
    ;

// For-loop initializer (e.g., int i = 0  OR  i = 0)
init
    : (INT_TYPE)? IDENTIFIER ASSIGN expression
    |   // empty init allowed
    ;

// For-loop update (e.g., i++, i--, i = i + 1)
update
    : IDENTIFIER INCOP
    | IDENTIFIER ASSIGN expression
    |   // empty update allowed
    ;

// Expression: handles arithmetic with left-to-right associativity
expression
    : expression (PLUS | MINUS | STAR | SLASH) expression   # BinaryExpr
    | IDENTIFIER                                              # IdentExpr
    | NUMBER                                                  # NumExpr
    ;

// ─── Lexer Rules ─────────────────────────────────────────────────────────────

// Keywords
IF       : 'if' ;
ELSE     : 'else' ;
FOR      : 'for' ;
INT_TYPE : 'int' ;

// Symbols
LPAREN  : '(' ;
RPAREN  : ')' ;
LBRACE  : '{' ;
RBRACE  : '}' ;
SEMI    : ';' ;
ASSIGN  : '=' ;
PLUS    : '+' ;
MINUS   : '-' ;
STAR    : '*' ;
SLASH   : '/' ;

// Relational operators (e.g., <, >, <=, >=, ==, !=)
RELOP   : '<' | '>' | '<=' | '>=' | '==' | '!=' ;

// Increment / Decrement (e.g., i++, i--)
INCOP   : '++' | '--' ;

// Identifiers and numbers
IDENTIFIER : [a-zA-Z_][a-zA-Z_0-9]* ;
NUMBER     : [0-9]+ ;

// Skip whitespace and comments
WS         : [ \t\r\n]+ -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;