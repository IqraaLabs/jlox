package me.moamenhredeen.jlox;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.PositionalParamSpec;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.ScopeType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.function.ToIntFunction;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JLox {

    public static final String VERSION = "1.0-SNAPSHOT";

    static final int OK = 0;
    private static final int USAGE_ERROR = 64;
    private static final int DATA_ERROR = 65;
    static final int NO_INPUT = 66;
    private static final int INTERNAL_ERROR = 70;

    private static final Logger log = Logger.getLogger("jlox");

    private JLox() {
    }

    public static CommandLine commandLine() {
        var root = command("jlox", "the lox programming language, implemented in java");
        root.version("jlox " + VERSION);

        root.addOption(OptionSpec.builder("-v", "--verbose")
                .type(boolean.class)
                .scopeType(ScopeType.INHERIT)
                .description("print diagnostics to stderr")
                .build());
        root.addOption(OptionSpec.builder("-h", "--help")
                .usageHelp(true)
                .scopeType(ScopeType.INHERIT)
                .description("show this message and exit")
                .build());
        root.addOption(OptionSpec.builder("-V", "--version")
                .versionHelp(true)
                .scopeType(ScopeType.INHERIT)
                .description("show the version and exit")
                .build());

        root.addSubcommand("repl", new CommandLine(
                command("repl", "start an interactive session")));
        root.addSubcommand("run", new CommandLine(
                script("run", "evaluate a script")));
        root.addSubcommand("tokenize", new CommandLine(
                script("tokenize", "print the token stream of a script")));
        root.addSubcommand("parse", new CommandLine(
                script("parse", "print the syntax tree of a script")));

        return new CommandLine(root).setExecutionStrategy(JLox::dispatch);
    }

    private static CommandSpec command(String name, String description) {
        var spec = CommandSpec.create().name(name);
        spec.usageMessage().description(description);
        spec.exitCodeOnInvalidInput(USAGE_ERROR);
        spec.exitCodeOnExecutionException(INTERNAL_ERROR);
        return spec;
    }

    private static CommandSpec script(String name, String description) {
        return command(name, description)
                .addPositional(PositionalParamSpec.builder()
                        .paramLabel("<script>")
                        .type(Path.class)
                        .index("0")
                        .arity("1")
                        .required(true)
                        .description("the lox source file")
                        .build());
    }

    private static int dispatch(ParseResult parsed) {
        var helpExitCode = CommandLine.executeHelpRequest(parsed);
        if (helpExitCode != null) {
            return helpExitCode;
        }

        configureLogging(verbose(parsed));

        var subcommand = parsed.subcommand();
        if (subcommand == null) {
            return repl();
        }

        var name = subcommand.commandSpec().name();
        log.fine(() -> "running command " + name);
        return switch (name) {
            case "repl" -> repl();
            case "run" -> withSource(script(subcommand), JLox::evaluate);
            case "tokenize" -> withSource(script(subcommand), JLox::tokenize);
            case "parse" -> withSource(script(subcommand), JLox::parse);
            default -> throw new IllegalStateException("unbound command: " + name);
        };
    }

    private static boolean verbose(ParseResult parsed) {
        for (var level = parsed; level != null; level = level.subcommand()) {
            if (level.hasMatchedOption("--verbose")) {
                return true;
            }
        }
        return false;
    }

    private static Path script(ParseResult parsed) {
        return parsed.matchedPositionalValue(0, (Path) null);
    }

    private static int repl() {
        return new Repl().run();
    }

    private static int tokenize(String source) {
        new Tokenizer().scan(source).forEach(System.out::println);
        return OK;
    }

    private static int parse(String source) {
        var tokens = new Tokenizer().scan(source);
        var expr = new Parser().parse(tokens);
        if (expr.isEmpty()) {
            System.err.println("jlox: could not parse the input");
            return DATA_ERROR;
        }
        System.out.println(new AST().prettyPrint(expr.get()));
        return OK;
    }

    static int evaluate(String source) {
        return parse(source);
    }

    private static int withSource(Path script, ToIntFunction<String> action) {
        String source;
        try {
            source = Files.readString(script);
        } catch (NoSuchFileException e) {
            System.err.println("jlox: no such file: " + script);
            return NO_INPUT;
        } catch (IOException e) {
            System.err.println("jlox: cannot read " + script + ": " + e.getMessage());
            return NO_INPUT;
        }
        return action.applyAsInt(source);
    }

    private static void configureLogging(boolean verbose) {
        var level = verbose ? Level.FINE : Level.SEVERE;

        for (var existing : log.getHandlers()) {
            log.removeHandler(existing);
        }

        var handler = new ConsoleHandler();
        handler.setLevel(level);
        log.addHandler(handler);
        log.setLevel(level);
        log.setUseParentHandlers(false);
    }
}
