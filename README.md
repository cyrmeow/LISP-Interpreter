README.txt

# README
*Author: Yiran Cao*

## Description
This is the second part of the Lisp interpreter project, with the whole Lisp interpreter being implemented. The program takes its input, which should be an S-expression in the form of either list, dotted or mixed notation, from standard input, evaluates it and outputs the evaluation result in dotted notation to standard output, giving error message when encountering any error.

## Source File List
* TokenKind.java: A enum class defining different kinds of tokens, used by the tokenizer.
* Tokenizer.java: Contains a general tokenizer interface.
* LispTokenizer.java: A tokenizer for Lisp, implementing the `Tokenizer ` interface.
* sexpression.java: Defines `SExp` class.
* Parser.java: Where the main functinality of the interpreter, including input, output, and evaluation is implemented.
* Interpreter.java: `main` function stays here.

## HOW-TO-RUN
1. Open up a command line terminal, change the working directory where source files stay.
2. Type `make` to compile.
3. After compiling successfully, type `java Interpreter` to run the program.
4. Input S-expressions. Each complete s-expression should be followed by a line containing a single “$” sign. The last s-expression should be followed by a line containing “$$”. 

## WARNINGS
1. Your input should contain ONLY legal tokens, which are upppercase/lowercase letters, digits, left/right parentheses("(" and ")"), dots("."), positive/negative signs("+" and "-"), dollar sign("$") and whitespaces(" ", "\t", "\n" and "\r"), or the program can crash or end up in infinite loop.

2. If the interpreter encounters any error, the error message won't be printed immediately after the $ sign at the end of the S-expression. In this case, you need to input another $ and hit enter, then the error message would appear on the screen.
