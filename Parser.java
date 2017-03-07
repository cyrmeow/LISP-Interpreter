package lisp;
import java.util.*;

public class Parser {
	private LispTokenizer tokenizer;
	private static ArrayList<SExp> existSymbol;
	static {
		existSymbol = new ArrayList<SExp>();
		// reserved symbolic atoms

		// function names
		existSymbol.add(new SExp(2, "NIL"));
		existSymbol.add(new SExp(2, "T"));
		existSymbol.add(new SExp(2, "CAR"));
		existSymbol.add(new SExp(2, "CDR"));
		existSymbol.add(new SExp(2, "CONS"));
		existSymbol.add(new SExp(2, "NULL"));
		existSymbol.add(new SExp(2, "ATOM"));
		existSymbol.add(new SExp(2, "EQ"));
		existSymbol.add(new SExp(2, "PLUS"));
		existSymbol.add(new SExp(2, "MINUS"));

		// 3 special forms
		existSymbol.add(new SExp(2, "DEFUN"));
		existSymbol.add(new SExp(2, "COND"));
		existSymbol.add(new SExp(2, "QUOTE"));

	}

	public Parser(Scanner in) {
		this.tokenizer = LispTokenizer.create(in);
	}

	private SExp cons(SExp e1, SExp e2) {
		return new SExp(3, e1, e2);
	}

	private void skipLine() {
		while(this.tokenizer.getToken() != TokenKind.EOF) {
			if(this.tokenizer.getToken() != TokenKind.DOLLAR) {
				this.tokenizer.skipToken();
			} else {
				break;
			}
		}
	}
	private SExp input() {
		if (this.tokenizer.getToken() == TokenKind.INTEGER) {
			SExp atomInt = new SExp(1, this.tokenizer.intVal());
			this.tokenizer.skipToken(); // skip integer
			return atomInt;
		} else if (this.tokenizer.getToken() == TokenKind.IDENTIFIER) {
			SExp atomSymbol = getId(this.tokenizer.idName());
			this.tokenizer.skipToken(); // skip identifier
			return atomSymbol;
		} else if (this.tokenizer.getToken() == TokenKind.LEFT_PARENTHESIS) {
			this.tokenizer.skipToken(); // skip "("
			SExp left = input();
			SExp right;
			if (this.tokenizer.getToken() == TokenKind.DOT) {
				this.tokenizer.skipToken(); // skip "."
				right = input();
				if(this.tokenizer.getToken() != TokenKind.RIGHT_PARENTHESIS) {
					System.out.println("Error: unexpected token: " + this.tokenizer.getToken());
					// System.exit(0);
					return null;
				}
				this.tokenizer.skipToken(); // skip ")"
			} else {
				right = input2();
			}

			return cons(left, right);
		} else {
			System.out.println("Error: unexpected token: " + this.tokenizer.getToken());
			return null;
		}
	}

	private SExp input2() {
		if(this.tokenizer.getToken() == TokenKind.RIGHT_PARENTHESIS) {
			this.tokenizer.skipToken(); // skip ")"
			return getId("NIL");
		} else {
			SExp left = input();
			SExp right = input2();
			SExp newlist = cons(left, right);
			return newlist;
		}
	}

	private SExp getId(String s) {
		int len = existSymbol.size();
		for(int i = 0; i < len; i ++) {
			if(Parser.existSymbol.get(i).getName().equals(s)) {
				return Parser.existSymbol.get(i);
			}
		}
		SExp newSymbol = new SExp(2, s);
		Parser.existSymbol.add(newSymbol);	
		return newSymbol;
	}

	public void output() {
		while(this.tokenizer.getToken() != TokenKind.EOF) {
			
			SExp out = input();
			if (out != null) {
				System.out.println(out);
			}

			skipLine();
			
			this.tokenizer.skipToken();
			if(this.tokenizer.getToken() == TokenKind.DOLLAR) break;
		} 
	}

}