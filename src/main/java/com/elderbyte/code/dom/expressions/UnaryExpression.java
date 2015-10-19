package com.elderbyte.code.dom.expressions;


public abstract class UnaryExpression extends ExpressionNode {

    private final ExpressionNode inner;

    public UnaryExpression(ExpressionNode expressionNode){
        inner = expressionNode;
    }

    public ExpressionNode getInner() {
        return inner;
    }

}
