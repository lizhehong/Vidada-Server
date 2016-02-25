package com.elderbyte.code.dom.expressions;

/**
 * Represents an Operator in an expression
 */
public final class Operator {

    private final boolean isUnary;
    private final String sign;
    private final int precedence;
    private final boolean isLeftAssociative;

    /**
     * Creates a new Operator
     * @param sign
     * @param precedence
     * @param isLeftAssociative
     * @param isUnary
     */
    public Operator(String sign, int precedence, boolean isLeftAssociative, boolean isUnary){
        this.isUnary = isUnary;
        this.sign = sign;
        this.precedence = precedence;
        this.isLeftAssociative = isLeftAssociative;
    }

    public boolean isUnary() {
        return isUnary;
    }

    public String getSign() {
        return sign;
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftAssociative() {
        return isLeftAssociative;
    }


    @Override
    public String toString() {
        return "Operator{" +
            "isUnary=" + isUnary +
            ", sign='" + sign + '\'' +
            ", precedence=" + precedence +
            ", isLeftAssociative=" + isLeftAssociative +
            '}';
    }
}
