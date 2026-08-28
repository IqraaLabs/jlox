package me.moamenhredeen.jlox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        this.parser = new Parser();
    }

    @Test
    void parseEmptyListOfTokens() {
        var tokens = new ArrayList<Token>();
        var expr = parser.parse(tokens);
        assertTrue(expr.isEmpty());
    }


    @Test
    void parseLiteralValue() {
        var tokens = List.of(new Token("2", 1, TokenType.NUMBER));
        var expr = parser.parse(tokens);
        assertTrue(expr.isPresent());
        assertEquals(new LiteralExpr(2), expr.get());
    }
}