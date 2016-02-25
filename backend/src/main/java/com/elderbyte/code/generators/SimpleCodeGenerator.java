package com.elderbyte.code.generators;


import com.elderbyte.code.dom.expressions.*;


/**
 * Turns the AST in a simple String with a Java-Like syntax, mostly used for debugging.
 * - supports only the most basic Nodes
 */
public class SimpleCodeGenerator implements ICodeGenerator {

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
