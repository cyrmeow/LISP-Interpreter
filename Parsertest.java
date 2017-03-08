import lisp.Parser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Test class
 *
 * @author Yiran Cao
 *
 */

public final class Parsertest{
	private Parsertest(){}

	public static void main(String[] args) {
		Scanner in;
        // read input from standard input
        in = new Scanner(System.in).useDelimiter("");
        Parser lispParser = new Parser(in);
        lispParser.output();
	}
}