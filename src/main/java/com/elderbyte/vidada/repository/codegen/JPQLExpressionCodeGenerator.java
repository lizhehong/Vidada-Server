package com.elderbyte.vidada.repository.codegen;

import com.elderbyte.code.dom.expressions.*;
import com.elderbyte.code.generators.ICodeGenerator;

/**
 * Generats JPQL Code
 */
public class JPQLExpressionCodeGenerator  implements ICodeGenerator {

    @Override
    public String generate(ExpressionNode node) {
        if(node instanceof LiteralValueExpression){
            return "'" + ((LiteralValueExpression) node).getValue() + "'";
        }

        if(node instanceof VariableReference){
            return ((VariableReference) node).getName();
        }

        if(node instanceof BinaryOperatorExpression){
            BinaryOperatorExpression bin = (BinaryOperatorExpression) node;
            return "(" + generate(bin.getLeft()) + " " + bin.getOperator().getSign() + " " + generate(bin.getRight()) + ")";
        }

        if(node instanceof UnaryOperatorExpression){
            UnaryOperatorExpression unary = (UnaryOperatorExpression) node;
            return unary.getOperator().getSign() + " (" + generate(unary.getInner()) + ")";
        }

        return "<" + node.getClass() + ">";
    }
}
