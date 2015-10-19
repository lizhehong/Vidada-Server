package com.elderbyte.code.dom.expressions;


public class IsMemberOfExpression extends BinaryExpression {


    public IsMemberOfExpression(LiteralValueExpression item, VariableReference collection){
        super(item, collection);
    }

    @Override
    public String toString(){
        return getLeft().toString() + " isMemberOf " + getRight().toString();
    }
}
