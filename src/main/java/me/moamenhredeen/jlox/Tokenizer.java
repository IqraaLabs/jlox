package me.moamenhredeen.jlox;

import java.util.ArrayList;
import java.util.List;

/// responsible for converting a stream of character into a stream of tokens
public class Tokenizer {
    public List<Token> scan(String source) {
        List<Token> tokens = new ArrayList<>();
        int line = 1;
        int start = 0;
        int current = 0;

        while(current < source.length()){
            start = current;
            var token = switch (source.charAt(current)) {
                case '(' -> new Token("(", line, TokenType.LEFT_PAREN);
                case ')' -> new Token(")", line, TokenType.RIGHT_PAREN);
                case '{' -> new Token("{", line, TokenType.LEFT_BRACE);
                case '}' -> new Token("}", line, TokenType.RIGHT_BRACE);
                case ',' -> new Token(",", line, TokenType.COMMA);
                case ';' -> new Token(";", line, TokenType.SEMICOLON);
                case '-' -> new Token("-", line, TokenType.MINUS);
                case '+' -> new Token("+", line, TokenType.PLUS);
                case '*' -> new Token("*", line, TokenType.STAR);
                case '.' -> new Token(".", line, TokenType.DOT);
                case '!'  -> {
                    if (current + 1 < source.length() && source.charAt(current + 1) == '=') {
                        current++;
                        yield  new Token("!=", line, TokenType.BANG_EQUAL);
                    }else {
                        yield  new Token("!", line, TokenType.BANG);
                    }
                }
                case '=' -> {
                    if (current + 1 < source.length() && source.charAt(current + 1) == '=') {
                        current++;
                        yield  new Token("==", line, TokenType.EQUAL_EQUAL);
                    } else {
                        yield  new Token("=", line, TokenType.EQUAL);
                    }
                }
                case '<' -> {
                    if (current + 1 < source.length() && source.charAt(current + 1) == '=') {
                        current++;
                        yield  new Token("<=", line, TokenType.LESS_EQUAL);
                    } else {
                        yield  new Token("<", line, TokenType.LESS);
                    }
                }
                case '>' -> {
                    if (current + 1 < source.length() && source.charAt(current + 1) == '=') {
                        current++;
                        yield  new Token(">=", line, TokenType.GREATER_EQUAL);
                    } else {
                        yield  new Token(">", line, TokenType.GREATER);
                    }
                }
                case '/' -> {
                    if (current + 1 < source.length() && source.charAt(current + 1) == '/') {
                        do {
                            current++;
                        } while (current + 1 < source.length() && source.charAt(current + 1) != '\n');
                        yield  null;
                    } else {
                        yield  new Token("/", line, TokenType.SLASH);
                    }
                }
                case '"' -> {
                    while (true) {
                        if (current + 1 >= source.length()) yield null;
                        if (source.charAt(current + 1) == '\n') {
                            yield null;
                        }
                        if (source.charAt(current + 1) == '"') {
                            current++;
                            // trim the surrounding quotes
                            yield new Token(source.substring(start + 1, current), line, TokenType.STRING);
                        }
                        current++;
                    }
                }
                case char i when i >= '0' && i <= '9' -> {
                    while (current + 1 < source.length() && source.charAt(current + 1) >= '0' && source.charAt(current + 1) <= '9' ) {
                        current++;
                    }
                    if (current + 1 < source.length() && source.charAt(current + 1) == '.') {
                        do{
                            current++;
                        } while (current + 1 < source.length() && source.charAt(current + 1) >= '0' && source.charAt(current + 1) <= '9' );
                    }
                    yield new Token(source.substring(start, current + 1), line, TokenType.NUMBER);
                }
                case char i when (i >= 'a' && i <= 'z') || (i >= 'A' && i <= 'Z') || i == '_' -> {
                    while (current + 1 < source.length() &&
                            ((source.charAt(current + 1) >= 'a' && source.charAt(current + 1) <= 'z')
                            || (source.charAt(current + 1) >= 'A' && source.charAt(current + 1) <= 'Z')
                            || (source.charAt(current + 1) >= '0' && source.charAt(current + 1) <= '9')
                            || source.charAt(current + 1) == '_')) {
                        current++;
                    }

                    var lexeme = source.substring(start, current + 1);
                    yield switch (lexeme) {
                        case "and" -> new Token(lexeme, line, TokenType.AND);
                        case "or" -> new Token(lexeme, line, TokenType.OR);
                        case "true" -> new Token(lexeme, line, TokenType.TRUE);
                        case "false" -> new Token(lexeme, line, TokenType.FALSE);
                        case "while" -> new Token(lexeme, line, TokenType.WHILE);
                        case "for" -> new Token(lexeme, line, TokenType.FOR);
                        case "if" -> new Token(lexeme, line, TokenType.IF);
                        case "else" -> new Token(lexeme, line, TokenType.ELSE);
                        case "var" -> new Token(lexeme, line, TokenType.VAR);
                        case "fun" -> new Token(lexeme, line, TokenType.FUN);
                        case "return" -> new Token(lexeme, line, TokenType.RETURN);
                        case "class" -> new Token(lexeme, line, TokenType.CLASS);
                        case "this" -> new Token(lexeme, line, TokenType.THIS);
                        case "super" -> new Token(lexeme, line, TokenType.SUPER);
                        case "print" -> new Token(lexeme, line, TokenType.PRINT);
                        default -> new Token(lexeme, line, TokenType.IDENTIFIER);
                    };
                }
                case ' ', '\t', '\r' -> null;
                case '\n' -> {
                    line++;
                    yield null;
                }
                default -> new Token("", line, TokenType.UNKNOWN);
            };

            if (token != null) {
                tokens.add(token);
            }
            current++;
        }

        tokens.add(new Token("", line, TokenType.EOF));
        return tokens;
    }
}
