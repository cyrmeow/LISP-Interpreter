# Design Documentation

## Description
As the first half of the Lisp Interpreter project, this program takes s-expression inputs, converts and stores them to s-expression objects and outputs the s-expression in dotted form.

## SExp class
Each s-expression is regarded as an instance of SExp class. Since there are three possible types of s-expression, which are integer atoms, symbolic atoms, and binary trees, the instance variable `type` is introduced to identify the type of a s-expression. Accordingly, three constructors are called when creating different types of `SExp` objects.

The value of a integer atom is stored in the `value` instance variable, and the name of a symbolic atom is stored in the `name` instance variable. 

If the s-expression is not an atom, it has a left child and a right child, which are both s-expressions.

## Tokenizer
The tokenizer is implemented based on the finite state automaton for Lisp. It recognizes 9 kinds of tokens, which are left parenthesis "(", right parenthesis ")", dot ".", dollar sign "$", integer, identifier, whitespace, EOF, and error.

## Input Routine
the input s-expression could be an atom, or a binary tree in list notation, dotted notation or mixed form, and the input routine should take care of all possible forms, converting it to a s-expression object.

The BNF for Lisp can be defined as following:

> < s-exp > ::= < atom_symbol > | < atom_integer > | () | (< s-exp > . < s-exp >) | (< s-exp > < rest >
> 
> < rest >  ::= ) | < s-exp > < rest >
> 

The input routine parse the input stream in a recursive descent fashion according to the BNF.

The method of parsing < s-exp > is implemented as input(), following the steps below:

1. If the next token is an integer, create a new integer s-expression object and return it.
2. If the next token is an identifier, search the exisiting symbol list for it by name. If the symbol currently exists, return it, otherwise, create a new symbolic atom s-expression, adding it to the exisiting symbol list and return it.
3. If the next token is a left parenthesis, parse the next < s-exp > first and take the return value as the left child of the binary tree. 
4. After that, if the next token is a dot, continue parsing the next < s-exp > and take the return value as the right child. Otherwise, parse < rest > and take the return value as the right child.
5. construct a new binary tree with left and right children and return the new binary tree.

The method of parsing < rest > is implemented as input2(), following the steps below:

1. If the next token is a right parenthesis, return the symbolic atom "NIL".
2. Otherwise, parse the next < s-exp > and takes the return value as left child.
3. After that, parse the following < rest > and takes the return value as right child.
4. Create a new binary with left and right children and return the new tree.

## Output
The output routine simply outputs every legal s-expression in the input stream, as well as any error message, to the standard output.

## Evaluation

The evaluation process is simply implemented in lisp style, borrowing most of the ideas from the class note, "transtating" the lisp expressions into java language.

### a-list and d-list
The a-list and d-list are two static object of the `SExp` class. When a new function is defined, the d-list is updated, taking the newly defined function as its car and the old d-list as its cdr. When a the `apply()` method is called, the a-list will be updated, adding the latest bindings to the head of the a-list.

### Reserved Symbolic S-expressions

#### Reserved Symbolic Atoms
`NIL` and `T` are two reserved sybolic atoms, which I implemented as two static variables `SExp t` and `SExp nil`

#### Reserved Function Names (Primitive Functions)
Primitive functions are pre-implemented in java, without being added to the d-list. When the user called a function, the interpreter will first check if it's a primitive function. If the user attempts to define a function in the name of a primitive function, the interpreter will not allow the definition and give a runtime error.

### Evaluation

#### Atoms

If the S-expression is an integer atom, the interprer will evaluates it as itself. If it's a symbolic atom, the interpreter will first check if it's a `T` or `NIL`, if it's not `T` or `NIL`, the interpreter will try to find the bindings in the a-List, giving errors if it's an unbound variable.

#### Special forms

If the car of the S-expression is one of the three special forms, QUOTE, COND, or DEFUN, the interpreter will call the corresponding evaluation function to evalutate it.

#### Function Applications

If the car of the S-expression is neither one of the three special forms, nor an atom, it will be treated as a function application. The interpreter will try to evaluate each of the arguements, then evaluates the outermost function application. If the function name is not found in the d-list, or the length of the arguement list is not same as the parameter list, or one of its arguements evaluation encounters an error, the evaluation won't succeed.


## Handling Errors
During the interpreting process, if the interpreter encounters any unexpected tokens during the input stage, or encounters any error during the evaluation stage, it will throw an exception. The output routine will catch it and print the error message.