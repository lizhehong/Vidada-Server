package com.elderbyte.code.dom.expressions;

/**
 * Negates the given boolean expression
 */
public class UnaryOperatorExpression extends UnaryExpression {

    private final Operator operator;


    public UnaryOperatorExpression(Operator operator, ExpressionNode expressionNode){
        super(expressionNode);
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

}
