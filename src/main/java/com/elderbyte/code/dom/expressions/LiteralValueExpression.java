package com.elderbyte.code.dom.expressions;


public class LiteralValueExpression extends ExpressionNode {

    private final String value;

    public LiteralValueExpression(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
