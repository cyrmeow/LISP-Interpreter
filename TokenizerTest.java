package lisp;

import static lisp.TokenKind.ERROR;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Part 1 of Project 1 for CSE 3341. Test a Tokenizer for Core.
 *
 * @author Yiran Cao
 *
 */
public final class TokenizerTest {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TokenizerTest() {
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        Scanner in;
        // System.out.println(Integer.parseInt("-42"));
        try {
            // in = new Scanner("(2 . (3 . (4 . NIL)))");
            in = new Scanner(Paths.get(args[0])).useDelimiter("");
        } catch (IOException e) {
            System.err.println("Error opening file: " + args[0]);
            return;
        }
        Tokenizer t = LispTokenizer.create(in);
        while (t.getToken() != ERROR) {
            System.out.println(t.getToken().getTokenNumber());
            if(t.getToken() == TokenKind.EOF) break;
            t.skipToken();
        }
        if (t.getToken() == ERROR) {
            System.out.println("Error: Illegal token encountered.");
            in.close();
            System.exit(0);
        }
        in.close();
        /*
         * Close input stream
         */

        // *******************test code******************* /
        // try {
        //     // in = new Scanner("(2 . (3 . (4 . NIL)))");
        //     in = new Scanner(Paths.get(args[0])).useDelimiter("");
        // } catch (IOException e) {
        //     System.err.println("Error opening file: " + args[0]);
        //     return;
        // }
        // while(in.hasNext()) {
        //     System.out.println(in.next());
        // }
        
        // in.close();
    }

}
