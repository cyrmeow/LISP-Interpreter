import lisp.Parser;

import java.util.Scanner;

/**
 * Main class
 *
 * @author Yiran Cao
 *
 */

public final class Interpreter{
    private Interpreter(){}

    public static void main(String[] args) {
        Scanner in;
        in = new Scanner(System.in).useDelimiter("");
        Parser lispParser = new Parser(in);
        lispParser.output();
    }
}