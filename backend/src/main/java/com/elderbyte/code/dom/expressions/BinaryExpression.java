package com.elderbyte.code.dom.expressions;

/**
 * Represents a binary expression, which splits the expression tree in two branches.
 * Usually used by Operators,
 * right [x] left
 *
 * @see BinaryOperatorExpression
 */
public abstract class BinaryExpression  extends ExpressionNode {

    private ExpressionNode left;
    private ExpressionNode right;

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

    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
    }
}
