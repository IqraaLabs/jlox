package me.moamenhredeen.jlox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ASTTest {

    private AST ast;

    @BeforeEach
    void setUp() {
        this.ast = new AST();
    }


    @Test
    void printLiteralExpr() {
        var tree = new LiteralExpr(1);
        var s = ast.prettyPrint(tree);
        assertEquals("1", s);
    }



    @Test
    void printUnaryExpr() {
        var tree = new UnaryExpr(
                new Token("+", 1, TokenType.PLUS),
                new LiteralExpr(1)
        );
        var s = ast.prettyPrint(tree);
        assertEquals("(+ 1)", s);
    }

    @Test
    void printBinaryExpr() {
        var tree = new BinaryExpr(
                new Token("+", 1, TokenType.PLUS),
                new LiteralExpr(1),
                new LiteralExpr(2)
        );
        var s = ast.prettyPrint(tree);
        assertEquals("(+ 1 2)", s);
    }


    @Test
    void printGroupExpr() {
        var tree = new GroupExpr(
                new LiteralExpr(1)
        );
        var s = ast.prettyPrint(tree);
        assertEquals("(1)", s);
    }


    @Test
    void printMoreComplexAST() {
        var tree = new BinaryExpr(
                new Token("*", 1, TokenType.STAR),
                new LiteralExpr(1),
                new GroupExpr(
                        new BinaryExpr(
                                new Token("+", 1, TokenType.PLUS),
                                new LiteralExpr(1),
                                new LiteralExpr(2)
                        )
                )
        );
        var s = ast.prettyPrint(tree);
        assertEquals("(* 1 ((+ 1 2)))", s);
    }

}