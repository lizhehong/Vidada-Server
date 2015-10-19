package com.elderbyte.code.dom.expressions;

/**
 * Represents a binary expression
 *
 * right x left : where x is the operator a subclass has to define
 */
public abstract class BinaryExpression  extends ExpressionNode {

    private final ExpressionNode left;
    private final ExpressionNode right;

    public BinaryExpression(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public ExpressionNode getRight() {
        return right;
    }
}
