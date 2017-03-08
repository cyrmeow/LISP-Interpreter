# README

## Description
This is the first part of the Lisp interpreter project, the input and output routines. The program takes its input, which should be an S-expression in the form of either list, dotted or mixed notation, from standard input, and outputs the dotted notation of that S-expression to standard output.

## File List
* TokenKind.java: A enum class defining different kinds of tokens, used by the tokenizer.
* Tokenizer.java: Contains a general tokenizer interface.
* LispTokenizer.java: A tokenizer for Lisp, implementing the `Tokenizer ` interface.
* sexpression.java: Defines `SExp` class.
* Parser.java: The parser.
* Parsertest.java: `main` function stays here.

## HOW-TO-RUN
1. Open up a command line terminal, change the working directory where source files stay.
2. Type `make` to compile.
3. After compiling successfully, type `java Parsertest` to run the program.
4. Input S-expressions. Each complete s-expression should be followed by a line containing a single “$” sign. The last s-expression should be followed by a line containing “$$”. 

## WARNINGS
Your input should contain ONLY legal tokens, which are upppercase/lowercase letters, digits, left/right parentheses("(" and ")"), dots("."), positive/negative signs("+" and "-"), dollar sign("$") and whitespaces(" ", "\t", "\n" and "\r"), or the program can crash or end up in infinite loop.
