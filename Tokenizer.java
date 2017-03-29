package lisp;

/**
 * Tokenizer Interface
 *
 * @author Yiran Cao
 *
 */

interface Tokenizer {


    /**
     * Return the kind of the front token.
     */
    TokenKind getToken();

    /**
     * Skip front token.
     */
    void skipToken();
    /**
     * Return the integer value of the front INTEGER_CONSTANT token. (Restores
     * this.)
     */
    int intVal();

    /**
     * Return the name of the front IDENTIFIER token. (Restores this.)
     */
    String idName();
}
