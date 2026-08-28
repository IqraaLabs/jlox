package me.moamenhredeen.jlox;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

final class Repl {

    private static final String PREFIX = ":";
    private static final String PROMPT = "> ";
    private static final String HISTORY_FILE = ".jlox_history";

    private static final Logger log = Logger.getLogger("jlox");

    private record Meta(String name, List<String> aliases, String argument,
                        String help, Consumer<String> action) {

        String render() {
            return PREFIX + name + (argument.isEmpty() ? "" : " <" + argument + ">");
        }
    }

    private final List<Meta> commands;
    private final Map<String, Meta> byName = new LinkedHashMap<>();

    private Terminal terminal;
    private boolean running = true;

    Repl() {
        commands = List.of(
                new Meta("help", List.of("?"), "", "list these commands", argument -> help()),
                new Meta("exit", List.of("quit"), "", "leave the session", argument -> running = false),
                new Meta("tokens", List.of(), "source", "print the token stream of a line", this::tokens),
                new Meta("ast", List.of(), "source", "print the syntax tree of a line", this::ast),
                new Meta("load", List.of(), "file", "read a script into the session", this::load));

        for (var command : commands) {
            byName.put(command.name(), command);
            for (var alias : command.aliases()) {
                byName.put(alias, command);
            }
        }
    }

    int run() {
        try (var term = TerminalBuilder.builder()
                .name("jlox")
                .system(true)
                .dumb(true)
                .build()) {

            terminal = term;
            log.fine(() -> "terminal type " + term.getType());

            var reader = LineReaderBuilder.builder()
                    .terminal(term)
                    .appName("jlox")
                    .completer(new ArgumentCompleter(
                            new StringsCompleter(byName.keySet().stream().map(PREFIX::concat).toList()),
                            NullCompleter.INSTANCE))
                    .variable(LineReader.HISTORY_FILE, Path.of(System.getProperty("user.home"), HISTORY_FILE))
                    .build();

            var out = term.writer();
            out.println("jlox " + JLox.VERSION + " (:help for repl commands, ctrl-d to exit)");
            out.flush();

            while (running) {
                String line;
                try {
                    line = reader.readLine(PROMPT);
                } catch (UserInterruptException e) {
                    continue;
                } catch (EndOfFileException e) {
                    break;
                }

                if (line.isBlank()) {
                    continue;
                }

                var input = line.strip();
                if (input.startsWith(PREFIX)) {
                    meta(input);
                } else {
                    JLox.evaluate(input);
                }
            }

            return JLox.OK;
        } catch (IOException e) {
            System.err.println("jlox: " + e.getMessage());
            return JLox.NO_INPUT;
        }
    }

    private void meta(String input) {
        var space = input.indexOf(' ');
        var name = (space < 0 ? input : input.substring(0, space)).substring(PREFIX.length());
        var argument = space < 0 ? "" : input.substring(space + 1).strip();

        var command = byName.get(name);
        if (command == null) {
            print("unknown command " + PREFIX + name + ", try " + PREFIX + "help");
            return;
        }
        if (!command.argument().isEmpty() && argument.isEmpty()) {
            print(command.render() + " needs an argument");
            return;
        }

        command.action().accept(argument);
    }

    private void help() {
        var width = commands.stream().mapToInt(command -> command.render().length()).max().orElse(1);
        var out = terminal.writer();
        for (var command : commands) {
            var aliases = command.aliases().isEmpty()
                    ? ""
                    : "  (also " + PREFIX + String.join(", " + PREFIX, command.aliases()) + ")";
            out.printf("  %-" + width + "s  %s%s%n", command.render(), command.help(), aliases);
        }
        out.flush();
    }

    private void tokens(String source) {
        var out = terminal.writer();
        new Tokenizer().scan(source).forEach(out::println);
        out.flush();
    }

    private void ast(String source) {
        var expr = new Parser().parse(new Tokenizer().scan(source));
        print(expr.isEmpty()
                ? "could not parse the input"
                : new AST().prettyPrint(expr.get()));
    }

    private void load(String file) {
        try {
            JLox.evaluate(Files.readString(Path.of(file)));
        } catch (NoSuchFileException e) {
            print("no such file: " + file);
        } catch (IOException e) {
            print("cannot read " + file + ": " + e.getMessage());
        }
    }

    private void print(String message) {
        PrintWriter out = terminal.writer();
        out.println(message);
        out.flush();
    }
}
