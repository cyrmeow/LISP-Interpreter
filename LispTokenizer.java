package lisp;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Tokenizer for Lisp
 *
 * @author Yiran Cao
 *
 */

class LispTokenizer implements Tokenizer {

	private static String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String lowercase = "abcdefghijklmnopqrstuvwxyz";
	private static String digits = "0123456789";
	private static String whitespaces = " \n\t\r";
	private static String sign = "+-";
	
	private Scanner src;
	private String cur;
	private int int_token_value;
	private String id_token_name;

	private TokenKind curTokenKind;
	
	private LispTokenizer(Scanner in) {
		this.src = in;
		this.int_token_value = 0;
		this.id_token_name = null;
		this.cur = "";
		this.skipToken();
	}
	public static LispTokenizer create(Scanner in) {
		return new LispTokenizer(in);
	}

	public TokenKind getToken() {
		return curTokenKind;
	}
	public void skipToken(){
		if (cur.equals("") && !src.hasNext()) {
			this.curTokenKind = TokenKind.EOF;
		} else {
			if(cur.equals("")) {
				cur = src.next();
			}

			// The Automaton for LISP
			if (cur.equals("(")) {
				cur = "";
				this.curTokenKind = TokenKind.LEFT_PARENTHESIS;
			} else if (cur.equals(")")) {
				cur = "";
				this.curTokenKind = TokenKind.RIGHT_PARENTHESIS;
			} else if (cur.equals(".")) {
				cur = "";
				this.curTokenKind = TokenKind.DOT;
			} else if (cur.equals("$")) {
				cur = "";
				this.curTokenKind = TokenKind.DOLLAR;
			} else if (sign.contains(cur) || digits.contains(cur)) {
				StringBuffer buffer = new StringBuffer(cur);
				boolean updated = false;
				while(src.hasNext()) {
					cur = src.next();
					if (digits.contains(cur)) {
						buffer.append(cur);
					} else if (uppercase.contains(cur) || lowercase.contains(cur) || sign.contains(cur)) {
						updated = true;
						this.curTokenKind = TokenKind.ERROR;
						break;
					} else {
						updated = true;
						this.int_token_value = Integer.parseInt(new String(buffer));
						this.curTokenKind = TokenKind.INTEGER;
						break;
					}
				}

				if(!updated){
					cur = "";
					this.int_token_value = Integer.parseInt(new String(buffer));
					this.curTokenKind = TokenKind.INTEGER;
				}
			} else if (uppercase.contains(cur) || lowercase.contains(cur)) {
				StringBuffer buffer = new StringBuffer(cur);
				boolean updated = false;
				while(src.hasNext()) {
					cur = src.next();
					if (digits.contains(cur) || uppercase.contains(cur) || lowercase.contains(cur)) {
						buffer.append(cur);
					} else if (sign.contains(cur)) {
						updated = true;
						this.curTokenKind = TokenKind.ERROR;
						break;
					} else {
						updated = true;
						this.id_token_name = new String(buffer).toUpperCase();
						this.curTokenKind = TokenKind.IDENTIFIER;
						break;
					}
				}
				if(!updated) {
					cur = "";
					this.id_token_name = new String(buffer).toUpperCase();
					this.curTokenKind = TokenKind.IDENTIFIER;
				}
			} else if (whitespaces.contains(cur)) {
				boolean updated = false;
				while(src.hasNext()) {
					cur = src.next();
					if (!whitespaces.contains(cur)) {
						updated = true;
						this.curTokenKind = TokenKind.SPACE;
						break;
					}
				}
				if(!updated) {
					cur = "";
					this.curTokenKind = TokenKind.SPACE;
				}
			} else {
				this.curTokenKind = TokenKind.ERROR;
			}
		}

		
	}

	public int intVal() {
		return this.int_token_value;
	}

	public String idName(){
		return this.id_token_name;
	}
}