package com.elderbyte.code.dom.expressions;

/**
 * Represents a boolean and expression
 * (a & b)
 */
public class BinaryOperatorExpression extends BinaryExpression {

    private final Operator operator;

    public BinaryOperatorExpression(ExpressionNode left, Operator operator, ExpressionNode right) {
        super(left, right);
        this.operator = operator;
    }


    public Operator getOperator() {
        return operator;
    }
}
