package me.moamenhredeen.jlox;

public sealed interface Expr
        permits LiteralExpr, BinaryExpr, UnaryExpr, GroupExpr{
}
