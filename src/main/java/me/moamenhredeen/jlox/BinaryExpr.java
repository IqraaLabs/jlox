package me.moamenhredeen.jlox;

public record BinaryExpr(Token operator, Expr left, Expr right)  implements Expr {
}
