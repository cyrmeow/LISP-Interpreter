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
	private SExp input() throws UnexpectedTokenException {
		if(this.tokenizer.getToken() == TokenKind.SPACE) {
			this.tokenizer.skipToken(); // skip space
			return input();
		} else if (this.tokenizer.getToken() == TokenKind.INTEGER) {
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
			// skipping unnecessary spaces after left child
			while (this.tokenizer.getToken() == TokenKind.SPACE) this.tokenizer.skipToken();
			SExp right = null;
			if (this.tokenizer.getToken() == TokenKind.DOT) {
				this.tokenizer.skipToken(); // skip "."
				right = input();
				// skipping unnecessary whitespaces after right child
				while (this.tokenizer.getToken() == TokenKind.SPACE) this.tokenizer.skipToken();
				if(this.tokenizer.getToken() != TokenKind.RIGHT_PARENTHESIS) {
					throw new UnexpectedTokenException("Error: unexpected token: " + this.tokenizer.getToken().toString());
				}
				
				this.tokenizer.skipToken(); // skip ")"
			} else {
				try{
					right = input2();
				} catch (UnexpectedTokenException e) {
					throw new UnexpectedTokenException(e.getMessage());
				}
			}
			return cons(left, right);
		} else {
			throw new UnexpectedTokenException("Error: unexpected token: " + this.tokenizer.getToken().toString());
		}
	}

	private SExp input2() throws UnexpectedTokenException {
		if(this.tokenizer.getToken() == TokenKind.SPACE) {
			this.tokenizer.skipToken(); //skip whitespace
			return input2();
		} else if(this.tokenizer.getToken() == TokenKind.RIGHT_PARENTHESIS) {
			this.tokenizer.skipToken(); // skip ")"
			while(this.tokenizer.getToken() == TokenKind.SPACE) this.tokenizer.skipToken();
			return getId("NIL");
		} else {
			SExp left;
			try{
				left = input();
			} catch (UnexpectedTokenException e) {
				throw new UnexpectedTokenException(e.getMessage());
			}
			while (this.tokenizer.getToken() == TokenKind.SPACE) this.tokenizer.skipToken();

			SExp right;
			SExp newlist = null;
			try{ 
				right = input2();
			} catch (UnexpectedTokenException e) {
				throw new UnexpectedTokenException(e.getMessage());
			}
			newlist = cons(left, right);
			while (this.tokenizer.getToken() == TokenKind.SPACE) this.tokenizer.skipToken();
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
			SExp out;
			try{
				out = input();
				System.out.println("> " + out.toString());
			} catch (UnexpectedTokenException e) {
				this.tokenizer.skipToken();
				System.out.println(e.getMessage());
			}

			skipLine();
			
			this.tokenizer.skipToken();
			if(this.tokenizer.getToken() == TokenKind.DOLLAR) {
				System.out.println("> Bye!");
				break;
			}
		} 
	}

}

class UnexpectedTokenException extends Exception{
    public UnexpectedTokenException(){
        super();
    }
    public UnexpectedTokenException(String msg){
        super(msg);
    }
}