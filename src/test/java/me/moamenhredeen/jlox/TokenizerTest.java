package me.moamenhredeen.jlox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {
    private Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
    }

    @Test
    void tokenizeEmptyScript() {
        var res = tokenizer.scan("");
        assertEquals(1, res.size());
        assertEquals(new Token("", 1, TokenType.EOF), res.get(0));
    }


    @Test
    void tokenizeTabWhitespaceAndNewLine() {
        var res = tokenizer.scan("""
                
                
                
                """);
        assertEquals(1, res.size());
        assertEquals(new Token("", 4, TokenType.EOF), res.get(0));
    }

    @Test
    void tokenizeSingleCharacterOperators() {
        var tokens = tokenizer.scan("() {} , ; + - * / .");
        assertEquals(List.of(
                new Token("(", 1, TokenType.LEFT_PAREN),
                new Token(")", 1, TokenType.RIGHT_PAREN),
                new Token("{", 1, TokenType.LEFT_BRACE),
                new Token("}", 1, TokenType.RIGHT_BRACE),
                new Token(",", 1, TokenType.COMMA),
                new Token(";", 1, TokenType.SEMICOLON),
                new Token("+", 1, TokenType.PLUS),
                new Token("-", 1, TokenType.MINUS),
                new Token("*", 1, TokenType.STAR),
                new Token("/", 1, TokenType.SLASH),
                new Token(".", 1, TokenType.DOT),
                new Token("", 1, TokenType.EOF)
        ), tokens);
    }

    @Test
    void tokenizeMultiCharacterOperators() {
        var tokens = tokenizer.scan("! != = == < <= > >=");
        assertEquals(List.of(
                new Token("!", 1, TokenType.BANG),
                new Token("!=", 1, TokenType.BANG_EQUAL),
                new Token("=", 1, TokenType.EQUAL),
                new Token("==", 1, TokenType.EQUAL_EQUAL),
                new Token("<", 1, TokenType.LESS),
                new Token("<=", 1, TokenType.LESS_EQUAL),
                new Token(">", 1, TokenType.GREATER),
                new Token(">=", 1, TokenType.GREATER_EQUAL),
                new Token("", 1, TokenType.EOF)
        ), tokens);
    }

    @Test
    void ignoreComments() {
        var tokens = tokenizer.scan("// comment");
        assertEquals(List.of(
                new Token("", 1, TokenType.EOF)
        ), tokens);


        tokens = tokenizer.scan("""
                // this will be ignored () {}
                // and this {} + - / *
                """);
        assertEquals(List.of(
                new Token("", 3, TokenType.EOF)
        ), tokens);
    }

    @Test
    void tokenizeStrings() {
        var tokens = tokenizer.scan("\"hello world\"");
        assertEquals(List.of(
                new Token("hello world", 1, TokenType.STRING),
                new Token("", 1, TokenType.EOF)
        ), tokens);
    }

    @Test
    void tokenizeNumbers() {
        var tokens = tokenizer.scan("1 0.5 999");
        assertEquals(List.of(
                new Token("1", 1, TokenType.NUMBER),
                new Token("0.5", 1, TokenType.NUMBER),
                new Token("999", 1, TokenType.NUMBER),
                new Token("", 1, TokenType.EOF)
        ), tokens);
    }


    @Test
    void tokenizeKeywordsAndIdentifiers() {
        var tokens = tokenizer.scan("and or true false while for if else fun return class this super print var variable_name_1");
        assertEquals(List.of(
                new Token("and", 1, TokenType.AND),
                new Token("or", 1, TokenType.OR),
                new Token("true", 1, TokenType.TRUE),
                new Token("false", 1, TokenType.FALSE),
                new Token("while", 1, TokenType.WHILE),
                new Token("for", 1, TokenType.FOR),
                new Token("if", 1, TokenType.IF),
                new Token("else", 1, TokenType.ELSE),
                new Token("fun", 1, TokenType.FUN),
                new Token("return", 1, TokenType.RETURN),
                new Token("class", 1, TokenType.CLASS),
                new Token("this", 1, TokenType.THIS),
                new Token("super", 1, TokenType.SUPER),
                new Token("print", 1, TokenType.PRINT),
                new Token("var", 1, TokenType.VAR),
                new Token("variable_name_1", 1, TokenType.IDENTIFIER),
                new Token("", 1, TokenType.EOF)
        ), tokens);
    }

    @Test
    void tokenizeSmallProgram(){
        var tokens = tokenizer.scan("""
                // this a small program
                var x = 10;
                print(x);
                """);
        assertEquals(List.of(
                new Token("var", 2, TokenType.VAR),
                new Token("x", 2, TokenType.IDENTIFIER),
                new Token("=", 2, TokenType.EQUAL),
                new Token("10", 2, TokenType.NUMBER),
                new Token(";", 2, TokenType.SEMICOLON),
                new Token("print", 3, TokenType.PRINT),
                new Token("(", 3, TokenType.LEFT_PAREN),
                new Token("x", 3, TokenType.IDENTIFIER),
                new Token(")", 3, TokenType.RIGHT_PAREN),
                new Token(";", 3, TokenType.SEMICOLON),
                new Token("", 4, TokenType.EOF)
        ), tokens);
    }

    @Test
    void tokenizeSmallAndMinifiedProgram(){
        var tokens = tokenizer.scan("""
                //this a small program
                var x=10;print(x);
                """);
        assertEquals(List.of(
                new Token("var", 2, TokenType.VAR),
                new Token("x", 2, TokenType.IDENTIFIER),
                new Token("=", 2, TokenType.EQUAL),
                new Token("10", 2, TokenType.NUMBER),
                new Token(";", 2, TokenType.SEMICOLON),
                new Token("print", 2, TokenType.PRINT),
                new Token("(", 2, TokenType.LEFT_PAREN),
                new Token("x", 2, TokenType.IDENTIFIER),
                new Token(")", 2, TokenType.RIGHT_PAREN),
                new Token(";", 2, TokenType.SEMICOLON),
                new Token("", 3, TokenType.EOF)
        ), tokens);
    }
}