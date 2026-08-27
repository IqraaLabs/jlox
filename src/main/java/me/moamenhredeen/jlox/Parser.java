package me.moamenhredeen.jlox;

import java.util.List;
import java.util.Optional;


/// parser really has two jobs
/// - Given a valid sequence of tokens, produce a corresponding syntax tree.
/// - Given an invalid sequence of tokens, detect any errors and tell the user about their mistakes.
///
/// # Grammar
/// ```
/// expression     → equality ;
/// equality       → comparison ( ( "!=" | "==" ) comparison )* ;
/// comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
/// term           → factor ( ( "-" | "+" ) factor )* ;
/// factor         → unary ( ( "/" | "*" ) unary )* ;
/// unary          → ( "!" | "-" ) unary
///                | primary ;
/// primary        → NUMBER | STRING | "true" | "false" | "nil"
///                | "(" expression ")" ;
/// ```
public class Parser {
    private List<Token> tokens;
    private int current = 0;

    public Optional<Expr> parse(List<Token> tokens) {
        this.tokens = tokens;

        while (current < tokens.size()) {
            var token = tokens.get(current);

            // this switch expression is the code translation of the rules
            switch (token.type()) {

                // equality rule
                case BANG_EQUAL, EQUAL_EQUAL -> {

                }
            }
            current++;
        }

        if (tokens.isEmpty()) return Optional.empty();
        return Optional.empty();
    }

    private void parse(){

    }
}
