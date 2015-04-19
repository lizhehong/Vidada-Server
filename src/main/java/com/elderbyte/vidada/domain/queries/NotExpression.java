package com.elderbyte.vidada.domain.queries;

/**
 * Negates an expression
 */
public class NotExpression<T> extends Expression<T>  {

    private final String KEYWORD_NOT = "NOT";

    private final Expression<T> expression;

    /**
     * Creates a new Not expression
     * @param expression
     */
    public NotExpression(Expression<T> expression){
        this.expression = expression;
    }

    @Override
    public String code() {
        return KEYWORD_NOT + " " + expression.code();
    }
}
