package lisp;

/**
 * Token kinds needed for tokenizer
 *
 * @author Yiran Cao
 *
 */
enum TokenKind {

    LEFT_PARENTHESIS(1),
    RIGHT_PARENTHESIS(2),
    DOT(3),
    INTEGER(4),
    IDENTIFIER(5),
    DOLLAR(6),
    ERROR(7),
    EOF(8);


    /**
     * token number.
     */
    private int TokenNumber;

    /**
     * Constructor.
     *
     * @param number
     *            the token number
     */
    private TokenKind(int number) {
        this.TokenNumber = number;
    }

    /**
     * Return test driver's token number.
     *
     * @return test driver's token number
     */
    public int getTokenNumber() {
        return this.TokenNumber;
    }
}
