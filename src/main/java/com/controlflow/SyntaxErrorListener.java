package com.controlflow;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

/**
 * SyntaxErrorListener — Collects ANTLR parse errors into a simple list.
 *
 * Attach this to the parser instead of the default console listener
 * so we can display errors in a clean, controlled way from Main.java.
 */
public class SyntaxErrorListener extends BaseErrorListener {

    private final List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        // Format: "line 3:7 — mismatched input '}' expecting ';'"
        errors.add("line " + line + ":" + charPositionInLine + " — " + msg);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }
}
