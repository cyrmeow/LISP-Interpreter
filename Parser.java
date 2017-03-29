
package lisp;
import java.util.*;

/**
 * Parser for lisp
 *
 * @author Yiran Cao
 *
 */
public class Parser {
    private LispTokenizer tokenizer;
    private static ArrayList<SExp> existSymbol;
    private static ArrayList<SExp> reserved;
    private static SExp nil;
    private static SExp t;
    private static SExp aList;
    private static SExp dList;
    static {
        existSymbol = new ArrayList<SExp>();
        reserved = new ArrayList<SExp>();

        // reserved symbolic atoms
        nil = new SExp(2, "NIL");
        existSymbol.add(nil);
        reserved.add(nil);
        t = new SExp(2, "T");
        existSymbol.add(t);
        reserved.add(nil);

        aList = nil;
        dList = nil;

        // primitive function names
        SExp car = new SExp(2, "CAR");
        existSymbol.add(car);
        reserved.add(car);

        SExp cdr = new SExp(2, "CDR");
        existSymbol.add(cdr);
        reserved.add(cdr);

        SExp cons = new SExp(2, "CONS");
        existSymbol.add(cons);
        reserved.add(cons);

        SExp nul = new SExp(2, "NULL");
        existSymbol.add(nul);
        reserved.add(nul);

        SExp atom = new SExp(2, "ATOM");
        existSymbol.add(atom);
        reserved.add(atom);

        SExp eq = new SExp(2, "EQ");
        existSymbol.add(eq);
        reserved.add(eq);

        SExp plus = new SExp(2, "PLUS");
        existSymbol.add(plus);
        reserved.add(plus);

        SExp minus = new SExp(2, "MINUS");
        existSymbol.add(minus);
        reserved.add(minus);

        SExp isInt = new SExp(2, "INT");
        existSymbol.add(isInt);
        reserved.add(isInt);

        SExp times = new SExp(2, "TIMES");
        existSymbol.add(times);
        reserved.add(times);

        SExp quotient = new SExp(2, "QUOTIENT");
        existSymbol.add(quotient);
        reserved.add(quotient);

        SExp remainder = new SExp(2, "REMAINDER");
        existSymbol.add(remainder);
        reserved.add(remainder);

        SExp less = new SExp(2, "LESS");
        existSymbol.add(less);
        reserved.add(less);

        SExp greater = new SExp(2, "GREATER");
        existSymbol.add(greater);
        reserved.add(greater);


        // 3 special forms
        SExp defun = new SExp(2, "DEFUN");
        existSymbol.add(defun);
        reserved.add(defun);

        SExp cond = new SExp(2, "COND");
        existSymbol.add(cond);
        reserved.add(cond);

        SExp quote = new SExp(2, "QUOTE");
        existSymbol.add(quote);
        reserved.add(quote);

    }

