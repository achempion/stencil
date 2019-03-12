package io.github.erdos.stencil;

import io.github.erdos.stencil.exceptions.EvalException;
import io.github.erdos.stencil.exceptions.ParsingException;
import io.github.erdos.stencil.standalone.ArgsParser;
import io.github.erdos.stencil.standalone.StandaloneApplication;
import io.github.erdos.stencil.standalone.StencilArgsParser;

import java.io.IOException;

import static io.github.erdos.stencil.impl.ClojureHelper.callShutdownAgents;

public class Main {

    public static void main(String... args) throws IOException {
        try {
            final ArgsParser.ParseResult parsed = StencilArgsParser.parse(args);
            final StandaloneApplication app = new StandaloneApplication(parsed);

            app.run();
        } catch (IllegalArgumentException e) {
            System.out.println("Parameter error! See --help for usage information.");
            printException(e);
            System.exit(2);
        } catch (ParsingException e) {
            System.out.println("Error parsing template!");
            System.out.println("Expression: " + e.getExpression());
            printException(e);
            System.exit(3);
        } catch (EvalException e) {
            System.out.println("Error rendering template!");
            printException(e);
            System.exit(4);
        } finally {
            // stop Clojure thread pools
            callShutdownAgents();
        }
    }

    private static void printException(Throwable e) {
        if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
            System.out.println(e.getMessage());
        } else {
            System.out.print(e.getClass().getName());
        }

        for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
            System.out.println("- " + t.getClass().getName() + " " + t.getMessage());
        }
    }
}
