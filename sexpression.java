package lisp;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

class SExp{

	private int type; // 1 = integer, 2 = symbolic atom, 3 = binary tree
	private String name;
	private int value;
	private SExp left;
	private SExp right;

	public SExp(int t, int v) {
		//constructor for integer
		if(t == 1) {
			this.type = t;
			this.value = v;
		} else {
			System.exit(0);
		}
	}
	public SExp(int t, String s) {
		this.type = t;
		this.name = new String(s);
	}
	public SExp(int t, SExp e1, SExp e2){
		this.type = t;
		this.left = e1;
		this.right = e2;
	}
	public int getType() {
		return this.type;
	}
	public String getName() {
		return this.name;
	}
	public int getVal() {
		return this.value;
	}
	public String toString() {
		if(this.getType() == 1) {
			return new String(this.getVal() + "");
		} else if(this. getType() == 2) {
			return this.getName();
		} else {
			return new String( "(" + this.left.toString() + "." + this.right.toString() + ")");
		}
	}
}