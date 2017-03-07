// Author: Yiran Cao.805

package lisp;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

class LispTokenizer implements Tokenizer {

	private static String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String lowercase = "abcdefghijklmnopqrstuvwxyz";
	private static String digits = "0123456789";
	private static String whitespaces = " \n\t\r";
	private static String sign = "+-";
	
	private Scanner src;
	private int entry;
	private int end;
	private String line;

	private int int_token_value;
	private String id_token_name;

	
	private LispTokenizer(Scanner in) {
		this.src = in;
		this.entry = 0;
		this.end = 0;
		this.line = null;
		this.int_token_value = 0;
		this.id_token_name = null;
	}
	public static LispTokenizer create(Scanner in) {
		return new LispTokenizer(in);
	}

	public TokenKind getToken(){
		// if it's the start of the program and has seen no lines
		if (this.line == null) {
			if (this.src.hasNextLine()) {
				this.line = this.src.nextLine();
				this.entry = 0;
			} else {
				return TokenKind.EOF;
			}
		}

		this.end = this.entry + 1;

		// check if encountered with EOF
		while(this.line.length() == 0) {
			if(!this.src.hasNextLine()){
				return TokenKind.EOF;
			} else {
				this.line = this.src.nextLine();
			}
		}

		if(this.entry == this.line.length() && !this.src.hasNextLine()) {
			return TokenKind.EOF;
		}

		String first = this.line.substring(this.entry, this.end);

		/* ---------------------------------------- automaton ---------------------------------------- */

		// skipping whitespaces
		while (whitespaces.contains(first)) {
			this.entry ++;
			this.end ++;
			if (this.end >= this.line.length()) {
				if (!this.src.hasNextLine()) {
					return TokenKind.EOF;
				} else {
					this.line = this.src.nextLine();
					while(this.line.length() == 0) {
						if(!this.src.hasNextLine()){
							return TokenKind.EOF;
						} else {
							this.line = this.src.nextLine();
						}
					}
					this.entry = 0;
					this.end = 1;
				}
			}
			first = this.line.substring(this.entry, this.end);
		}

		if(!src.hasNextLine() && this.entry == this.line.length()) {
			// EOF
			return TokenKind.EOF;
		} else if (first.equals("(")) {
			// LEFT PARENTHESIS
			return TokenKind.LEFT_PARENTHESIS;
		}else if (first.equals(")")) {
			// RIGHT PARENTHESIS
			return TokenKind.RIGHT_PARENTHESIS;
		} else if (first.equals(".")) {
			// DOT
			return TokenKind.DOT;
		} else if (first.equals("$")) {
			// DOLLAR
			return TokenKind.DOLLAR;
		} else if (uppercase.contains(first)) {
			// IDENTIFIER
			while (this.end < this.line.length()) {
				if (lowercase.contains(this.line.substring(this.end, this.end + 1))) {
					return TokenKind.ERROR;
				} else if (uppercase.contains(this.line.substring(this.end, this.end + 1)) || digits.contains(this.line.substring(this.end, this.end + 1))) {
					this.end ++;
				} else {
					break;
				}
			}
			this.id_token_name = this.line.substring(this.entry, this.end);
			return TokenKind.IDENTIFIER;
		} else if (digits.contains(first) || sign.contains(first)) {
			// INTEGER
			if(sign.contains(first) && !digits.contains(this.line.substring(this.end, this.end + 1))) {
				return TokenKind.ERROR;
			}
			while(this.end < this.line.length()) {
				if(digits.contains(this.line.substring(this.end, this.end + 1))) {
					this.end ++;
				} else if (uppercase.contains(this.line.substring(this.end, this.end + 1)) 
					|| lowercase.contains(this.line.substring(this.end, this.end + 1))) {
					
					return TokenKind.ERROR;
				} else {
					break;
				}
			}
			this.int_token_value = Integer.parseInt(this.line.substring(this.entry, this.end));
			return TokenKind.INTEGER;
		} else {
			return TokenKind.ERROR;
		}
	}

	public void skipToken(){
		if (this.end == this.line.length()) {
			if(this.src.hasNextLine()) {
				this.line = this.src.nextLine();
				this.entry = 0;
			} else {
				this.entry = this.end;
			}
			
		} else {
			this.entry = this.end;
		}
	}

	public int intVal() {
		return this.int_token_value;
	}

	public String idName(){
		return this.id_token_name;
	}
}