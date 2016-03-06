package com.elderbyte.code.dom.expressions;

/**
 * Represents a boolean and expression:
 *
 * [left] (operator) [right]
 *
 * Examples:
 * (a + b)
 * (3 - 6)
 * (true | false)
 */
public class BinaryOperatorExpression extends BinaryExpression {

    private final Operator operator;

    /**
     * Creates a new BinaryOperatorExpression
     * @param left
     * @param operator
     * @param right
     */
    public BinaryOperatorExpression(ExpressionNode left, Operator operator, ExpressionNode right) {
        super(left, right);
        this.operator = operator;
    }


    public Operator getOperator() {
        return operator;
    }
}
