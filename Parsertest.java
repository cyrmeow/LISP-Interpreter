import lisp.Parser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public final class Parsertest{
	private Parsertest(){}

	public static void main(String[] args) {
		Scanner in;
        // System.out.println(Integer.parseInt("-42"));
        try {
            // in = new Scanner("(2 . (3 . (4 . NIL)))");
            in = new Scanner(Paths.get(args[0]));
        } catch (IOException e) {
            System.err.println("Error opening file: " + args[0]);
            return;
        }
        Parser lispParser = new Parser(in);
        lispParser.output();
	}
}