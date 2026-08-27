package me.moamenhredeen.jlox;

public class AST {

    public String prettyPrint(Expr expr) {
        return switch (expr) {
            case LiteralExpr literal -> literal.value().toString();
            case UnaryExpr unary -> String.format("(%s %s)",
                    unary.operator().lexeme(),
                    prettyPrint(unary.expr()));
            case BinaryExpr binary -> String.format("(%s %s %s)",
                    binary.operator().lexeme(),
                    prettyPrint(binary.left()),
                    prettyPrint(binary.right()));
            case GroupExpr group -> String.format("(%s)",
                    prettyPrint(group.expr()));
        };
    }
}
