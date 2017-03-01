// Author: Yiran Cao.805

package core;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

class MyTokenizer implements Tokenizer {

	private static String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String lowercase = "abcdefghijklmnopqrstuvwxyz";
	private static String digits = "0123456789";
	private static String whitespaces = " \n\t\r";
	
	private Scanner src;
	private int entry;
	private int end;
	private String line;

	private int int_token_value;
	private String id_token_name;

	
	private MyTokenizer(Scanner in) {
		this.src = in;
		this.entry = 0;
		this.end = 0;
		this.line = null;
		this.int_token_value = 0;
		this.id_token_name = null;
	}
	public static MyTokenizer create(Scanner in) {
		return new MyTokenizer(in);
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
		} else if (first.equals(",")) {
			// COMMA
			return TokenKind.COMMA;
		}else if (first.equals(";")) {
			// SEMICOLON
			return TokenKind.SEMICOLON;
		} else if (first.equals("[")) {
			// LEFT_BRACKET
			return TokenKind.LEFT_BRACKET;
		} else if (first.equals("]")) {
			// RIGHT_BRACKET
			return TokenKind.RIGHT_BRACKET;
		} else if (first.equals("(")) {
			// LEFT_PARENTHESIS
			return TokenKind.LEFT_PARENTHESIS;
		} else if (first.equals(")")) {
			// RIGHT_PARENTHESIS
			return TokenKind.RIGHT_PARENTHESIS;
		} else if (first.equals("+")) {
			// PLUS_OPERATOR
			return TokenKind.PLUS_OPERATOR;
		} else if (first.equals("-")) {
			// MINUS_OPERATOR
			return TokenKind.MINUS_OPERATOR;
		} else if (first.equals("=")) {
			// ASSIGNMENT or EQUALITY_TEST
			if (this.end >= this.line.length()) {
				return TokenKind.ASSIGNMENT_OPERATOR;
			} else if (this.line.charAt(this.end) != '=') {
				return TokenKind.ASSIGNMENT_OPERATOR;
			} else {
				this.end += 1;
				return TokenKind.EQUALITY_TEST;
			}
		} else if (first.equals("<")) {
			// LESS_TEST or LESS_EQUAL_TEST
			if (this.end >= this.line.length()) {
				return TokenKind.LESS_TEST;
			} else if (this.line.charAt(this.end) != '=') {
				return TokenKind.LESS_TEST;
			} else {
				this.end += 1;
				return TokenKind.LESS_EQUAL_TEST;
			}
		} else if (first.equals(">")) {
			// GRESTER_TEST or GREATER_EQUAL_TEST
			if (this.end >= this.line.length()) {
				return TokenKind.GREATER_TEST;
			} else if (this.line.charAt(this.end) != '=') {
				return TokenKind.GREATER_TEST;
			} else {
				this.end += 1;
				return TokenKind.GREATER_EQUAL_TEST;
			}
		} else if (first.equals("|")) {
			// OR_OPERATOR
			if (this.end >= this.line.length()) {
				return TokenKind.ERROR;
			} else if (this.line.charAt(this.end) != '|') {
				return TokenKind.ERROR;
			} else {
				this.end ++;
				return TokenKind.OR_OPERATOR;
			}

		} else if(first.equals("&")){
			if (this.end >= this.line.length()) {
				return TokenKind.ERROR;
			} else if (this.line.charAt(this.end) != '&') {
				return TokenKind.ERROR;
			} else {
				this.end ++;
				return TokenKind.AND_OPERATOR;
			}
		} else if (uppercase.contains(first)) {
			// IDENTIFIER
			while (this.end < this.line.length()) {
				if (lowercase.contains(this.line.substring(this.end, this.end + 1))) {
					return TokenKind.ERROR;
				} else if (uppercase.contains(this.line.substring(this.end, this.end + 1))) {
					this.end ++;
				} else if (digits.contains(this.line.substring(this.end, this.end + 1))) {
					this.end ++;
					break;
				} else {
					break;
				}
			}

			while(this.end < this.line.length()) {
				if (uppercase.contains(this.line.substring(this.end, this.end + 1))) {
					return TokenKind.ERROR;
				} else if (lowercase.contains(this.line.substring(this.end, this.end + 1))) {
					return TokenKind.ERROR;
				} else if (digits.contains(this.line.substring(this.end, this.end + 1))) {
					this.end ++;
				} else {
					break;
				}
			}

			this.id_token_name = this.line.substring(this.entry, this.end);
			return TokenKind.IDENTIFIER;
		} else if (digits.contains(first)) {
			// INTEGER
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
			return TokenKind.INTEGER_CONSTANT;
		} else if (lowercase.contains(first)) {
			// LOWER_CASE_WORD
			while(this.end < this.line.length()) {
				if(uppercase.contains(this.line.substring(this.end, this.end + 1)) 
					|| digits.contains(this.line.substring(this.end, this.end + 1))) {

					return TokenKind.ERROR;
				} else if (lowercase.contains(this.line.substring(this.end, this.end + 1))) {
					this.end ++;
				} else {
					break;
				}
			}
			this.id_token_name = this.line.substring(this.entry, this.end);

			switch (this.id_token_name) {
				case "program": 
					return TokenKind.PROGRAM;
					// break;
				case "begin": 
					return TokenKind.BEGIN;
					// break;
				case "end":
					return TokenKind.END;
					// break;
				case "int":
					return TokenKind.INT;
					// break;
				case "if":
					return TokenKind.IF;
					// break;
				case "then":
					return TokenKind.THEN;
					// break;
				case "else":
					return TokenKind.ELSE;
					// break;
				case "while":
					return TokenKind.WHILE;
					// break;
				case "loop":
					return TokenKind.LOOP;
					// break;
				case "read":
					return TokenKind.READ;
					// break;
				case "write":
					return TokenKind.WRITE;
					// break;
				default:
					return TokenKind.ERROR;
					// break;

			}
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