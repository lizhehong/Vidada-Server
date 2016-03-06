package com.elderbyte.server.vidada.queries;

import com.elderbyte.code.dom.expressions.*;
import com.elderbyte.code.generators.ICodeGenerator;

/**
 * Generats JPQL Code
 */
public class JPQLExpressionCodeGenerator  implements ICodeGenerator {

    @Override
    public String generate(ExpressionNode node) {

        if(node instanceof LiteralValueExpression){
            // TODO propably handle Numbers specially
            return "'" + ((LiteralValueExpression) node).getValue() + "'";
        }

        if(node instanceof VariableReference){
            return ((VariableReference) node).getName();
        }


        if(node instanceof BinaryOperatorExpression){
            BinaryOperatorExpression bin = (BinaryOperatorExpression) node;
            return "(" + generate(bin.getLeft()) + " " + getOperatorSign(bin.getOperator()) + " " + generate(bin.getRight()) + ")";
        }

        if(node instanceof UnaryOperatorExpression){
            UnaryOperatorExpression unary = (UnaryOperatorExpression) node;
            return getOperatorSign(unary.getOperator()) + " (" + generate(unary.getInner()) + ")";
        }

        return "<" + node.getClass() + ">";
    }


    private String getOperatorSign(Operator op){

        if(op.getSign().equals("!") || op.getSign().toUpperCase().equals("NOT")){
            return "NOT";
        }

        if(op.getSign().equals("&") || op.getSign().toUpperCase().equals("AND")){
            return "AND";
        }

        if(op.getSign().equals("|") || op.getSign().toUpperCase().equals("OR")){
            return "OR";
        }

        return op.getSign();
    }
}
