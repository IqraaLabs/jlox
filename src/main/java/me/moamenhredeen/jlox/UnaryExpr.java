package me.moamenhredeen.jlox;

public record UnaryExpr(Token operator, Expr expr) implements Expr {
}