    public Parser(Scanner in) {
        this.tokenizer = LispTokenizer.create(in);
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
            if (this.tokenizer.getToken() == TokenKind.RIGHT_PARENTHESIS) {
                this.tokenizer.skipToken(); // skip ")"
                return getId("NIL");
            }
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
        boolean first = true;
        while(this.tokenizer.getToken() != TokenKind.EOF) {
            SExp out = nil;
            try{
                out = input();
                first = false;
                if(this.tokenizer.getToken() == TokenKind.SPACE) this.tokenizer.skipToken();

                if (this.tokenizer.getToken() != TokenKind.DOLLAR) {
                    // if the top level s-exp is not followed by a $, print error message and skip to next $
                    System.out.println("Error: unexpected token " + this.tokenizer.getToken().toString());
                    skipLine();
                    this.tokenizer.skipToken();
                    continue;
                }

                try {
                    out = eval(out);
                    System.out.println("> " + out.toString());
                } catch (RuntimeErrorException e) {
                    this.tokenizer.skipToken();
                    System.out.println(e.getMessage());
                }

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

	/*--------------PRIMITIVE FUNCTIONS--------------*/

    // CAR
    private SExp car(SExp e) throws RuntimeErrorException {
        if (atom(e) == t) {
            throw new RuntimeErrorException("Error: CAR cannot be applied to an atom");
        } else {
            return e.getLeft();
        }
    }

    // CDR
    private SExp cdr(SExp e) throws RuntimeErrorException {
        if (atom(e) == t) {
            throw new RuntimeErrorException("Error: CAR cannot be applied to an atom");
        } else {
            return e.getRight();
        }
    }
    // CONS
    private SExp cons(SExp e1, SExp e2) {
        return new SExp(3, e1, e2);
    }

    // ATOM
    private SExp atom(SExp e) {
        if (e.getType() == 3) {
            return nil;
        } else {
            return t;
        }
    }

    // EQ
    private SExp eq(SExp e1, SExp e2) {
        if(e1.getType() == 1 && e2.getType() == 1) {
            if(e1.getVal() == e2.getVal()) {
                return t;
            } else {
                return nil;
            }
        } else if (e1 == e2) {
            return t;
        } else {
            return nil;
        }
    }

    // NULL
    private SExp isNull(SExp e) {
        if (e == nil) {
            return t;
        } else {
            return nil;
        }
    }

    // INT
    private SExp isInt(SExp e) {
        if (e.getType() == 1) {
            return t;
        } else {
            return nil;
        }
    }

    // PLUS
    private SExp plus(SExp e1, SExp e2) throws RuntimeErrorException {
        if (e1.getType() != 1 || e2.getType() != 1) {
            throw new RuntimeErrorException("Error: PLUS can only apply to 2 integers");
        } else {
            int v1 = e1.getVal();
            int v2 = e2.getVal();
            return new SExp(1, v1 + v2);
        }
    }

    // MINUS
    private SExp minus(SExp e1, SExp e2) throws RuntimeErrorException {
        if (e1.getType() != 1 || e2.getType() != 1) {
            throw new RuntimeErrorException("Error: MINUS can only apply to 2 integers");
        } else {
            int v1 = e1.getVal();
            int v2 = e2.getVal();
            return new SExp(1, v1 - v2);
        }
    }

    // TIMES
    private SExp times(SExp e1, SExp e2) throws RuntimeErrorException {
        if (e1.getType() != 1 || e2.getType() != 1) {
            throw new RuntimeErrorException("Error: TIMES can only apply to 2 integers");
        } else {
            int v1 = e1.getVal();
            int v2 = e2.getVal();
            return new SExp(1, v1 * v2);
        }
    }

    // QUOTIENT
    private SExp quotient(SExp e1, SExp e2) throws RuntimeErrorException {
        if (e1.getType() != 1 || e2.getType() != 1) {
            throw new RuntimeErrorException("Error: QUOTIENT can only apply to 2 integers");
        } else {
            int v1 = e1.getVal();
            int v2 = e2.getVal();
            return new SExp(1, v1 / v2);
        }
    }

    // REMAINDER
    private SExp remainder(SExp e1, SExp e2) throws RuntimeErrorException {
        if (e1.getType() != 1 || e2.getType() != 1) {
            throw new RuntimeErrorException("Error: REMAINDER can only apply to 2 integers");
        } else {
            int v1 = e1.getVal();
            int v2 = e2.getVal();
            return new SExp(1, v1 % v2);
        }
    }

    // LESS
    private SExp less(SExp e1, SExp e2) throws RuntimeErrorException {
        if (e1.getType() != 1 || e2.getType() != 1) {
            throw new RuntimeErrorException("Error: LESS can only apply to 2 integers");
        } else {
            int v1 = e1.getVal();
            int v2 = e2.getVal();
            if (v1 < v2) {
                return t;
            } else {
                return nil;
            }
        }
    }

    // GREATER
    private SExp greater(SExp e1, SExp e2) throws RuntimeErrorException {
        if (e1.getType() != 1 || e2.getType() != 1) {
            throw new RuntimeErrorException("Error: GREATER can only apply to 2 integers");
        } else {
            int v1 = e1.getVal();
            int v2 = e2.getVal();
            if (v1 > v2) {
                return t;
            } else {
                return nil;
            }
        }
    }

    // COND

    // isReserve
    private boolean isReserve(SExp e) {
        int len = reserved.size();
        for (int i = 0; i < len; i ++) {
            if(reserved.get(i) == e) {
                return true;
            }
        }
        return false;
    }

    // getValue
    private SExp getValue(SExp exp, SExp list) throws RuntimeErrorException {
        try{
            if(list == nil) {
                return nil;
            } else if (car(car(list)) == exp) {
                return cdr(car(list));
            } else {
                return getValue(exp, cdr(list));
            }
        } catch (RuntimeErrorException e) {
            throw e;
        }
    }

    // in
    private boolean in(SExp symbol, SExp list) throws RuntimeErrorException{
        try{
            if(list == nil) {
                return false;
            } else if (car(car(list)) == symbol) {
                return true;
            } else {
                return in(symbol, cdr(list));
            }
        } catch (RuntimeErrorException e) {
            throw e;
        }
    }

    // addToDList
    private void addToDList(SExp e) {
        dList = cons(e, dList);
    }
//    addToAList
    private void addToAList(SExp parList, SExp valList) throws RuntimeErrorException {
//        SExp l1 = parList;
//        SExp l2 = valList;
        while(parList != nil && valList != nil) {
            SExp par = car(parList);
            SExp val = car(valList);
//            if(in(par, aList)) {
//                SExp curPair = car(aList);
//                SExp list = aList;
//                while(car(curPair) != par) {
//                    list = cdr(list);
//                    curPair = car(list);
//                }
//                curPair.setRight(val);
//            } else {
                aList = cons(cons(par, val), aList);
//            }
            parList = cdr(parList);
            valList = cdr(valList);
        }
        if(valList != nil) {
            throw new RuntimeErrorException("ERROR: too many arguments");
        }
        if (parList != nil) {
            throw new RuntimeErrorException("ERROR: too few arguments");
        }
    }
    // EVAL
    public SExp eval(SExp exp) throws RuntimeErrorException {
        if (atom(exp) == t) {
            if (isInt(exp) == t) {
                return exp;
            } else if (eq(exp, t) == t) {
                return t;
            } else if (eq(exp, nil) == t) {
                return nil;
            } else {

                if(!in(exp, aList)) {
                    throw new RuntimeErrorException("unbound variable: " + exp.toString());
                } else {
                    SExp value = getValue(exp, aList);
                    return value;
                }
            }
        } else if (atom(car(exp)) != t) {
            throw new RuntimeErrorException("Error: trying to evaluate " + car(exp).toString());

        } else {
            if (eq(car(exp), getId("QUOTE")) == t) {
                return car(cdr(exp));
            } else if (eq(car(exp), getId("COND")) == t) {
                return evcon(cdr(exp));
            } else if (eq(car(exp), getId("DEFUN")) == t) {
                SExp funcName = car(cdr(exp));
                if (isReserve(funcName)) {
                    throw new RuntimeErrorException("ERROR: " + funcName.toString() + " is reserved");
                }
                SExp parList = car(cdr(cdr(exp)));
                SExp body = car(cdr(cdr(cdr(exp))));
                SExp newEle = cons(funcName, cons(parList, body));
                addToDList(newEle);
                return funcName;
            } else {
                SExp funcName = car(exp);
                SExp arglist = cdr(exp);
                if(atom(arglist) == t) {
                    throw new RuntimeErrorException("Error: trying to evaluate " + exp);
                }

                return apply(funcName, evlis(arglist));
            }
        }
    }

    // EVLIS
    private SExp evlis(SExp list) throws RuntimeErrorException {
        try{
            if(isNull(list) == t) {
                return nil;
            } else {
                SExp result = cons(eval(car(list)), evlis(cdr(list)));
                return result;
            }
        } catch (RuntimeErrorException e) {
            throw e;
        }
    }

    // EVCON
    private SExp evcon(SExp be) throws RuntimeErrorException {
        if(isNull(be) == t) {
            throw new RuntimeErrorException("Error: be list is null");
        } else if (eval(car(car(be))) != nil) {
            return eval(car(cdr(car(be))));
        } else {
            return evcon(cdr(be));
        }
    }

    // APPLY
    private SExp apply(SExp f, SExp x) throws RuntimeErrorException {
        if (atom(f) == t) {
            if (eq(f, getId("CAR")) == t) {
                return car(car(x));
            } else if (eq(f, getId("CDR")) == t) {
                return cdr(car(x));
            } else if (eq(f, getId("CONS")) == t) {
                return cons(car(x), car(cdr(x)));
            } else if (eq(f, getId("ATOM")) == t) {
                return atom(car(x));
            } else if (eq(f, getId("NULL")) == t) {
                return isNull(car(x));
            } else if (eq(f, getId("EQ")) == t) {
                return eq(car(x), car(cdr(x)));
            } else if (eq(f, getId("INT")) == t) {
                return isInt(car(x));
            } else if (eq(f, getId("PLUS")) == t) {
                return plus(car(x), car(cdr(x)));
            } else if (eq(f, getId("MINUS")) == t) {
                return minus(car(x), car(cdr(x)));
            } else if (eq(f, getId("TIMES")) == t) {
                return times(car(x), car(cdr(x)));
            } else if (eq(f, getId("QUOTIENT")) == t) {
                return quotient(car(x), car(cdr(x)));
            } else if (eq(f, getId("REMAINDER")) == t) {
                return remainder(car(x), car(cdr(x)));
            } else if (eq(f, getId("LESS")) == t) {
                return less(car(x), car(cdr(x)));
            } else if (eq(f, getId("GREATER")) == t) {
                return greater(car(x), car(cdr(x)));
            } else if (isInt(f) == t) {
                throw new RuntimeErrorException("Error: function name couldn't be an integer");
            } else {
                SExp parBody = getValue(f, dList);
                if (parBody == nil) {
                    throw new RuntimeErrorException("Error: function " + f.toString() + " not defined");
                } else {
                    addToAList(car(parBody), x);
                    return eval(cdr(parBody));
                }
            }
        } else {
            throw new RuntimeErrorException("ERROR: function name should be an atom!");
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

class RuntimeErrorException extends Exception{
    public RuntimeErrorException(){
        super();
    }
    public RuntimeErrorException(String msg){
        super(msg);
    }
}