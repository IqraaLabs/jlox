package me.moamenhredeen.jlox;

public record Token(String lexeme, int line, TokenType type) {
}
